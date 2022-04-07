package com.thfw.util;

/**
 * Author:pengs
 * Date: 2022/4/7 13:50
 * Describe:Todo
 */
public class NumUtil {

    public static final String ZERO_1 = "0.0";

    public static String getPeople(float people) {
        if (people == 0f) {
            return ZERO_1;
        }
        return new java.text.DecimalFormat("##0.0").format(people);
    }

    public static String numberToStr(long num) {
        if (num > 10000) {
            return getPeople(num / 10000f) + "万";
        }
        return String.valueOf(num);
    }
}
