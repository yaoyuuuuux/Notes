package net.micode.notes.tool;

import android.util.Log;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

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

    private AiApiHelper() {
        client = new OkHttpClient();
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
            json.put("stream", false); // 关闭流式返回，简化处理

            RequestBody body = RequestBody.create(
                json.toString(), MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    callback.onError("网络请求失败: " + e.getMessage());
                }
                @Override public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String resBody = response.body().string();
                        JSONObject resJson = new JSONObject(resBody);
                        String content = resJson.getJSONArray("choices")
                            .getJSONObject(0).getJSONObject("message")
                            .getString("content");
                        callback.onSuccess(content);
                    } else {
                        callback.onError("API 响应错误: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("参数构建异常: " + e.getMessage());
        }
    }
}
