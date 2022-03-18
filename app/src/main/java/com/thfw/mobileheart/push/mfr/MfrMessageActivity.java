package com.thfw.mobileheart.push.mfr;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.thfw.mobileheart.R;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;


/**
 * 厂商通道配置托管启动的Activity
 * 如点击小米、华为、OPPO、vivo等系统通道的通知消息，启动的Activity类
 */
public class MfrMessageActivity extends UmengNotifyClickActivity {

    private static final String TAG = "MfrMessageActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.mfr_message_layout);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Log.d(TAG, "bundle: " + bundle);
        }
        final String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.d(TAG, "body: " + body);
        if (!TextUtils.isEmpty(body)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.tv)).setText(body);
                }
            });
        }
    }
}
