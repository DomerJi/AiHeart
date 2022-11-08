package com.thfw.robotheart.push;

import android.app.Application;

import com.thfw.base.utils.MyPreferences;
import com.thfw.robotheart.push.helper.PushHelper;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;


/**
 * 应用程序类
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initUmengSDK();
    }

    /**
     * 初始化友盟SDK
     */
    private void initUmengSDK() {
        //日志开关
        UMConfigure.setLogEnabled(true);
        //预初始化
        PushHelper.preInit(this);
        //是否同意隐私政策
        boolean agreed = MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
        if (!agreed) {
            return;
        }
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

}
