package com.thfw.robotheart.push;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.thfw.base.models.PushMsgModel;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.push.helper.PushHandle;
import com.umeng.message.UTrack;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

/**
 * 点击通知栏消息后，落地的Activity类
 */
public class MyCustomNotificationClickActivity extends Activity {

    public static final String EXTRA_BODY = "body";
    public static final String EXTRA_BODY_TYPE = "body.type";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            String body = intent.getStringExtra(EXTRA_BODY);
            LogUtil.d("MyCustomNotificationClickActivity body : " + body);
            int bodyType = intent.getIntExtra(EXTRA_BODY_TYPE, 0);
            if (body == null) {
                LogUtil.d("MyCustomNotificationClickActivity body == null");
                finish();
                return;
            }
            if (bodyType == 0) {
                try {
                    UMessage message = new UMessage(new JSONObject(body));
                    UTrack.getInstance().trackMsgClick(message);

                    //TODO: 处理通知消息
                    new UmengNotificationClickHandler().handleMessage(this, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    PushMsgModel pushMsgModel = GsonUtil.fromJson(body, PushMsgModel.class);
                    if (pushMsgModel != null) {
                        PushHandle.handleMessage(MyCustomNotificationClickActivity.this, pushMsgModel.toPushModel());
                    }
                } catch (Exception e) {
                    ToastUtil.show("notification error : " + e.getMessage());
                }

            }
        }

        finish();
    }




}
