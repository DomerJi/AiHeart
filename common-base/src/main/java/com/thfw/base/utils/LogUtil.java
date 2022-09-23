package com.thfw.base.utils;

import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.Setting;


/**
 * Created by jishuaipeng on 2021-04-26.
 * Description:日志打印工具类
 */
public class LogUtil {

    private static final String TAG = "THFW_HEART";
    // 用于存储日志的调试开关
    private static final String KEY_LOG_ENABLE = "key.log.enabled";
    private static boolean isLogEnabled = true;
    private static String versionName = "1.0.0";

    public static boolean switchLogEnable() {
        return switchLogEnable(!isLogSpEnable());
    }

    public static boolean switchLogEnable(boolean logEnabled) {
        isLogEnabled = logEnabled;
        SharePreferenceUtil.setBoolean(KEY_LOG_ENABLE, isLogEnabled);
        Setting.setShowLog(isLogEnabled);
        return isLogEnabled;
    }

    public static boolean isLogSpEnable() {
        return SharePreferenceUtil.getBoolean(KEY_LOG_ENABLE, false);
    }

    public static boolean isLogEnabled() {
        return isLogEnabled;
    }

    public static void setLogEnabled(boolean logEnabled) {
        isLogEnabled = logEnabled;
        Setting.setShowLog(isLogEnabled);
    }

    /**
     * 一般日志
     *
     * @param msg 内容
     */
    public static void v(String msg) {
        v(null, msg);
    }

    /**
     * 一般日志
     *
     * @param tag
     * @param msg 内容
     */
    public static void v(String tag, String msg) {
        if (isLogEnabled()) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            msg = getLogMsg(msg);
            Log.v(tag, msg);
        }
    }

    /**
     * 调试日志
     *
     * @param msg 内容
     */
    public static void d(String msg) {
        d(null, msg);
    }

    /**
     * 调试日志
     *
     * @param tag
     * @param msg 内容
     */
    public static void d(String tag, String msg) {
        if (isLogEnabled()) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            msg = getLogMsg(msg);
            Log.d(tag, msg);
        }
    }

    /**
     * 提示日志
     *
     * @param msg 内容
     */
    public static void i(String msg) {
        i(null, msg);
    }

    /**
     * 提示日志
     *
     * @param tag
     * @param msg 内容
     */
    public static void i(String tag, String msg) {
        if (isLogEnabled()) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            msg = getLogMsg(msg);
            Log.i(tag, msg);
        }
    }

    /**
     * 警告日志
     *
     * @param msg 内容
     */
    public static void w(String msg) {
        w(null, msg);
    }

    /**
     * 警告日志
     *
     * @param tag
     * @param msg 内容
     */
    public static void w(String tag, String msg) {
        if (isLogEnabled()) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            msg = getLogMsg(msg);
            Log.w(tag, msg);

        }
    }

    /**
     * 错误日志
     *
     * @param msg 内容
     */
    public static void e(String msg) {
        e(null, msg);
    }

    /**
     * 错误日志
     *
     * @param tag
     * @param msg 内容
     */
    public static void e(String tag, String msg) {
        if (isLogEnabled()) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            msg = getLogMsg(msg);
            Log.e(tag, msg);

        }
    }

    /**
     * 错误日志
     *
     * @param tag
     * @param throwable 内容
     */
    public static void e(String tag, Throwable throwable) {
        e(tag, throwable.getMessage());
    }

    /**
     * 错误日志
     *
     * @param tag
     * @param msg       内容
     * @param throwable 内容
     */
    public static void e(String tag, String msg, Throwable throwable) {
        e(tag, msg + " : " + throwable.getMessage());
    }

    /**
     * 错误日志
     *
     * @param throwable 内容
     */
    public static void e(Throwable throwable) {
        e(null, throwable);
    }


    private static String getLogMsg(String msg) {
        String versionName = getVersionName();
        if (TextUtils.isEmpty(versionName)) {
            return msg;
        } else {
            return versionName + ": " + msg;
        }
    }

    private static String getVersionName() {
        if (!TextUtils.isEmpty(versionName)) {
            return versionName;
        }
        return versionName;
    }


}
