package com.thfw.robotheart.util;

import android.text.TextUtils;

import com.thfw.robotheart.R;

import java.util.Calendar;

/**
 * Author:pengs
 * Date: 2022/9/15 11:26
 * Describe:Todo
 */
public class IconUtil {

    public static int getWeatherIcon(String weather) {
        if (TextUtils.isEmpty(weather)) {
            return R.mipmap.refresh_cloud;
        }
        if (weather.length() > 2 && weather.contains("转")) {
            String tempWeather[] = weather.split("转");
            if (!TextUtils.isEmpty(tempWeather[0])) {
                weather = tempWeather[0];
            }
        }
        int curHour24 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        boolean light = curHour24 < 19 && curHour24 > 6;
        switch (weather) {
            case "晴":
                return light ? R.mipmap.icon_sunny : R.mipmap.icon_sunny_night;
            case "多云":
                return light ? R.mipmap.icon_cloudy : R.mipmap.icon_cloudy_night;
            case "阴":
                return light ? R.mipmap.icon_overcast : R.mipmap.icon_gray_bg_overcast;
            case "雷阵雨":
                return light ? R.mipmap.icon_t_storm : R.mipmap.icon_gray_bg_t_storm;
            case "小雨":
            case "阵雨":
                return light ? R.mipmap.icon_light_rain : R.mipmap.icon_gray_bg_light_rain;
            case "中雨":
                return light ? R.mipmap.icon_moderate_rain : R.mipmap.icon_gray_bg_moderate_rain;
            case "大雨":
            case "暴雨":
            case "大暴雨":
                return light ? R.mipmap.icon_heavy_rain : R.mipmap.icon_gray_bg_heavy_rain;
            case "冰雹":
                return light ? R.mipmap.icon_ice_rain : R.mipmap.icon_gray_bg_ice_rain;
            case "雨夹雪":
                return light ? R.mipmap.icon_rain_snow : R.mipmap.icon_gray_bg_rain_snow;
            case "小雪":
                return light ? R.mipmap.icon_light_snow : R.mipmap.icon_gray_bg_light_snow;
            case "中雪":
                return light ? R.mipmap.icon_moderate_snow : R.mipmap.icon_gray_bg_moderate_snow;
            case "大雪":
            case "大暴雪":
            case "暴雪":
                return light ? R.mipmap.icon_heavy_snow : R.mipmap.icon_gray_bg_heavy_snow;
            case "大雾":
            case "雾":
                return light ? R.mipmap.icon_fog : R.mipmap.icon_fog_night;
            case "雾霾":
            case "霾":
                return light ? R.mipmap.icon_pm_dirt : R.mipmap.icon_gray_bg_pm_dirt;
            case "浮尘":
                return light ? R.mipmap.icon_float_dirt : R.mipmap.icon_float_dirt;
            case "沙尘暴":
            case "沙尘":
            case "扬尘":
                return light ? R.mipmap.icon_sand : R.mipmap.icon_gray_bg_sand;

        }
        return R.mipmap.refresh_cloud;
    }
}
