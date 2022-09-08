package com.thfw.base.models;

/**
 * Author:pengs
 * Date: 2022/9/8 18:51
 * Describe:Todo
 */
public class WeatherInfoModel {

    private WeatherInfoCityModel cityModel;
    private WeatherInfoSkModel skModel;

    private String desc;

    public WeatherInfoModel(WeatherInfoCityModel cityModel, WeatherInfoSkModel skModel) {
        this.cityModel = cityModel;
        this.skModel = skModel;
    }


    public String getDesc() {
        StringBuilder sb = new StringBuilder();
        if (cityModel != null && cityModel.weatherinfo != null) {
            // 城市 和 天气
            sb.append(cityModel.weatherinfo.city);
            sb.append(" ");
            sb.append(cityModel.weatherinfo.weather);

            if (skModel != null && skModel.weatherinfo != null) {
                sb.append("，");
                // ？风 和 ？级
                sb.append(skModel.weatherinfo.wd);
                sb.append(" ");
                sb.append(skModel.weatherinfo.ws);

                sb.append("，");

                sb.append(" 温度 ");
                sb.append(skModel.weatherinfo.temp);

                sb.append("℃，");

                sb.append(" 相对湿度 ");
                sb.append(skModel.weatherinfo.sd);

                sb.append("，");
                sb.append("气压 ");
                sb.append(skModel.weatherinfo.ap);
                sb.append("。");
            }
            desc = sb.toString();
        }

        return desc;
    }
}
