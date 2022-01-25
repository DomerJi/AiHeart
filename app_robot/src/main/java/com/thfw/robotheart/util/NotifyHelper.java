package com.thfw.robotheart.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.thfw.robotheart.R;


public class NotifyHelper {
    private static NotifyHelper instance;
    private Context mContext;

    private NotifyHelper(Context context) {
        mContext = context;
    }

    public static NotifyHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotifyHelper(context);
        }
        return instance;
    }

    public void CreateChannel(String channel_id, CharSequence channel_name, String description) {
        //8.0以上版本通知适配
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * 返回一个前台通知
     *
     * @param channel_id  通知渠道id，注意8.0创建通知的时候渠道id与此要匹配
     * @param audioModel  数据对象
     * @param remoteViews 自定义通知样式的对象，但是与View不同，不提供findViewById方法，详细建议看看源码和官方文档
     * @return
     */

    public Notification createForeNotification(String channel_id, AudioModel audioModel, RemoteViews remoteViews) {
        Intent intent = new Intent(mContext, null);
        PendingIntent mainIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channel_id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomBigContentView(remoteViews)
                .setContentIntent(mainIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }
}

