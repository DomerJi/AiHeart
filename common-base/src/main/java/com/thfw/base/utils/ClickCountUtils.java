package com.thfw.base.utils;

import android.util.Log;

/**
 * Author:pengs
 * Date: 2021/8/12 11:24
 * Describe:点击次数计数
 */
public class ClickCountUtils {

    private static int clickCount = 1;
    private static long lastTimeMillis = 0;

    public static int getClickCount() {
        return clickCount;
    }

    /**
     * 连续点击
     *
     * @param count 计数总数
     * @return 是否达到计数总数
     */
    public static boolean click(int count) {
        long time = System.currentTimeMillis();
        // 距离上次点击小于N毫秒
        if (time - lastTimeMillis < 1000) {
            lastTimeMillis = time;
            clickCount++;
            if (clickCount >= count) {
                clickCount = 1;
                return true;
            }
        } else {
            lastTimeMillis = time;
            clickCount = 1;
        }
        Log.i("ClickCountUtils", "lastTimeMillis = " + lastTimeMillis + "; count = " + count);
        return false;
    }
}
