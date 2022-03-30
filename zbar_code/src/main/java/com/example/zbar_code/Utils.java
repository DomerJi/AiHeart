package com.example.zbar_code;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isCongCongRobot() {
        Log.i("Utils", "Device Type:" + Build.BRAND + " " + Build.DEVICE);
        if (Build.DEVICE.equals("kar_pro") || Build.DEVICE.equals("x10")) {
            return true;
        }
        return false;
    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isHuaweiPad() {
        if (Build.BRAND.equals("HUAWEI") && Build.DEVICE.equals("HWBAH3-H")) {
            return true;
        }

        return false;
    }


    public static boolean hasFlash(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


}
