package com.thfw.util;

import android.content.Context;

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

    public static void init(Context context) {
        ContextApp.context = context.getApplicationContext();
        ToastUtil.init(context);
        SharePreferenceUtil.init(context);
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
         * 机器人
         */
        int ROBOT = 3;

    }
}
