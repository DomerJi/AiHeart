package com.thfw.base.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/9/14 16:40
 * Describe:Todo
 */
public class HeartBoxEntity {

    public long time;
    public String timeStr;
    public String flag;
    public String content;
    public List<String> images = new ArrayList<>();

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    private static String formatDateTime(long time) {
        Date date = new Date();
        date.setTime(time);

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();    //昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);

        if (current.after(today)) {
            return "今天";
        } else if (current.before(today) && current.after(yesterday)) {

            return "昨天";
        } else {
            Calendar now = Calendar.getInstance();
            now.setTime(date);
            if (current.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                return now.get(Calendar.DAY_OF_MONTH) + "-" + (now.get(Calendar.MONTH) + 1) + "月";
            } else {
                return (now.get(Calendar.MONTH) + 1) + "月-" + current.get(Calendar.YEAR) + "年";
            }
        }
    }

    public String getTimeStr() {
        if (timeStr == null) {
            timeStr = formatDateTime(time);
        }
        return timeStr;
    }
}
