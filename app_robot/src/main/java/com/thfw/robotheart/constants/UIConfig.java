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
    public static final float LEFT_TAB_MAX_TEXTSIZE = 21f;
    public static final float LEFT_TAB_MIN_TEXTSIZE = 19f;
    public static final float LEFT_TAB_CHILD_MAX_TEXTSIZE = 19f;
    public static final float LEFT_TAB_CHILD_MIN_TEXTSIZE = 17f;
    public static final float LEFT_TAB_CHILD2_MAX_TEXTSIZE = 17f;
    public static final float LEFT_TAB_CHILD2_MIN_TEXTSIZE = 15f;

    public static final String COLOR_HOUR = "#FD9D56";
    public static final String COLOR_RED = "#FF5454";
    public static final String COLOR_GRREN = "#00A871";
    public static final String COLLECTED = "已收藏";
    public static final String COLLECTED_UN = "已取消收藏";
    // 录音 存储 摄像头
    public static String[] NEEDED_PERMISSION = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            // wifi状态，每个页面的 wifi 图标
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE};

    public static final String getUserAgent() {
        return com.google.android.exoplayer2.util.Util.getUserAgent(MyApplication.getApp(), MyApplication.getApp().getResources().getString(R.string.app_name));
    }
}
