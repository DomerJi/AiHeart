package com.thfw.base.utils;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.thfw.base.net.OkHttpUtil;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LocationUtils {

    // public static String cityName = "深圳";  // 城市名
    private static String cityName;  // 城市名
    private static CityNameListener mCityNameListener;  // 城市名
    private static final String KEY_LOCAL_CITY = "key.city";
    private static final String TAG = "LocationUtils";
    private static Geocoder geocoder;    // 此对象能通过经纬度来获取相应的城市等信息

    //用于获取Location对象，以及其他
    private static LocationManager locationManager;

    public static String getCityName() {
        if (cityName == null) {
            cityName = SharePreferenceUtil.getString(KEY_LOCAL_CITY, cityName);
        }
        return cityName;
    }

    public static void setCityNameListener(CityNameListener mCityNameListener) {
        LocationUtils.mCityNameListener = mCityNameListener;
        notifyCityName();
    }

    private static void notifyCityName() {
        HandlerUtil.getMainHandler().post(() -> {
            if (LocationUtils.mCityNameListener != null) {
                if (!TextUtils.isEmpty(getCityName())) {
                    LocationUtils.mCityNameListener.callBack(cityName);
                }
            }
        });

    }

    public interface CityNameListener {
        void callBack(String cityName);
    }

    /**
     * 通过地理坐标获取城市名	其中CN分别是city和name的首字母缩写
     *
     * @param context
     */
    public static void getCNBylocation(Context context) {
        try {
//            有时会有县一级的名字
//            getCNBylocationGeo(context);
            getCurrentProvinceAndCity();
        } catch (Exception e) {
        }
    }

    private static void getCNBylocationGeo(Context context) throws Exception {

        geocoder = new Geocoder(context);

        String serviceName = Context.LOCATION_SERVICE;
        //实例化一个LocationManager对象
        locationManager = (LocationManager) context.getSystemService(serviceName);
        if (locationManager == null || geocoder == null) {
            throw new Exception("geocoder == null");
        }
        //provider的类型
        String provider = LocationManager.NETWORK_PROVIDER;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    //高精度
        criteria.setAltitudeRequired(false);    //不要求海拔
        criteria.setBearingRequired(false);    //不要求方位
        criteria.setCostAllowed(false);    //不允许有话费
        criteria.setPowerRequirement(Criteria.POWER_LOW);    //低功耗

        //通过最后一次的地理位置来获得Location对象
        Location location = locationManager.getLastKnownLocation(provider);

        String queryed_name = updateWithNewLocation(location);
        if ((queryed_name != null) && (0 != queryed_name.length())) {
            cityName = queryed_name;
        }

        /*
         * 第二个参数表示更新的周期，单位为毫秒；第三个参数的含义表示最小距离间隔，单位是米
         * 设定每30秒进行一次自动定位
         */
        locationManager.requestLocationUpdates(provider, 30000, 50,
                locationListener);
    }

    /**
     * 方位改变时触发，进行调用
     */
    private final static LocationListener locationListener = new LocationListener() {
        String tempCityName;

        public void onLocationChanged(Location location) {

            tempCityName = updateWithNewLocation(location);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {
                cityName = tempCityName;
                SharePreferenceUtil.setString(KEY_LOCAL_CITY, cityName);
                notifyCityName();
                LogUtil.i(TAG, "cityName = " + cityName);
                if (locationManager != null) {
                    try {
                        locationManager.removeUpdates(locationListener);
                    } catch (Exception e) {
                    }
                }
            }
        }

        public void onProviderDisabled(String provider) {
            tempCityName = updateWithNewLocation(null);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {

                cityName = tempCityName;
                SharePreferenceUtil.setString(KEY_LOCAL_CITY, cityName);
                notifyCityName();
                LogUtil.i(TAG, "cityName = " + cityName);
                if (locationManager != null) {
                    try {
                        locationManager.removeUpdates(locationListener);
                    } catch (Exception e) {
                    }
                }
            }

        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 更新location
     *
     * @param location
     * @return cityName
     */
    private static String updateWithNewLocation(Location location) {
        String mcityName = "";
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {

            System.out.println("无法获取地理信息");
        }

        try {

            addList = geocoder.getFromLocation(lat, lng, 1);    //解析经纬度

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address add = addList.get(i);
                mcityName += add.getLocality();
            }
        }
        if (mcityName.length() != 0) {

            return mcityName.substring(0, (mcityName.length() - 1));
        } else {
            return mcityName;
        }
    }

    /**
     * 通过经纬度获取地址信息的另一种方法
     *
     * @param latitude
     * @param longitude
     * @return 城市名
     */
    public static String GetAddr(String latitude, String longitude) {
        String addr = "";

        /*
         * 也可以是http://maps.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s，不过解析出来的是英文地址
         * 密钥可以随便写一个key=abc
         * output=csv,也可以是xml或json，不过使用csv返回的数据最简洁方便解析
         */
        String url = String.format(
                "http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s",
                latitude, longitude);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {

            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        try {

            httpsConn = (URLConnection) myURL.openConnection();

            if (httpsConn != null) {
                InputStreamReader insr = new InputStreamReader(
                        httpsConn.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(insr);
                String data = null;
                if ((data = br.readLine()) != null) {
                    String[] retList = data.split(",");
                    if (retList.length > 2 && ("200".equals(retList[0]))) {
                        addr = retList[2];
                    } else {
                        addr = "";
                    }
                }
                insr.close();
            }
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        return addr;
    }


    private static void getCurrentProvinceAndCity() {
        final String url = "http://ip-api.com/json/?lang=zh-CN";
        OkHttpUtil.request(url, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                TimingHelper.getInstance().addWorkArriveListener(new TimingHelper.WorkListener() {
                    @Override
                    public void onArrive() {
                        getCurrentProvinceAndCity();
                    }

                    @Override
                    public WorkInt workInt() {
                        return WorkInt.SECOND_IP;
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                LogUtil.i(TAG, "json ip = " + json);
                JSONObject jsonObject = null;
                TimingHelper.getInstance().removeWorkArriveListener(WorkInt.SECOND_IP);
                try {
                    jsonObject = new JSONObject(json);
                    if (jsonObject != null) {
                        String city = jsonObject.optString("city");
                        if (!TextUtils.isEmpty(city)) {
                            cityName = city;
                            SharePreferenceUtil.setString(KEY_LOCAL_CITY, cityName);
                            notifyCityName();
                        }
                    }
                } catch (JSONException e) {
                    LogUtil.e(TAG, "json ip e = " + e.getMessage());
                }
            }
        });
    }

}