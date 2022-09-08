package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author:pengs
 * Date: 2022/9/8 18:50
 * Describe:Todo
 */
public class WeatherInfoCityModel implements Serializable {

    /**
     * weatherinfo : {"city":"广州","cityid":"101280101","temp1":"26℃","temp2":"29℃","weather":"阵雨转暴雨","img1":"n3.gif","img2":"d10.gif","ptime":"18:00"}
     */

    @SerializedName("weatherinfo")
    public WeatherinfoBean weatherinfo;



    public static class WeatherinfoBean implements Serializable {
        /**
         * city : 广州
         * cityid : 101280101
         * temp1 : 26℃
         * temp2 : 29℃
         * weather : 阵雨转暴雨
         * img1 : n3.gif
         * img2 : d10.gif
         * ptime : 18:00
         */

        @SerializedName("city")
        public String city;
        @SerializedName("cityid")
        public String cityid;
        @SerializedName("temp1")
        public String temp1;
        @SerializedName("temp2")
        public String temp2;
        @SerializedName("weather")
        public String weather;
        @SerializedName("img1")
        public String img1;
        @SerializedName("img2")
        public String img2;
        @SerializedName("ptime")
        public String ptime;

    }
}
