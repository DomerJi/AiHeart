package com.thfw.robotheart.constants;

import android.Manifest;

import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.R;

/**
 * Author:pengs
 * Date: 2021/12/2 17:02
 * Describe:Todo
 */
public class UIConfig {
    public static final int LEFT_TAB_MAX_TEXTSIZE = 22;
    public static final int LEFT_TAB_MIN_TEXTSIZE = 18;
    public static final int LEFT_TAB_CHILD_MAX_TEXTSIZE = 20;
    public static final int LEFT_TAB_CHILD_MIN_TEXTSIZE = 18;
    public static final String COLOR_HOUR = "#FD9D56";
    public static final String COLOR_GRREN = "#00A871";
    public static final String COLLECTED = "已收藏";
    public static final String COLLECTED_UN = "已取消收藏";
    public static String[] NEEDED_PERMISSION = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final String getUserAgent() {
        return com.google.android.exoplayer2.util.Util.getUserAgent(MyApplication.getApp(), MyApplication.getApp().getResources().getString(R.string.app_name));
    }
}
