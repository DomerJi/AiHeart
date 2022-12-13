package com.thfw.mobileheart.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ainirobot.coreservice.client.RobotApi;
import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.lhxk.LhXkHelper;

/**
 * Author:pengs
 * Date: 2022/10/17 9:13
 * Describe:Todo
 */
public class AppLifeHelper {

    private static int startedActivityCount;
    private static int foregroundFlag = -1;
    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

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
                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {

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

    public static void onForeground() {
        int flag = ifForeground() ? 1 : 0;
        LogUtil.i("RobotApi.getInstance().isApiConnectedService() -> "+RobotApi.getInstance().isApiConnectedService());
        if (foregroundFlag != flag) {
            foregroundFlag = flag;
            if (foregroundFlag == 1) {
                if (!RobotApi.getInstance().isApiConnectedService()) {
                    LhXkHelper.init();
                }
                LogUtil.d("onForeground = " + 1);
            } else {
                if (RobotApi.getInstance().isApiConnectedService()) {
                    LhXkHelper.disconnectApi();
                }
                LogUtil.d("onForeground = " + 0);
            }

        }
    }

    public static boolean ifForeground() {
        return startedActivityCount > 0;
    }
}
