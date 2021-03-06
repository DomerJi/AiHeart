package com.thfw.robotheart.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;

import com.thfw.base.utils.LogUtil;

/**
 * Author:pengs
 * Date: 2022/2/21 17:26
 * Describe:网页字体大小跟随dpi缩放
 */
public class WebSizeUtil {

    public static void setSize(Context context, WebSettings webSettings) {
        int screenDensity = context.getResources().getDisplayMetrics().densityDpi;
        float lv = screenDensity * 1.0f / DisplayMetrics.DENSITY_XHIGH;
        int sizeZoom = (int) (lv * 100f);
        LogUtil.d("WebSizeUtil", "sizeZoom = " + sizeZoom);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setTextZoom(sizeZoom);
    }
}
