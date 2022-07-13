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

import com.thfw.base.ContextApp;
import com.thfw.base.base.MsgType;
import com.thfw.base.models.PushMsgModel;
import com.thfw.base.utils.GsonUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.util.MsgCountManager;
import com.thfw.ui.utils.UrgeUtil;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengMessageService;
import com.umeng.message.UmengNotificationReceiver;
import com.umeng.message.common.UPushNotificationChannel;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
            Log.i(TAG, "onMessage: body =  " + body);
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

    /**
     * 自定义消息
     * TODO 解析验证
     *
     * @param message
     */
    private void handleCustomMessage(UMessage message) {
        String json = message.getRaw().toString();
        Log.i(TAG, "handleCustomMessage: " + json);
        PushMsgModel pushMsgModel = new PushMsgModel();
        try {
            JSONObject mMsg = new JSONObject(json);
            if (mMsg != null) {
                String msg_id = mMsg.optString("msg_id");
                pushMsgModel.setMsgId(msg_id);
                String display_type = mMsg.optString("display_type");
                pushMsgModel.setDisplayType(display_type);
                JSONObject mBody = mMsg.optJSONObject("body");
                if (mBody != null) {
                    String mCustomJson = mBody.optString("custom");
                    Log.i(TAG, "handleCustomMessage: mCustomJson = " + mCustomJson.toString());
                    if (mCustomJson != null) {
                        JSONObject mCustomObject = new JSONObject(mCustomJson);
                        if (mCustomObject == null) {
                            Log.i(TAG, "handleCustomMessage: mCustomObject is null");
                            return;
                        }
                        String title = mCustomObject.optString("title");
                        String content = mCustomObject.optString("content");
                        int id = mCustomObject.optInt("id", 0);
                        String turnPage = mCustomObject.optString("turn_page");
                        int msgType = mCustomObject.optInt("msg_type");
                        pushMsgModel.setType(msgType);
                        pushMsgModel.setId(id);
                        pushMsgModel.setTitle(title);
                        pushMsgModel.setContent(content);
                        pushMsgModel.setTurnPage(turnPage);
                        if (msgType == 1) {
                            if (!MsgCountManager.getInstance().hasTaskMsgId(msg_id)) {
                                MsgCountManager.getInstance().addNumTask();
                            }
                        } else {
                            // 机器人只处理机器人的消息
                            if (ContextApp.getDeviceType() == ContextApp.DeviceType.ROBOT
                                    && MsgType.isRobotMsg(msgType)) {
                                if (!MsgCountManager.getInstance().hasSystemMsgId(msg_id)) {
                                    MsgCountManager.getInstance().addNumSystem();
                                }
                            } else {
                                // 非机器人只处理非机器人的消息，
                                if (MsgType.isMobileMsg(msgType)) {
                                    if (!MsgCountManager.getInstance().hasSystemMsgId(msg_id)) {
                                        MsgCountManager.getInstance().addNumSystem();
                                    }
                                }
                            }

                        }
                        // 催促消息
                        if (MsgType.isUrge(msgType)) {
                            UrgeUtil.notify(new HashMap<>());
                        } else {
                            handleCustomNotificationMessage(pushMsgModel, message);
                        }
                    }
                }
                Log.i(TAG, "pushMsgModel：" + pushMsgModel.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleCustomNotificationMessage(PushMsgModel msg, UMessage message) {
        Log.i(TAG, "handleCustomNotificationMessage：=======================");
        Notification.Builder builder;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = UPushNotificationChannel.PRIMARY_CHANNEL;
            String channelName = PushAgent.getInstance(this).getNotificationChannelName();
            NotificationChannel channel = manager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, channelName,
                        msg.isImportant() ? NotificationManager.IMPORTANCE_HIGH :
                                NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            } else {
                channel.setImportance(msg.isImportant() ? NotificationManager.IMPORTANCE_HIGH :
                        NotificationManager.IMPORTANCE_DEFAULT);
            }
            builder = new Notification.Builder(this, UPushNotificationChannel.PRIMARY_CHANNEL);
        } else {
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle(msg.getTitle())
                .setContentText(msg.getContent())
                .setTicker(msg.getTicker())
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        Notification notification = builder.getNotification();
        PendingIntent clickIntent = getClickPendingIntent(this, msg);
        notification.deleteIntent = getDismissPendingIntent(this, msg);
        notification.contentIntent = clickIntent;
        manager.notify((int) SystemClock.elapsedRealtime(), notification);
        UTrack.getInstance().trackMsgShow(message, notification);
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
        intent.putExtra(MyCustomNotificationClickActivity.EXTRA_BODY_TYPE, 0);
        intent.putExtra(MyCustomNotificationClickActivity.EXTRA_BODY, msg.getRaw().toString());
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getActivity(context, (int) (System.currentTimeMillis()), intent, flags);
    }

    public PendingIntent getClickPendingIntent(Context context, PushMsgModel msg) {
        Intent intent = new Intent(context, MyCustomNotificationClickActivity.class);
        intent.setPackage(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MyCustomNotificationClickActivity.EXTRA_BODY_TYPE, 1);
        intent.putExtra(MyCustomNotificationClickActivity.EXTRA_BODY, GsonUtil.toJson(msg));
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getActivity(context, (int) (System.currentTimeMillis()), intent, flags);
    }


    public PendingIntent getDismissPendingIntent(Context context, PushMsgModel msg) {
        Intent var3 = new Intent();
        var3.setClass(context, UmengNotificationReceiver.class);
        var3.putExtra("MSG", GsonUtil.toJson(msg));
        var3.putExtra("ACTION", 11);
        var3.putExtra("MESSAGE_ID", msg.getMsgId());
        var3.putExtra("NOTIFICATION_ID", System.currentTimeMillis());
        var3.putExtra("TASK_ID", msg.getMsgId());
        int var4 = 268435456;
        if (Build.VERSION.SDK_INT >= 23) {
            var4 |= 67108864;
        }

        PendingIntent var5 = PendingIntent.getBroadcast(context, (int) (System.currentTimeMillis() + 1L), var3, var4);
        return var5;
    }

    public PendingIntent getDismissPendingIntent(Context context, UMessage msg) {
        return new UmengMessageHandler().getDismissPendingIntent(context, msg);
    }

}
