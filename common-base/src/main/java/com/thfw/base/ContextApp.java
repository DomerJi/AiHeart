package com.thfw.base;

import android.content.Context;

/**
 * Author:pengs
 * Date: 2021/11/23 11:20
 * Describe:Todo
 */
public final class ContextApp {
    private static Context context;

    private ContextApp() {

    }

    public static void init(Context context) {
        ContextApp.context = context.getApplicationContext();
    }

    public static Context get() {
        return ContextApp.context;
    }
}
