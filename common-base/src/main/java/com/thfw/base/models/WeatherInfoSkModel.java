package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author:pengs
 * Date: 2022/9/8 18:48
 * Describe:Todo
 */
public class WeatherInfoSkModel implements Serializable {

    /**
     * weatherinfo : {"city":"广州","cityid":"101280101","temp":"26.6","WD":"东南风","WS":"小于3级","SD":"83%","AP":"1001.4hPa","njd":"暂无实况","WSE":"<3","time":"17:50","sm":"1.7","isRadar":"1","Radar":"JC_RADAR_AZ9200_JB"}
     */

    @SerializedName("weatherinfo")
    public WeatherinfoBean weatherinfo;

    public WeatherinfoBean getWeatherinfo() {
        return weatherinfo;
    }

    public static class WeatherinfoBean implements Serializable {
        /**
         * city : 广州
         * cityid : 101280101
         * temp : 26.6
         * WD : 东南风
         * WS : 小于3级
         * SD : 83%
         * AP : 1001.4hPa
         * njd : 暂无实况
         * WSE : <3
         * time : 17:50
         * sm : 1.7
         * isRadar : 1
         * Radar : JC_RADAR_AZ9200_JB
         */

        @SerializedName("city")
        public String city;
        @SerializedName("cityid")
        public String cityid;
        @SerializedName("temp")
        public String temp;
        @SerializedName("WD")
        public String wd;
        @SerializedName("WS")
        public String ws;
        @SerializedName("SD")
        public String sd;
        @SerializedName("AP")
        public String ap;
        @SerializedName("njd")
        public String njd;
        @SerializedName("WSE")
        public String wse;
        @SerializedName("time")
        public String time;
        @SerializedName("sm")
        public String sm;
        @SerializedName("isRadar")
        public String isRadar;
        @SerializedName("Radar")
        public String Radar;
        
    }
}
