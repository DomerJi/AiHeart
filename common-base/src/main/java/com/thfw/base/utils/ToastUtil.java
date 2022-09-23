package com.thfw.base.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.StringRes;

/**
 * Created by jishuaipeng on 2021-04-26.
 * 描述: 吐司
 */
public final class ToastUtil {

    private static final int LIMIT = 1500;
    private static Context appContext;
    private static long showTime;

    private ToastUtil() {
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void init(Context appContext) {
        ToastUtil.appContext = appContext.getApplicationContext();
    }

    public static void show(CharSequence charSequence) {
        if (System.currentTimeMillis() - showTime > LIMIT) {
            showTime = System.currentTimeMillis();
            if (isMainThread()) {
                Toast.makeText(appContext, charSequence, Toast.LENGTH_SHORT).show();
            } else {
                showOnMainThread(charSequence, Toast.LENGTH_SHORT);
            }
        }
    }

    public static void show(@StringRes int resId) {
        if (System.currentTimeMillis() - showTime > LIMIT) {
            showTime = System.currentTimeMillis();
            if (isMainThread()) {
                Toast.makeText(appContext, resId, Toast.LENGTH_SHORT).show();
            } else {
                showOnMainThread(resId, Toast.LENGTH_SHORT);
            }
        }
    }

    public static void debugShow(String charSequence) {
        if (LogUtil.isLogEnabled()) {
            if (isMainThread()) {
                Toast.makeText(appContext, charSequence, Toast.LENGTH_SHORT).show();
            } else {
                showOnMainThread(charSequence, Toast.LENGTH_SHORT);
            }
        }
    }

    public static void showLong(CharSequence charSequence) {
        if (System.currentTimeMillis() - showTime > LIMIT) {
            showTime = System.currentTimeMillis();
            if (isMainThread()) {
                Toast.makeText(appContext, charSequence, Toast.LENGTH_LONG).show();
            } else {
                showOnMainThread(charSequence, Toast.LENGTH_LONG);
            }
        }
    }

    private static void showOnMainThread(CharSequence key, int time) {
        HandlerUtil.getMainHandler().post(() -> {
            Toast.makeText(appContext, key, time).show();
        });
    }

    private static void showOnMainThread(int key, int time) {
        HandlerUtil.getMainHandler().post(() -> {
            Toast.makeText(appContext, key, time).show();
        });
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


}
