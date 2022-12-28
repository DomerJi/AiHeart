package com.thfw.ui.widget;

import android.os.Build;
import android.util.Log;

/**
 * Author:pengs
 * Date: 2022/10/14 11:54
 * Describe:Todo
 */
public class DeviceUtil {


    /**
     * 是否是猎户星空 CM-GB03D [豹大屏]
     *
     * @return
     */
    public static boolean isLhXk_CM_GB03D() {
        return "CM-GB03D".equals(Build.MODEL);
    }

    /**
     * 是否是猎户星空 OS-R-SD01B 豹小密 mini
     *
     * @return
     */
    public static boolean isLhXk_OS_R_SD01B() {
        Log.i("Build.MODEL", "Build.MODEL =" + Build.MODEL);
        return "OS-R-SD01B".equals(Build.MODEL);
    }

}
