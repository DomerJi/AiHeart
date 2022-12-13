package com.thfw.ui.widget;

import android.os.Build;

/**
 * Author:pengs
 * Date: 2022/10/14 11:54
 * Describe:Todo
 */
public class DeviceUtil {


    /**
     * 是否是猎户星空 CM-GB03D
     *
     * @return
     */
    public static boolean isLhXk_CM_GB03D() {
        return "CM-GB03D".equals(Build.MODEL);
    }

}
