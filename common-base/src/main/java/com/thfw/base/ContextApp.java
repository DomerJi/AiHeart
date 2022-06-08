package com.thfw.base;

import android.content.Context;

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

    private ContextApp() {

    }

    public static int getDeviceType() {
        if (deviceType <= 0) {
            deviceType = SharePreferenceUtil.getInt(KEY_DEVICES_TYPE, deviceType);
        }
        return deviceType;
    }

    public static void setDeviceType(int deviceType) {
        ContextApp.deviceType = deviceType;
        SharePreferenceUtil.setInt(KEY_DEVICES_TYPE, deviceType);
    }

    public static String getDeviceTypeStr() {
        return String.valueOf(getDeviceType());
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
}
