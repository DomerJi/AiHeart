package com.thfw.ui.widget;

import android.os.Build;

/**
 * Author:pengs
 * Date: 2022/10/14 11:54
 * Describe:Todo
 */
public class DeviceUtil {

    private static final Boolean isLhXk_CM_GB03D = "CM-GB03D".equals(Build.MODEL);
    private static final Boolean isLhXk_OS_R_SD01B = "OS-R-SD01B".equals(Build.MODEL);

    /**
     * 是否是猎户星空 CM-GB03D [豹大屏]
     *
     * @return
     */
    public static boolean isLhXk_CM_GB03D() {
        return isLhXk_CM_GB03D;
    }

    /**
     * 是否是猎户星空 OS-R-SD01B 豹小密 mini
     *
     * @return
     */
    public static boolean isLhXk_OS_R_SD01B() {
        return isLhXk_OS_R_SD01B;
    }

}
