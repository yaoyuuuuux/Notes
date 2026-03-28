
/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.gtask.remote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import net.micode.notes.R;
import net.micode.notes.ui.NotesListActivity;
import net.micode.notes.ui.NotesPreferenceActivity;

public class GTaskASyncTask extends AsyncTask<Void, String, Integer> {

    private static int GTASK_SYNC_NOTIFICATION_ID = 5234235;

    @Override
    protected Integer doInBackground(Void... params) {
        // 这里是你原来的后台执行逻辑
        // 如果你不小心全删了，你需要从原版源码里找回这个方法的代码
        return null; // 或者返回你实际需要的 Integer 值
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    private Context mContext;

    private NotificationManager mNotifiManager;

    private GTaskManager mTaskManager;

    private OnCompleteListener mOnCompleteListener;

    public GTaskASyncTask(Context context, OnCompleteListener listener) {
        mContext = context;
        mOnCompleteListener = listener;
        mNotifiManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mTaskManager = GTaskManager.getInstance();
    }

    public void cancelSync() {
        mTaskManager.cancelSync();
    }

    public void publishProgess(String message) {
        publishProgress(new String[]{
                message
        });
    }

    private void showNotification(int tickerId, String content) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "gtask_sync_channel";

        // 1. 创建点击通知后跳转的 Intent
        PendingIntent pendingIntent;
        // 注意：Android 12 (API 31) 以上要求 PendingIntent 必须指定 FLAG_IMMUTABLE 或 FLAG_MUTABLE
        int pendingIntentFlags = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ?
                PendingIntent.FLAG_IMMUTABLE : 0;

        if (tickerId != R.string.ticker_success) {
            pendingIntent = PendingIntent.getActivity(mContext, 0,
                    new android.content.Intent(mContext, net.micode.notes.ui.NotesPreferenceActivity.class), pendingIntentFlags);
        } else {
            pendingIntent = PendingIntent.getActivity(mContext, 0,
                    new android.content.Intent(mContext, net.micode.notes.ui.NotesListActivity.class), pendingIntentFlags);
        }

        // 2. 针对 Android 8.0 (API 26) 及以上版本，必须创建 NotificationChannel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    channelId,
                    "GTask Sync", // 通知渠道的名称
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }

        // 3. 使用 Builder 构建通知
        android.app.Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new android.app.Notification.Builder(mContext, channelId);
        } else {
            builder = new android.app.Notification.Builder(mContext);
        }

        builder.setSmallIcon(R.drawable.icon_app) // 设置小图标
                .setTicker(mContext.getString(tickerId)) // 设置状态栏提示文本
                .setWhen(System.currentTimeMillis())
                .setContentTitle(mContext.getString(R.string.app_name)) // 标题
                .setContentText(content) // 内容
                .setContentIntent(pendingIntent) // 点击跳转
                .setAutoCancel(true); // 点击后自动消失

        android.app.Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }

        // 4. 发送通知
        notificationManager.notify(GTASK_SYNC_NOTIFICATION_ID, notification);
    }
}