package com.thfw.robotheart.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * 自定义接收并处理友盟推送消息的服务类
 */
public class MyCustomMessageService extends UmengMessageService {
    private static final String TAG = "MyCustom:IPushAidlImp";

    @Override
    public void onMessage(Context context, Intent intent) {
        Log.i(TAG, "onMessage");
        try {
            String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            Log.i(TAG, "onMessage: body =  " + body);
            onMsgReciver(body);
            UMessage message = new UMessage(new JSONObject(body));
            if (UMessage.DISPLAY_TYPE_NOTIFICATION.equals(message.display_type)) {
                //处理通知消息
//                handleNotificationMessage(message);
            } else if (UMessage.DISPLAY_TYPE_CUSTOM.equals(message.display_type)) {
                //TODO: 处理自定义消息
//                handleCustomMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onMsgReciver(String str) {
        Log.d(TAG, "onMsgReciver(" + str + ")");
        Log.d(TAG, "onMsgReciver callbackInterfaces size = "
                + (CallBack.getCallbackInterfaces() == null ? -1 : CallBack.getCallbackInterfaces().size()));
        int code = 0;
        for (ICallbackInterface callbackInterface : CallBack.getCallbackInterfaces()) {
            try {
                callbackInterface.onMsgReciver(str);
                code = 1;
            } catch (Exception e) {
                Log.e(TAG, "e -> " + e.getMessage());
            }
        }
        return code == 1;
    }


}
