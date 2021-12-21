package com.thfw.robotheart.constants;

import android.Manifest;

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

    public static String[] NEEDED_PERMISSION = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
}
