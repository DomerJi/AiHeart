package com.thfw.robotheart.push;

import android.app.Application;
import android.util.Log;

import com.thfw.robotheart.push.helper.PushHelper;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushRegisterCallback;


/**
 * 应用程序类
 */
public class MyApplication extends Application {

    private static MyApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initUmengSDK();
    }

    public static MyApplication getApp() {
        return application;
    }

    /**
     * 初始化友盟SDK
     */
    public void initUmengSDK() {
        //日志开关
        UMConfigure.setLogEnabled(true);
        //预初始化
        PushHelper.preInit(this);
        boolean isMainProcess = UMUtils.isMainProgress(this);
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushHelper.init(getApplicationContext());
                }
            }).start();
        } else {
            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
            PushHelper.init(getApplicationContext());
        }
    }

    /**
     * 登录状态下，即点击隐私协议同意按钮后，初始化PushSDK
     */
    public static void agreementAfterInitUmeng() {
        MyPreferences.getInstance(MyApplication.getApp()).setAgreePrivacyAgreement(true);
        PushHelper.init(MyApplication.getApp());
        PushAgent.getInstance(MyApplication.getApp()).register(new UPushRegisterCallback() {
            @Override
            public void onSuccess(final String deviceToken) {
                Log.d("","deviceToken = " + deviceToken);
            }

            @Override
            public void onFailure(String code, String msg) {
                Log.d("MainActivity", "code:" + code + " msg:" + msg);
            }
        });
    }

}
