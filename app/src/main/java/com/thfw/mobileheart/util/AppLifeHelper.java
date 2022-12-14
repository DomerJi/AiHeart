package com.thfw.mobileheart.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ainirobot.coreservice.client.RobotApi;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.ui.widget.DeviceUtil;

import java.lang.ref.WeakReference;

/**
 * Author:pengs
 * Date: 2022/10/17 9:13
 * Describe:Todo
 */
public class AppLifeHelper {

    private static int startedActivityCount;
    private static int foregroundFlag = -1;
    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;
    private static WeakReference<Activity> mTopActivity;

    public static Activity getTopActivity() {
        if (ifForeground() && mTopActivity != null) {
            return mTopActivity.get();
        }
        return null;
    }

    public static void initActivityLifecycle(Application application) {
        if (activityLifecycleCallbacks == null) {
            activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                    startedActivityCount++;
                    onForeground();

                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {
                    mTopActivity = new WeakReference<>(activity);
                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {
                    mTopActivity = null;
                    DialogFactory.dismissSpeech();
                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {
                    startedActivityCount--;
                    onForeground();
                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {

                }
            };
        }
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private static final long ONE_NS_TIME = 1000 * 1000 * 1000;

    public static void onForeground() {
        int flag = ifForeground() ? 1 : 0;
        LogUtil.i("RobotApi.getInstance().isApiConnectedService() -> " + RobotApi.getInstance().isApiConnectedService());
        if (foregroundFlag != flag) {
            foregroundFlag = flag;
            LogUtil.d("onForeground = " + ifForeground());
            if (DeviceUtil.isLhXk_CM_GB03D()) {

                if (foregroundFlag == 1) {
                    if (!RobotApi.getInstance().isApiConnectedService()) {
                        if (System.nanoTime() < 180 * ONE_NS_TIME) {
                            HandlerUtil.getMainHandler().postDelayed(() -> LhXkHelper.init(), 1200);
                        } else {
                            LhXkHelper.init();
                        }

                    }
                    LogUtil.d("onForeground = " + 1);
                } else {
                    if (RobotApi.getInstance().isApiConnectedService()) {
                        LhXkHelper.disconnectApi();
                    }
                    MyApplication.kill();
                }

            }

        }
    }

    public static boolean ifForeground() {
        return startedActivityCount > 0;
    }
}
