package com.thfw.base.utils;

import android.content.Context;
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
            Toast.makeText(appContext, charSequence, Toast.LENGTH_SHORT).show();
        }
    }

    public static void show(@StringRes int resId) {
        if (System.currentTimeMillis() - showTime > LIMIT) {
            showTime = System.currentTimeMillis();
            Toast.makeText(appContext, resId, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showLong(CharSequence charSequence) {
        if (System.currentTimeMillis() - showTime > LIMIT) {
            showTime = System.currentTimeMillis();
            Toast.makeText(appContext, charSequence, Toast.LENGTH_LONG).show();
        }
    }

}
