package com.thfw.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePreferenceUtil {

    public static final String SP_NAME = "hmhd_sp";

    private static Context sContext;

    public static void init(Context appContext) {
        if (appContext instanceof Activity) {
            LogUtil.e(new RuntimeException("必须传入Application的Context!!"));
            return;
        }
        sContext = appContext;
    }

    public static SharedPreferences getSharedPreferences() {
        if (sContext == null) {
            LogUtil.e("SharePreferenceUtil未初始化！！");
            return null;
        }
        return sContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(String key, boolean dValue) {
        boolean result = getSharedPreferences().getBoolean(key, dValue);
        return result;
    }

    public static void setBoolean(String key, boolean value) {
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String getString(String key, String dValue) {
        String result = getSharedPreferences().getString(key, dValue);
        return result;
    }

    public static void setString(String key, String value) {
        Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static long getLong(String key, long dValue) {
        long result = getSharedPreferences().getLong(key, dValue);
        return result;
    }

    public static void setLong(String key, long value) {
        Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static int getInt(String key, int dValue) {
        int result = getSharedPreferences().getInt(key, dValue);
        return result;
    }

    public static void setInt(String key, int value) {
        Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void removeKey(String key) {
        Editor editor = getSharedPreferences().edit();
        editor.remove(key);
        editor.commit();
    }

}
