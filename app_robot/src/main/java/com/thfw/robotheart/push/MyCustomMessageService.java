package com.thfw.robotheart.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.thfw.robotheart.R;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengMessageService;
import com.umeng.message.common.UPushNotificationChannel;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * 自定义接收并处理友盟推送消息的服务类
 */
public class MyCustomMessageService extends UmengMessageService {
    private static final String TAG = "MyCustomMessageService";

    @Override
    public void onMessage(Context context, Intent intent) {
        Log.i(TAG, "onMessage");
        try {
            String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            UMessage message = new UMessage(new JSONObject(body));
            if (UMessage.DISPLAY_TYPE_NOTIFICATION.equals(message.display_type)) {
                //处理通知消息
                handleNotificationMessage(message);
            } else if (UMessage.DISPLAY_TYPE_CUSTOM.equals(message.display_type)) {
                //TODO: 处理自定义消息
                handleCustomMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCustomMessage(UMessage message) {
        Log.i(TAG, "handleCustomMessage: " + message.getRaw().toString());
    }

    private void handleNotificationMessage(UMessage msg) {
        Notification.Builder builder;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = UPushNotificationChannel.PRIMARY_CHANNEL;
            String channelName = PushAgent.getInstance(this).getNotificationChannelName();
            NotificationChannel channel = manager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            builder = new Notification.Builder(this, UPushNotificationChannel.PRIMARY_CHANNEL);
        } else {
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle(msg.title)
                .setContentText(msg.text)
                .setTicker(msg.ticker)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        Notification notification = builder.getNotification();
        PendingIntent clickIntent = getClickPendingIntent(this, msg);
        notification.deleteIntent = getDismissPendingIntent(this, msg);
        notification.contentIntent = clickIntent;
        manager.notify((int) SystemClock.elapsedRealtime(), notification);
        UTrack.getInstance().trackMsgShow(msg, notification);
    }

    public PendingIntent getClickPendingIntent(Context context, UMessage msg) {
        Intent intent = new Intent(context, MyCustomNotificationClickActivity.class);
        intent.setPackage(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MyCustomNotificationClickActivity.EXTRA_BODY, msg.getRaw().toString());
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getActivity(context, (int) (System.currentTimeMillis()), intent, flags);
    }

    public PendingIntent getDismissPendingIntent(Context context, UMessage msg) {
        return new UmengMessageHandler().getDismissPendingIntent(context, msg);
    }

}
