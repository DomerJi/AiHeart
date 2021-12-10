package com.thfw.base.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间转换
 */
public class HourUtil {

    private static final SimpleDateFormat formatyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat formatyyMMddHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat formatHHmmss = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat formatHHmm = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat format_ = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");


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

    public static String getHHMM(Date date) {
        return formatHHmm.format(date);
    }

    public static String getHHMM(long time) {
        return getHHMM(new Date(time));
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

    public static String getWeek(long time) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public static String[] getLimitTime(long limitTime) {
        long hour = limitTime / (3600 * 1000);
        long minute = limitTime / (60 * 1000) % 60;
        long second = limitTime / 1000 % 60;
        return new String[]{String.format("%02d", hour), String.format("%02d", minute), String.format("%02d", second)};
    }

    public static String getLimitTimeALl(long limitTime) {
        long hour = limitTime / (3600 * 1000);
        long minute = limitTime / (60 * 1000) % 60;
        long second = limitTime / 1000 % 60;
        return String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
    }
}
