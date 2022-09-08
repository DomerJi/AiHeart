package com.thfw.base.utils;

import android.content.res.Resources;
import android.text.TextUtils;

import com.thfw.base.ContextApp;
import com.thfw.base.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Author:pengs
 * Date: 2022/9/8 17:41
 * Describe:Todo
 */
public class WeatherUtil {

    private static final String TAG = WeatherUtil.class.getSimpleName();
    private static final String AREAID = "AREAID";
    private static HashMap<String, String> mCityIdMap;

    public static String getWeatherCityId(String city) {
        if (mCityIdMap == null) {
            mCityIdMap = new HashMap<>();
            String json = getAreaJsonString();
            if (!TextUtils.isEmpty(json)) {
                try {
                    JSONObject jsonObject = new JSONObject(json);

                    Iterator<String> kes = jsonObject.keys();
                    while (kes.hasNext()) {

                        String key = kes.next();
                        JSONObject provinceObject = jsonObject.optJSONObject(key);
                        if (provinceObject != null) {

                            Iterator<String> cityKes = provinceObject.keys();
                            while (cityKes.hasNext()) {
                                String cityKey = cityKes.next();
                                JSONObject idObject = provinceObject.optJSONObject(cityKey);
                                String areaId = idObject.optString(AREAID);
                                LogUtil.i(TAG, "cityKey = " + cityKey + " ; cityId = " + areaId);
                                mCityIdMap.put(cityKey, areaId);
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return mCityIdMap.get(city);
    }

    public static String getWeatherCityId() {
        return getWeatherCityId(LocationUtils.getCityName());
    }

    private static String getAreaJsonString() {

        StringBuffer sb = new StringBuffer();

        InputStream is = null;

        InputStreamReader isr = null;

        BufferedReader br = null;

        String str = "";

        Resources resources = ContextApp.get().getResources();

        try {
            is = resources.openRawResource(R.raw.weather); // 读取相应的章节
            isr = new InputStreamReader(is, "UTF-8");// 这里添加了UTF-8，解决乱码问题
            br = new BufferedReader(isr);

            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append('\n');
            }
            br.close();
            isr.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }
}
