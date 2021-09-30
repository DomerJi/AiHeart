package com.thfw.base.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jishuaipeng on 2021-04-26.
 * 描述: 吐司
 */
public final class ToastUtil {

    private static Context appContext;

    private ToastUtil() {
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void init(Context appContext) {
        ToastUtil.appContext = appContext.getApplicationContext();
    }

    public static void show(CharSequence charSequence) {
        Toast.makeText(appContext, charSequence, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(CharSequence charSequence) {
        Toast.makeText(appContext, charSequence, Toast.LENGTH_LONG).show();
    }

}
