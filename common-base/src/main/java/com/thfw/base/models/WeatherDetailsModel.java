package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.utils.HourUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author:pengs
 * Date: 2022/9/16 8:51
 * Describe:Todo
 */
public class WeatherDetailsModel implements Serializable {

    /**
     * code : 200
     * message :
     * redirect :
     * value : [{"alarms":[],"city":"西安","cityid":101110101,"indexes":[{"abbreviation":"ct","alias":"","content":"预计今日天气炎热，建议穿着清凉透气的衣物。推荐穿轻棉织物制作的薄T、短裙、短裤等。","level":"薄T","name":"穿衣指数"},{"abbreviation":"gm","alias":"","content":"感冒高发期，尽量避免外出，外出戴口罩防护。","level":"高发","name":"感冒指数"},{"abbreviation":"xc","alias":"","content":"预计今日不宜洗车，未来24小时可能有雨或风力较大，如果在此期间洗车，雨水或灰尘可能会再次弄脏您的爱车。","level":"不适宜","name":"洗车指数"},{"abbreviation":"yd","alias":"","content":"预计今日整体气象条件尚可，大部分人可以外出锻炼，但敏感人群要注意天气变化，提前做好调整。","level":"适宜","name":"运动指数"},{"abbreviation":"uv","alias":"","content":"紫外线辐射强，建议涂擦SPF20左右、PA++的防晒护肤品。","level":"强","name":"紫外线强度指数"},{"abbreviation":"pp","alias":"","content":"","level":"暂无","name":"化妆指数"}],"pm25":{"advice":"0","aqi":"77","citycount":400,"cityrank":7,"co":"0.8","color":"0","level":"0","no2":"65","o3":"21","pm10":"103","pm25":"56","quality":"良","so2":"13","timestamp":"","upDateTime":"2022-09-16 08:31:34"},"provinceName":"陕西省","realtime":{"img":"0","sD":"79","sendibleTemp":"20","temp":"19","time":"2022-09-16 08:31:35","wD":"南风","wS":"1级","weather":"晴","ziwaixian":"N/A"},"weatherDetailsInfo":{"publishTime":"2022-09-16 08:00:00","weather3HoursDetailsInfos":[{"endTime":"2022-09-16 12:00:00","highestTemperature":"20","img":"1","isRainFall":"","lowerestTemperature":"20","precipitation":"0","startTime":"2022-09-16 09:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-16 15:00:00","highestTemperature":"25","img":"1","isRainFall":"","lowerestTemperature":"25","precipitation":"0","startTime":"2022-09-16 12:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-16 18:00:00","highestTemperature":"28","img":"2","isRainFall":"","lowerestTemperature":"28","precipitation":"0","startTime":"2022-09-16 15:00:00","wd":"","weather":"阴","ws":""},{"endTime":"2022-09-16 21:00:00","highestTemperature":"25","img":"1","isRainFall":"","lowerestTemperature":"25","precipitation":"0","startTime":"2022-09-16 18:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-17 00:00:00","highestTemperature":"23","img":"1","isRainFall":"","lowerestTemperature":"23","precipitation":"0","startTime":"2022-09-16 21:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-17 03:00:00","highestTemperature":"20","img":"2","isRainFall":"","lowerestTemperature":"20","precipitation":"0","startTime":"2022-09-17 00:00:00","wd":"","weather":"阴","ws":""},{"endTime":"2022-09-17 06:00:00","highestTemperature":"19","img":"2","isRainFall":"","lowerestTemperature":"19","precipitation":"0","startTime":"2022-09-17 03:00:00","wd":"","weather":"阴","ws":""}]},"weathers":[{"date":"2022-09-16","img":"1","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"28","temp_day_f":"82.4","temp_night_c":"18","temp_night_f":"64.4","wd":"","weather":"多云","week":"星期五","ws":""},{"date":"2022-09-17","img":"7","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"26","temp_day_f":"78.8","temp_night_c":"19","temp_night_f":"66.2","wd":"","weather":"小雨","week":"星期六","ws":""},{"date":"2022-09-18","img":"0","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"28","temp_day_f":"82.4","temp_night_c":"20","temp_night_f":"68.0","wd":"","weather":"晴","week":"星期日","ws":""},{"date":"2022-09-19","img":"8","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"21","temp_day_f":"69.8","temp_night_c":"14","temp_night_f":"57.2","wd":"","weather":"中雨","week":"星期一","ws":""},{"date":"2022-09-20","img":"2","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"21","temp_day_f":"69.8","temp_night_c":"14","temp_night_f":"57.2","wd":"","weather":"阴","week":"星期二","ws":""},{"date":"2022-09-21","img":"1","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"27","temp_day_f":"80.6","temp_night_c":"15","temp_night_f":"59.0","wd":"","weather":"多云","week":"星期三","ws":""},{"date":"2022-09-15","img":"1","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"26","temp_day_f":"78.8","temp_night_c":"17","temp_night_f":"62.6","wd":"","weather":"多云","week":"星期四","ws":""}]}]
     */

    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("redirect")
    private String redirect;
    @SerializedName("value")
    private List<ValueBean> value;


    private TreeMap<String, String> weeksWeatherMap;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public List<ValueBean> getValue() {
        return value;
    }

    public void setValue(List<ValueBean> value) {
        this.value = value;
    }

    public static class ValueBean implements Serializable {
        /**
         * alarms : []
         * city : 西安
         * cityid : 101110101
         * indexes : [{"abbreviation":"ct","alias":"","content":"预计今日天气炎热，建议穿着清凉透气的衣物。推荐穿轻棉织物制作的薄T、短裙、短裤等。","level":"薄T","name":"穿衣指数"},{"abbreviation":"gm","alias":"","content":"感冒高发期，尽量避免外出，外出戴口罩防护。","level":"高发","name":"感冒指数"},{"abbreviation":"xc","alias":"","content":"预计今日不宜洗车，未来24小时可能有雨或风力较大，如果在此期间洗车，雨水或灰尘可能会再次弄脏您的爱车。","level":"不适宜","name":"洗车指数"},{"abbreviation":"yd","alias":"","content":"预计今日整体气象条件尚可，大部分人可以外出锻炼，但敏感人群要注意天气变化，提前做好调整。","level":"适宜","name":"运动指数"},{"abbreviation":"uv","alias":"","content":"紫外线辐射强，建议涂擦SPF20左右、PA++的防晒护肤品。","level":"强","name":"紫外线强度指数"},{"abbreviation":"pp","alias":"","content":"","level":"暂无","name":"化妆指数"}]
         * pm25 : {"advice":"0","aqi":"77","citycount":400,"cityrank":7,"co":"0.8","color":"0","level":"0","no2":"65","o3":"21","pm10":"103","pm25":"56","quality":"良","so2":"13","timestamp":"","upDateTime":"2022-09-16 08:31:34"}
         * provinceName : 陕西省
         * realtime : {"img":"0","sD":"79","sendibleTemp":"20","temp":"19","time":"2022-09-16 08:31:35","wD":"南风","wS":"1级","weather":"晴","ziwaixian":"N/A"}
         * weatherDetailsInfo : {"publishTime":"2022-09-16 08:00:00","weather3HoursDetailsInfos":[{"endTime":"2022-09-16 12:00:00","highestTemperature":"20","img":"1","isRainFall":"","lowerestTemperature":"20","precipitation":"0","startTime":"2022-09-16 09:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-16 15:00:00","highestTemperature":"25","img":"1","isRainFall":"","lowerestTemperature":"25","precipitation":"0","startTime":"2022-09-16 12:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-16 18:00:00","highestTemperature":"28","img":"2","isRainFall":"","lowerestTemperature":"28","precipitation":"0","startTime":"2022-09-16 15:00:00","wd":"","weather":"阴","ws":""},{"endTime":"2022-09-16 21:00:00","highestTemperature":"25","img":"1","isRainFall":"","lowerestTemperature":"25","precipitation":"0","startTime":"2022-09-16 18:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-17 00:00:00","highestTemperature":"23","img":"1","isRainFall":"","lowerestTemperature":"23","precipitation":"0","startTime":"2022-09-16 21:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-17 03:00:00","highestTemperature":"20","img":"2","isRainFall":"","lowerestTemperature":"20","precipitation":"0","startTime":"2022-09-17 00:00:00","wd":"","weather":"阴","ws":""},{"endTime":"2022-09-17 06:00:00","highestTemperature":"19","img":"2","isRainFall":"","lowerestTemperature":"19","precipitation":"0","startTime":"2022-09-17 03:00:00","wd":"","weather":"阴","ws":""}]}
         * weathers : [{"date":"2022-09-16","img":"1","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"28","temp_day_f":"82.4","temp_night_c":"18","temp_night_f":"64.4","wd":"","weather":"多云","week":"星期五","ws":""},{"date":"2022-09-17","img":"7","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"26","temp_day_f":"78.8","temp_night_c":"19","temp_night_f":"66.2","wd":"","weather":"小雨","week":"星期六","ws":""},{"date":"2022-09-18","img":"0","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"28","temp_day_f":"82.4","temp_night_c":"20","temp_night_f":"68.0","wd":"","weather":"晴","week":"星期日","ws":""},{"date":"2022-09-19","img":"8","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"21","temp_day_f":"69.8","temp_night_c":"14","temp_night_f":"57.2","wd":"","weather":"中雨","week":"星期一","ws":""},{"date":"2022-09-20","img":"2","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"21","temp_day_f":"69.8","temp_night_c":"14","temp_night_f":"57.2","wd":"","weather":"阴","week":"星期二","ws":""},{"date":"2022-09-21","img":"1","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"27","temp_day_f":"80.6","temp_night_c":"15","temp_night_f":"59.0","wd":"","weather":"多云","week":"星期三","ws":""},{"date":"2022-09-15","img":"1","sun_down_time":"18:50","sun_rise_time":"06:28","temp_day_c":"26","temp_day_f":"78.8","temp_night_c":"17","temp_night_f":"62.6","wd":"","weather":"多云","week":"星期四","ws":""}]
         */

        @SerializedName("city")
        private String city;
        @SerializedName("cityid")
        private int cityid;
        @SerializedName("pm25")
        private Pm25Bean pm25;
        @SerializedName("provinceName")
        private String provinceName;
        @SerializedName("realtime")
        private RealtimeBean realtime;
        @SerializedName("weatherDetailsInfo")
        private WeatherDetailsInfoBean weatherDetailsInfo;
        @SerializedName("alarms")
        private List<AlarmsBean> alarms;
        @SerializedName("indexes")
        private List<IndexesBean> indexes;
        @SerializedName("weathers")
        private List<WeathersBean> weathers;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getCityid() {
            return cityid;
        }

        public void setCityid(int cityid) {
            this.cityid = cityid;
        }

        public Pm25Bean getPm25() {
            return pm25;
        }

        public void setPm25(Pm25Bean pm25) {
            this.pm25 = pm25;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public RealtimeBean getRealtime() {
            return realtime;
        }

        public void setRealtime(RealtimeBean realtime) {
            this.realtime = realtime;
        }

        public WeatherDetailsInfoBean getWeatherDetailsInfo() {
            return weatherDetailsInfo;
        }

        public void setWeatherDetailsInfo(WeatherDetailsInfoBean weatherDetailsInfo) {
            this.weatherDetailsInfo = weatherDetailsInfo;
        }

        public List<AlarmsBean> getAlarms() {
            return alarms;
        }

        public void setAlarms(List<AlarmsBean> alarms) {
            this.alarms = alarms;
        }

        public List<IndexesBean> getIndexes() {
            return indexes;
        }

        public void setIndexes(List<IndexesBean> indexes) {
            this.indexes = indexes;
        }

        public List<WeathersBean> getWeathers() {
            return weathers;
        }

        public void setWeathers(List<WeathersBean> weathers) {
            this.weathers = weathers;
        }

        public static class AlarmsBean implements Serializable {

            /**
             * alarmContent : 大风蓝色预警信号：预计16日14时到17日2时，沈阳市浑南区东北风转西北风4到5级，阵风7级，请注意出行安全，加固临时搭建物，防范高空坠物等风险。浑南区气象台2022年9月16日10时47分发布
             * alarmDesc : 浑南区气象局发布大风蓝色预警[Ⅳ级/一般]
             * alarmId : 15c0eab15e4118fbd51020f9d178bcb0
             * alarmLevelNo : 01
             * alarmLevelNoDesc : 蓝色
             * alarmType : 00
             * alarmTypeDesc : 大风预警
             * precaution :
             * publishTime : 2022-09-16 10:53:17
             */

            @SerializedName("alarmContent")
            private String alarmContent;
            @SerializedName("alarmDesc")
            private String alarmDesc;
            @SerializedName("alarmId")
            private String alarmId;
            @SerializedName("alarmLevelNo")
            private String alarmLevelNo;
            @SerializedName("alarmLevelNoDesc")
            private String alarmLevelNoDesc;
            @SerializedName("alarmType")
            private String alarmType;
            @SerializedName("alarmTypeDesc")
            private String alarmTypeDesc;
            @SerializedName("precaution")
            private String precaution;
            @SerializedName("publishTime")
            private String publishTime;

            public String getAlarmContent() {
                return alarmContent;
            }

            public String getAlarmTypeDesc() {
                return alarmTypeDesc;
            }

            public String getAlarmLevelNoDesc() {
                return alarmLevelNoDesc;
            }
        }

        public static class Pm25Bean implements Serializable {
            /**
             * advice : 0
             * aqi : 77
             * citycount : 400
             * cityrank : 7
             * co : 0.8
             * color : 0
             * level : 0
             * no2 : 65
             * o3 : 21
             * pm10 : 103
             * pm25 : 56
             * quality : 良
             * so2 : 13
             * timestamp :
             * upDateTime : 2022-09-16 08:31:34
             */

            @SerializedName("advice")
            private String advice;
            @SerializedName("aqi")
            private String aqi;
            @SerializedName("citycount")
            private int citycount;
            @SerializedName("cityrank")
            private int cityrank;
            @SerializedName("co")
            private String co;
            @SerializedName("color")
            private String color;
            @SerializedName("level")
            private String level;
            @SerializedName("no2")
            private String no2;
            @SerializedName("o3")
            private String o3;
            @SerializedName("pm10")
            private String pm10;
            @SerializedName("pm25")
            private String pm25;
            @SerializedName("quality")
            private String quality;
            @SerializedName("so2")
            private String so2;
            @SerializedName("timestamp")
            private String timestamp;
            @SerializedName("upDateTime")
            private String upDateTime;

            public String getAdvice() {
                return advice;
            }

            public void setAdvice(String advice) {
                this.advice = advice;
            }

            public String getAqi() {
                return aqi;
            }

            public void setAqi(String aqi) {
                this.aqi = aqi;
            }

            public int getCitycount() {
                return citycount;
            }

            public void setCitycount(int citycount) {
                this.citycount = citycount;
            }

            public int getCityrank() {
                return cityrank;
            }

            public void setCityrank(int cityrank) {
                this.cityrank = cityrank;
            }

            public String getCo() {
                return co;
            }

            public void setCo(String co) {
                this.co = co;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getNo2() {
                return no2;
            }

            public void setNo2(String no2) {
                this.no2 = no2;
            }

            public String getO3() {
                return o3;
            }

            public void setO3(String o3) {
                this.o3 = o3;
            }

            public String getPm10() {
                return pm10;
            }

            public void setPm10(String pm10) {
                this.pm10 = pm10;
            }

            public String getPm25() {
                return pm25;
            }

            public void setPm25(String pm25) {
                this.pm25 = pm25;
            }

            public String getQuality() {
                return quality;
            }

            public void setQuality(String quality) {
                this.quality = quality;
            }

            public String getSo2() {
                return so2;
            }

            public void setSo2(String so2) {
                this.so2 = so2;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }

            public String getUpDateTime() {
                return upDateTime;
            }

            public void setUpDateTime(String upDateTime) {
                this.upDateTime = upDateTime;
            }
        }

        public static class RealtimeBean implements Serializable {
            /**
             * img : 0
             * sD : 79
             * sendibleTemp : 20
             * temp : 19
             * time : 2022-09-16 08:31:35
             * wD : 南风
             * wS : 1级
             * weather : 晴
             * ziwaixian : N/A
             */

            @SerializedName("img")
            private String img;
            @SerializedName("sD")
            private String sD;
            @SerializedName("sendibleTemp")
            private String sendibleTemp;
            @SerializedName("temp")
            private String temp;
            @SerializedName("time")
            private String time;
            @SerializedName("wD")
            private String wD;
            @SerializedName("wS")
            private String wS;
            @SerializedName("weather")
            private String weather;
            @SerializedName("ziwaixian")
            private String ziwaixian;

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getSD() {
                return sD;
            }

            public void setSD(String sD) {
                this.sD = sD;
            }

            public String getSendibleTemp() {
                return sendibleTemp;
            }

            public void setSendibleTemp(String sendibleTemp) {
                this.sendibleTemp = sendibleTemp;
            }

            public String getTemp() {
                return temp;
            }

            public void setTemp(String temp) {
                this.temp = temp;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getWD() {
                return wD;
            }

            public void setWD(String wD) {
                this.wD = wD;
            }

            public String getWS() {
                return wS;
            }

            public void setWS(String wS) {
                this.wS = wS;
            }

            public String getWeather() {
                return weather;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public String getZiwaixian() {
                return ziwaixian;
            }

            public void setZiwaixian(String ziwaixian) {
                this.ziwaixian = ziwaixian;
            }
        }

        public static class WeatherDetailsInfoBean implements Serializable {
            /**
             * publishTime : 2022-09-16 08:00:00
             * weather3HoursDetailsInfos : [{"endTime":"2022-09-16 12:00:00","highestTemperature":"20","img":"1","isRainFall":"","lowerestTemperature":"20","precipitation":"0","startTime":"2022-09-16 09:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-16 15:00:00","highestTemperature":"25","img":"1","isRainFall":"","lowerestTemperature":"25","precipitation":"0","startTime":"2022-09-16 12:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-16 18:00:00","highestTemperature":"28","img":"2","isRainFall":"","lowerestTemperature":"28","precipitation":"0","startTime":"2022-09-16 15:00:00","wd":"","weather":"阴","ws":""},{"endTime":"2022-09-16 21:00:00","highestTemperature":"25","img":"1","isRainFall":"","lowerestTemperature":"25","precipitation":"0","startTime":"2022-09-16 18:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-17 00:00:00","highestTemperature":"23","img":"1","isRainFall":"","lowerestTemperature":"23","precipitation":"0","startTime":"2022-09-16 21:00:00","wd":"","weather":"多云","ws":""},{"endTime":"2022-09-17 03:00:00","highestTemperature":"20","img":"2","isRainFall":"","lowerestTemperature":"20","precipitation":"0","startTime":"2022-09-17 00:00:00","wd":"","weather":"阴","ws":""},{"endTime":"2022-09-17 06:00:00","highestTemperature":"19","img":"2","isRainFall":"","lowerestTemperature":"19","precipitation":"0","startTime":"2022-09-17 03:00:00","wd":"","weather":"阴","ws":""}]
             */

            @SerializedName("publishTime")
            private String publishTime;
            @SerializedName("weather3HoursDetailsInfos")
            private List<Weather3HoursDetailsInfosBean> weather3HoursDetailsInfos;

            public String getPublishTime() {
                return publishTime;
            }

            public void setPublishTime(String publishTime) {
                this.publishTime = publishTime;
            }

            public List<Weather3HoursDetailsInfosBean> getWeather3HoursDetailsInfos() {
                return weather3HoursDetailsInfos;
            }

            public void setWeather3HoursDetailsInfos(List<Weather3HoursDetailsInfosBean> weather3HoursDetailsInfos) {
                this.weather3HoursDetailsInfos = weather3HoursDetailsInfos;
            }

            public static class Weather3HoursDetailsInfosBean implements Serializable {
                /**
                 * endTime : 2022-09-16 12:00:00
                 * highestTemperature : 20
                 * img : 1
                 * isRainFall :
                 * lowerestTemperature : 20
                 * precipitation : 0
                 * startTime : 2022-09-16 09:00:00
                 * wd :
                 * weather : 多云
                 * ws :
                 */

                @SerializedName("endTime")
                private String endTime;
                @SerializedName("highestTemperature")
                private String highestTemperature;
                @SerializedName("img")
                private String img;
                @SerializedName("isRainFall")
                private String isRainFall;
                @SerializedName("lowerestTemperature")
                private String lowerestTemperature;
                @SerializedName("precipitation")
                private String precipitation;
                @SerializedName("startTime")
                private String startTime;
                @SerializedName("wd")
                private String wd;
                @SerializedName("weather")
                private String weather;
                @SerializedName("ws")
                private String ws;

                public String getEndTime() {
                    return endTime;
                }

                public void setEndTime(String endTime) {
                    this.endTime = endTime;
                }

                public String getHighestTemperature() {
                    return highestTemperature;
                }

                public void setHighestTemperature(String highestTemperature) {
                    this.highestTemperature = highestTemperature;
                }

                public String getImg() {
                    return img;
                }

                public void setImg(String img) {
                    this.img = img;
                }

                public String getIsRainFall() {
                    return isRainFall;
                }

                public void setIsRainFall(String isRainFall) {
                    this.isRainFall = isRainFall;
                }

                public String getLowerestTemperature() {
                    return lowerestTemperature;
                }

                public void setLowerestTemperature(String lowerestTemperature) {
                    this.lowerestTemperature = lowerestTemperature;
                }

                public String getPrecipitation() {
                    return precipitation;
                }

                public void setPrecipitation(String precipitation) {
                    this.precipitation = precipitation;
                }

                public String getStartTime() {
                    return startTime;
                }

                public void setStartTime(String startTime) {
                    this.startTime = startTime;
                }

                public String getWd() {
                    return wd;
                }

                public void setWd(String wd) {
                    this.wd = wd;
                }

                public String getWeather() {
                    return weather;
                }

                public void setWeather(String weather) {
                    this.weather = weather;
                }

                public String getWs() {
                    return ws;
                }

                public void setWs(String ws) {
                    this.ws = ws;
                }
            }
        }

        public static class IndexesBean implements Serializable {
            /**
             * abbreviation : ct
             * alias :
             * content : 预计今日天气炎热，建议穿着清凉透气的衣物。推荐穿轻棉织物制作的薄T、短裙、短裤等。
             * level : 薄T
             * name : 穿衣指数
             */

            @SerializedName("abbreviation")
            private String abbreviation;
            @SerializedName("alias")
            private String alias;
            @SerializedName("content")
            private String content;
            @SerializedName("level")
            private String level;
            @SerializedName("name")
            private String name;

            public String getAbbreviation() {
                return abbreviation;
            }

            public void setAbbreviation(String abbreviation) {
                this.abbreviation = abbreviation;
            }

            public String getAlias() {
                return alias;
            }

            public void setAlias(String alias) {
                this.alias = alias;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class WeathersBean implements Serializable {
            /**
             * date : 2022-09-16
             * img : 1
             * sun_down_time : 18:50
             * sun_rise_time : 06:28
             * temp_day_c : 28
             * temp_day_f : 82.4
             * temp_night_c : 18
             * temp_night_f : 64.4
             * wd :
             * weather : 多云
             * week : 星期五
             * ws :
             */

            @SerializedName("date")
            private String date;
            @SerializedName("img")
            private String img;
            @SerializedName("sun_down_time")
            private String sunDownTime;
            @SerializedName("sun_rise_time")
            private String sunRiseTime;
            @SerializedName("temp_day_c")
            private String tempDayC;
            @SerializedName("temp_day_f")
            private String tempDayF;
            @SerializedName("temp_night_c")
            private String tempNightC;
            @SerializedName("temp_night_f")
            private String tempNightF;
            @SerializedName("wd")
            private String wd;
            @SerializedName("weather")
            private String weather;
            @SerializedName("week")
            private String week;
            @SerializedName("ws")
            private String ws;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getSunDownTime() {
                return sunDownTime;
            }

            public void setSunDownTime(String sunDownTime) {
                this.sunDownTime = sunDownTime;
            }

            public String getSunRiseTime() {
                return sunRiseTime;
            }

            public void setSunRiseTime(String sunRiseTime) {
                this.sunRiseTime = sunRiseTime;
            }

            public String getTempDayC() {
                return tempDayC;
            }

            public void setTempDayC(String tempDayC) {
                this.tempDayC = tempDayC;
            }

            public String getTempDayF() {
                return tempDayF;
            }

            public void setTempDayF(String tempDayF) {
                this.tempDayF = tempDayF;
            }

            public String getTempNightC() {
                return tempNightC;
            }

            public void setTempNightC(String tempNightC) {
                this.tempNightC = tempNightC;
            }

            public String getTempNightF() {
                return tempNightF;
            }

            public void setTempNightF(String tempNightF) {
                this.tempNightF = tempNightF;
            }

            public String getWd() {
                return wd;
            }

            public void setWd(String wd) {
                this.wd = wd;
            }

            public String getWeather() {
                return weather;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public String getWeek() {
                return week;
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public String getWs() {
                return ws;
            }

            public void setWs(String ws) {
                this.ws = ws;
            }
        }
    }

    public boolean isMySuccess() {
        return "200".equals(code) && value != null && !value.isEmpty();
    }

    public String getSimpleDesc() {
        return getMyWeather() + " " + getMyTemp();
    }

    public String getMyWeather() {
        if (isMySuccess()) {

            ValueBean valueBean = value.get(0);
            if (valueBean != null && valueBean.realtime != null) {
                long time = HourUtil.getYYMMDDHHMMSSbyLong(valueBean.realtime.time);
                // 距离实时天气时间 大于1小时 获取，未来三小时的数据
                if (System.currentTimeMillis() - time > HourUtil.LEN_HOUR) {
                    if (valueBean.weatherDetailsInfo != null && valueBean.weatherDetailsInfo.weather3HoursDetailsInfos != null) {
                        for (ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean hoursDetailsInfosBean
                                : valueBean.weatherDetailsInfo.weather3HoursDetailsInfos) {
                            long hourStart = HourUtil.getYYMMDDHHMMSSbyLong(hoursDetailsInfosBean.startTime);
                            long hourEnd = HourUtil.getYYMMDDHHMMSSbyLong(hoursDetailsInfosBean.endTime);
                            if (time >= hourStart && time <= hourEnd) {
                                return hoursDetailsInfosBean.weather;
                            }
                        }
                    }

                }
                return valueBean.realtime.weather;
            }
        }
        return null;

    }

    public String getMyTemp() {
        if (isMySuccess()) {
            ValueBean valueBean = value.get(0);
            if (valueBean != null && valueBean.realtime != null) {
                long time = HourUtil.getYYMMDDHHMMSSbyLong(valueBean.realtime.time);
                // 距离实时天气时间 大于1小时 获取，未来三小时的数据
                if (System.currentTimeMillis() - time > HourUtil.LEN_HOUR) {
                    if (valueBean.weatherDetailsInfo != null && valueBean.weatherDetailsInfo.weather3HoursDetailsInfos != null) {
                        for (ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean hoursDetailsInfosBean
                                : valueBean.weatherDetailsInfo.weather3HoursDetailsInfos) {
                            long hourStart = HourUtil.getYYMMDDHHMMSSbyLong(hoursDetailsInfosBean.startTime);
                            long hourEnd = HourUtil.getYYMMDDHHMMSSbyLong(hoursDetailsInfosBean.endTime);
                            if (time >= hourStart && time <= hourEnd) {
                                return hoursDetailsInfosBean.lowerestTemperature;
                            }
                        }
                    }

                }
                return valueBean.realtime.temp + "℃";
            }
        }
        return null;
    }

    public String getMySD() {
        if (isMySuccess()) {
            ValueBean valueBean = value.get(0);
            if (valueBean != null && valueBean.realtime != null) {
                return "湿度" + valueBean.realtime.sD + "%";
            }
        }
        return null;
    }

    public String getMyPm25() {
        if (isMySuccess()) {
            ValueBean valueBean = value.get(0);
            if (valueBean != null && valueBean.pm25 != null) {
                return "空气质量 " + valueBean.pm25.quality;
            }
        }
        return null;
    }

    public String getMyPm25Number() {
        if (isMySuccess()) {
            ValueBean valueBean = value.get(0);
            if (valueBean != null && valueBean.pm25 != null) {
                return valueBean.pm25.quality + " " + valueBean.pm25.aqi;
            }
        }
        return null;
    }

    public ValueBean getValueBean() {
        if (isMySuccess()) {
            return value.get(0);
        }
        return null;
    }

    public String[] getSunInOut() {
        ValueBean valueBean = value.get(0);
        if (valueBean != null && valueBean.weathers != null && !valueBean.weathers.isEmpty()) {
            weeksWeatherMap = new TreeMap<>();
            for (ValueBean.WeathersBean weathersBean : valueBean.weathers) {
                StringBuilder sb = new StringBuilder();
                sb.append(valueBean.city);
                // 今天
                if (weathersBean.date.equals(HourUtil.getYYMMDD(System.currentTimeMillis()))) {
                    return new String[]{weathersBean.sunRiseTime, weathersBean.sunDownTime};
                }
            }
        }
        return null;
    }

    public String getDesc() {
        return getDesc(HourUtil.getYYMMDD(System.currentTimeMillis()));
    }

    public String getWeekDesc() {
        if (weeksWeatherMap == null || weeksWeatherMap.isEmpty()) {
            init();
        }
        if (weeksWeatherMap != null && !weeksWeatherMap.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            ValueBean valueBean = value.get(0);
            if (valueBean != null) {
                sb.append(valueBean.city);
                sb.append("天气：<br>");
            }
            for (Map.Entry<String, String> entry : weeksWeatherMap.entrySet()) {
                sb.append(entry.getValue().replaceFirst(valueBean.city + "，", ""));
                sb.append("<br>");
            }
            String weekDesc = sb.toString();
            if (weekDesc.endsWith("<br>")) {
                weekDesc = weekDesc.substring(0, weekDesc.length() - "<br>".length());
            }
            return weekDesc;
        }
        return "";
    }


    /**
     * 日期 2022-01-01
     *
     * @param time
     * @return
     */
    public String getDesc(String time) {
        if (!isMySuccess()) {
            return null;
        }

        init();
        return weeksWeatherMap.get(time);

    }

    private void init() {
        if (weeksWeatherMap == null || weeksWeatherMap.isEmpty()) {
            ValueBean valueBean = value.get(0);
            if (valueBean != null && valueBean.weathers != null && !valueBean.weathers.isEmpty()) {
                weeksWeatherMap = new TreeMap<>();
                for (ValueBean.WeathersBean weathersBean : valueBean.weathers) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(valueBean.city);
                    // 今天
                    if (weathersBean.date.equals(HourUtil.getYYMMDD(System.currentTimeMillis()))) {
                        sb.append("，");
                        sb.append("今天 ");
                        sb.append(weathersBean.week);
                        sb.append("，");
                        sb.append(getMyWeather());
                        sb.append("，温度");
                        sb.append(getMyTemp());
                        sb.append("，");
                        sb.append(weathersBean.tempNightC + "℃~" + weathersBean.tempDayC + "℃");
                        sb.append("，");
                        sb.append(getMySD());
                        sb.append("，");
                        sb.append(getMyPm25());
                        sb.append("。");
                    } else {
                        sb.append("，");
                        if (weathersBean.date.equals(HourUtil.getYYMMDD(System.currentTimeMillis() + HourUtil.LEN_DAY))) {
                            sb.append("明天 ");
                        } else if (weathersBean.date.equals(HourUtil.getYYMMDD(System.currentTimeMillis() - HourUtil.LEN_DAY))) {
                            sb.append("昨天 ");
                        } else {
                            sb.append(weathersBean.date + " ");
                        }
                        sb.append(weathersBean.week);
                        sb.append("，");
                        sb.append(weathersBean.weather);
                        sb.append("，");
                        sb.append(weathersBean.tempNightC + "℃~" + weathersBean.tempDayC + "℃");
                        sb.append("。");
                    }
                    weeksWeatherMap.put(weathersBean.date, sb.toString());
                }
            }
        }
    }


}
