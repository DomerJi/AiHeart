package com.thfw.mobileheart.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;

import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.Util;

/**
 * Author:pengs
 * Date: 2022/2/21 17:26
 * Describe:网页字体大小跟随dpi缩放
 */
public class WebSizeUtil {

    public static void setSize(Context context, WebSettings webSettings) {
        int screenDensity = context.getResources().getDisplayMetrics().densityDpi;
        float lv = screenDensity * 1.0f / DisplayMetrics.DENSITY_220;
        if (Util.isPad(context)) {
            if (lv < 1.45f) {
                lv = 1.45f;
            }
        } else {
            if (lv < 2.18f) {
                lv = 2.2f;
            }
        }
        int sizeZoom = (int) (lv * 100f);
        LogUtil.d("WebSizeUtil", "screenDensity = " + screenDensity);
        LogUtil.d("WebSizeUtil", "lv = " + lv);
        LogUtil.d("WebSizeUtil", "sizeZoom = " + sizeZoom);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setTextZoom(sizeZoom);
    }

    public static void setSizeByAgree(Context context, WebSettings webSettings) {
        int screenDensity = context.getResources().getDisplayMetrics().densityDpi;
        float lv = screenDensity * 1.0f / DisplayMetrics.DENSITY_MEDIUM;
        if (Util.isPad(context)) {
            if (lv < 2.0f) {
                lv = 2.0f;
            }
        } else {
            if (lv < 3.0f) {
                lv = 3.0f;
            }
        }

        int sizeZoom = (int) (lv * 100f);
        LogUtil.d("WebSizeUtil", "screenDensity = " + screenDensity);
        LogUtil.d("WebSizeUtil", "lv = " + lv);
        LogUtil.d("WebSizeUtil", "sizeZoom = " + sizeZoom);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setTextZoom(sizeZoom);
    }
}
