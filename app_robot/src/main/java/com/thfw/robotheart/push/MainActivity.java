package com.thfw.robotheart.push;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thfw.robotheart.R;
import com.thfw.robotheart.push.helper.PushHelper;
import com.thfw.robotheart.push.tester.UPushAlias;
import com.thfw.robotheart.push.tester.UPushNotification;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushRegisterCallback;


/**
 * 应用首页Activity类
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_um_main);
        if (hasAgreedAgreement()) {
            PushAgent.getInstance(this).onAppStart();
            String deviceToken = PushAgent.getInstance(this).getRegistrationId();
            TextView token = findViewById(R.id.tv_device_token);
            token.setText(deviceToken);
        } else {
            showAgreementDialog();
        }
    }

    private boolean hasAgreedAgreement() {
        return MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
    }

    private void showAgreementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.agreement_title);
        builder.setMessage(R.string.agreement_msg);
        builder.setPositiveButton(R.string.agreement_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //用户点击隐私协议同意按钮后，初始化PushSDK
                MyPreferences.getInstance(getApplicationContext()).setAgreePrivacyAgreement(true);
                PushHelper.init(getApplicationContext());
                PushAgent.getInstance(getApplicationContext()).setNotificationOnForeground(true);
                PushAgent.getInstance(getApplicationContext()).register(new UPushRegisterCallback() {
                    @Override
                    public void onSuccess(final String deviceToken) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView token = findViewById(R.id.tv_device_token);
                                token.setText(deviceToken);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String code, String msg) {
                        Log.d("MainActivity", "code:" + code + " msg:" + msg);
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.agreement_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    /**
     * 推送消息测试
     */
    public void sendMsg(View view) {
        UPushNotification.send("来消息了", "标题", "这是内容，这是内容...");
    }

    /**
     * 设置别名，设置后可通过别名推送消息
     */
    public void setAlias(View view) {
        UPushAlias.test(this);
    }

}
