package com.thfw.base.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Author:pengs
 * Date: 2022/5/23 12:12
 * Describe:Todo
 */
public class HandlerUtil {

    private static Handler mMainHandler;

    public static Handler getMainHandler() {
        if (mMainHandler == null) {
            synchronized (HandlerUtil.class) {
                if (mMainHandler == null) {
                    mMainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mMainHandler;
    }
}
