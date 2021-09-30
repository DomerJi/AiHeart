package com.thfw.base.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转换
 */
public class HourUtil {

    private static final SimpleDateFormat formatyyMMdd = new SimpleDateFormat("yy-MM-dd");
    private static final SimpleDateFormat formatyyMMddHHmm = new SimpleDateFormat("yy-MM-dd");
    private static final SimpleDateFormat formatHHmmss = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat format_ = new SimpleDateFormat("yy_MM_dd HH_mm_ss");


    public static String getDate(Date date) {
        return format.format(date);
    }

    public static String getDate_(Date date) {
        return format_.format(date);
    }

    public static String getDate(long time) {
        return getDate(new Date(time));
    }

    public static String getDate_(long time) {
        return getDate_(new Date(time));
    }

    public static String getDate(String time) {
        if (RegularUtil.isNumber(time)) {
            return getDate(Long.parseLong(time));
        }
        return "";
    }

    public static String getHHMMSS(Date date) {
        return formatHHmmss.format(date);
    }

    public static String getHHMMSS(long time) {
        return getHHMMSS(new Date(time));
    }

    public static String getHHMMSS(String time) {
        if (RegularUtil.isNumber(time)) {
            return getHHMMSS(Long.parseLong(time));
        }
        return "";
    }

    public static String getYYMMDD(Date date) {
        return formatyyMMdd.format(date);
    }

    public static String getYYMMDD(long time) {
        return getYYMMDD(new Date(time));
    }

    public static String getYYMMDD(String time) {
        if (RegularUtil.isNumber(time)) {
            return getYYMMDD(Long.parseLong(time));
        }
        return "";
    }

    public static String getYYMMDD_HHMM(Date date) {
        return formatyyMMddHHmm.format(date);
    }

    public static String getYYMMDD_HHMM(long time) {
        return getYYMMDD_HHMM(new Date(time));
    }

    public static String getYYMMDD_HHMM(String time) {
        if (RegularUtil.isNumber(time)) {
            return getYYMMDD_HHMM(Long.parseLong(time));
        }
        return "";
    }

}
