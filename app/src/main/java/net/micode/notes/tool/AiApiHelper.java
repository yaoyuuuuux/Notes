package net.micode.notes.tool;

import android.util.Log;
import net.micode.notes.BuildConfig;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AiApiHelper {
    private static final String TAG = "AiApiHelper";
    // 填入您在智谱开放平台获取的 API Key
    private static final String API_KEY = BuildConfig.ZHIPU_API_KEY;
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String MODEL_NAME = "GLM-4-Flash";

    private final OkHttpClient client;
    private static AiApiHelper sInstance;

    public static synchronized AiApiHelper getInstance() {
        if (sInstance == null) sInstance = new AiApiHelper();
        return sInstance;
    }

//考虑到 AI 模型响应可能较慢（需要网络请求和 AI 处理时间），
//将连接、读取、写入超时时间均设置为 60 秒，避免请求过早超时
    private AiApiHelper() {
        // AI responses can be slow, so we increase timeouts to 60 seconds
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public interface AiCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    public void sendPrompt(String userText, AiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("model", MODEL_NAME);
            JSONArray messages = new JSONArray();
            JSONObject msg = new JSONObject();
            msg.put("role", "user");
            msg.put("content", userText);
            messages.put(msg);
            json.put("messages", messages);
            json.put("stream", false);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, json.toString());

            Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Request failed: " + e.getMessage());
                    callback.onError(e.getMessage());
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            String errorMsg = "Unexpected code " + response;
                            Log.e(TAG, errorMsg);
                            callback.onError(errorMsg);
                            return;
                        }

                        if (responseBody == null) {
                            callback.onError("Empty response body");
                            return;
                        }

                        String responseData = responseBody.string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String result = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        callback.onSuccess(result);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        callback.onError(e.getMessage());
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "JSON construction error: " + e.getMessage());
            callback.onError(e.getMessage());
        }
    }
}
