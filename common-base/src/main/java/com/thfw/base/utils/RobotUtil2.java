package com.thfw.base.utils;

import android.os.Build;

import com.thfw.base.ContextApp;

/**
 * Author:pengs
 * Date: 2022/7/19 8:39
 * Describe:是否安装在机器人设备上
 * 串口通信相关【严格校验，关于串口通信需要是否安装在 桌面1.0机器人上】
 */
public class RobotUtil2 {

    private static final String TAG = RobotUtil2.class.getSimpleName();
    // ACE 阵列mic唤醒 开关控制
    public static final String KEY_ACE_OPEN = "ace_open";
    public static final String KEY_ROBOT_ACTION = "robot_action_open";
    private static int installRobot = -1;
    private static int enableMic = -1; // 是否启用阵列mic

    /**
     * @return 是否系统应用
     */
    private static boolean isSystemApp() {
        return Util.isSystemApp(ContextApp.get().getPackageName()) && isBuildMsg();
    }

    /**
     * 第一个桌面机器人
     *
     * @return
     */
    private static boolean isBuildMsg() {
        return "TH-ROBOT".equals(Build.PRODUCT) || "TH-RK3399".equals(Build.DEVICE)
                || "RK3399-TH".equals(Build.MODEL) || "ROCKCHIP-TH".equals(Build.BRAND);
    }

    /**
     * 是否桌面1.0机器人
     *
     * @return
     */
    public static boolean isInstallRobot() {
        if (installRobot == -1) {
            installRobot = isSystemApp() ? 1 : 0;
        }
        return installRobot == 1;
    }

    public static void resetEnableMic() {
        enableMic = SharePreferenceUtil.getBoolean(KEY_ACE_OPEN, isInstallRobot()) ? 1 : 0;
    }

    /**
     * 是否启用阵列mic
     *
     * @return
     */
    public static boolean isEnableMic() {
        if (enableMic == -1) {
            enableMic = SharePreferenceUtil.getBoolean(KEY_ACE_OPEN, isInstallRobot()) ? 1 : 0;
        }
        return enableMic == 1;
    }

}
