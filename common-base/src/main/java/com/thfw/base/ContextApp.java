package com.thfw.base;

import android.content.Context;

import com.thfw.base.face.WebViewListener;
import com.thfw.base.utils.SharePreferenceUtil;

/**
 * Author:pengs
 * Date: 2021/11/23 11:20
 * Describe:Todo
 */
public final class ContextApp {

    private static final String KEY_DEVICES_TYPE = "key_devices_type";
    private static Context context;
    private static int deviceType = DeviceType.MOBILE;
    private static int contentType = ContentType.QIANG_JUN;

    private ContextApp() {

    }

    public static String getContentType() {
        return String.valueOf(contentType);
    }

    public static int getDeviceType() {
        if (deviceType <= 0) {
            deviceType = SharePreferenceUtil.getInt(KEY_DEVICES_TYPE, deviceType);
        }
        return deviceType;
    }

    public static void setContentType(int contentType) {
        ContextApp.contentType = contentType;
    }

    public static void setDeviceType(int deviceType) {
        ContextApp.deviceType = deviceType;
        SharePreferenceUtil.setInt(KEY_DEVICES_TYPE, deviceType);
    }

    public static String getDeviceTypeStr() {
        return String.valueOf(getDeviceType());
    }


    public static WebViewListener webViewListener;

    public static void setWebViewListener(WebViewListener webViewListener) {
        ContextApp.webViewListener = webViewListener;
    }

    public static WebViewListener getWebViewListener() {
        return webViewListener;
    }

    public static void init(Context context) {
        ContextApp.context = context.getApplicationContext();
    }

    public static Context get() {
        return ContextApp.context;
    }

    public interface DeviceType {
        /**
         * 手机
         */
        int MOBILE = 1;

        /**
         * 平板
         */
        int PAD = 2;

        /**
         * 机器人(台面)
         */
        int ROBOT = 3;

        /**
         * 第三方（4a,4b,4c,4d....）
         */

    }

    public interface ContentType {
        /**
         * 强军
         */
        int QIANG_JUN = 1;

        /**
         * 大学
         */
        int BIT_SCHOOL = 2;

        /**
         * 中小学
         */
        int SMALL_SCHOOL = 3;

    }

}
