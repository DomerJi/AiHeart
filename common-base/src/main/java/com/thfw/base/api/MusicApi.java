package com.thfw.base.api;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.thfw.base.models.BaikeModel;
import com.thfw.base.models.LyricModel;
import com.thfw.base.models.Mp3PicModel;
import com.thfw.base.models.MusicModel;
import com.thfw.base.models.WeatherInfoCityModel;
import com.thfw.base.models.WeatherInfoModel;
import com.thfw.base.models.WeatherInfoSkModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author:pengs
 * Date: 2022/8/19 8:56
 * Describe:Todo
 */
public class MusicApi {
    public static final String TAG = "MusicApi";
    public static final String COOKIE = "PHPSESSID=ueimn7bbdegnipmtm28fr86ti2";
    public static final String KEY_ALBUM_PIC = "key.mp3.pics";
    public static final Map<String, String> mAlbumPicMap = Collections.synchronizedMap(new HashMap<>());
    public static boolean initPics;

    private static void initPics() {
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> cachePics = SharePreferenceUtil.getObject(KEY_ALBUM_PIC, type);
        if (!EmptyUtil.isEmpty(cachePics)) {
            mAlbumPicMap.putAll(cachePics);
        }
        initPics = true;
    }

    public static void request(String name, MusicCallback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("name", name);
        builder.addFormDataPart("pages", "1");
        builder.addFormDataPart("count", "10");
        builder.addFormDataPart("source", "netease");
        builder.addFormDataPart("types", "search");
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("cookie", COOKIE)
                .url("https://l-by.cn/yinyue/api.php")
                .post(builder.build())
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "e = " + e.getMessage());
                if (callback != null) {
                    callback.onFailure(-1, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.i(TAG, "json = " + json);
                List<MusicModel> list = null;
                try {
                    Type type = new TypeToken<List<MusicModel>>() {
                    }.getType();
                    list = GsonUtil.fromJson(json, type);
                } catch (Exception e) {
                    LogUtil.e(TAG, "list e = " + e.getMessage());
                } finally {
                    if (callback != null) {
                        if (!EmptyUtil.isEmpty(list)) {
                            callback.onResponse(list);
                        } else {
                            callback.onFailure(-2, "datas is null");
                        }
                    }
                }
            }
        });
    }

    public static void requestLyric(String id, LyricCallback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("id", id);
        /**
         * types:
         * id: 553543175
         * source: netease
         */
        builder.addFormDataPart("source", "netease");
        builder.addFormDataPart("types", "lyric");
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("cookie", COOKIE)
                .url("https://l-by.cn/yinyue/api.php")
                .post(builder.build())
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "e = " + e.getMessage());
                if (callback != null) {
                    callback.onFailure(-1, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.i(TAG, "json = " + json);
                LyricModel lyricModel = null;
                try {
                    Type type = new TypeToken<LyricModel>() {
                    }.getType();
                    lyricModel = GsonUtil.fromJson(json, type);
                } catch (Exception e) {
                    LogUtil.e(TAG, "lyricModel e = " + e.getMessage());
                } finally {
                    if (callback != null) {
                        if (null != lyricModel) {
                            callback.onResponse(lyricModel);
                        } else {
                            callback.onFailure(-2, "data is null");
                        }
                    }
                }
            }
        });
    }

    public static void requestAlbum(String picId, AlbumCallback callback) {
        if (!initPics) {
            initPics();
        }
        String mAlbumPic = mAlbumPicMap.get(picId);
        if (!TextUtils.isEmpty(mAlbumPic)) {
            if (callback != null) {
                callback.onResponse(mAlbumPic);
            }
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("id", picId);
        /**
         * types:
         * id: 553543175
         * source: netease
         */
        builder.addFormDataPart("source", "netease");
        builder.addFormDataPart("types", "pic");
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("cookie", COOKIE)
                .url("https://l-by.cn/yinyue/api.php")
                .post(builder.build())
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "e = " + e.getMessage());
                if (callback != null) {
                    callback.onFailure(-1, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.i(TAG, "json = " + json);
                Mp3PicModel mp3PicModel = null;
                try {
                    Type type = new TypeToken<Mp3PicModel>() {
                    }.getType();
                    mp3PicModel = GsonUtil.fromJson(json, type);
                } catch (Exception e) {
                    Log.i(TAG, "mp3PicModel e = " + e.getMessage());
                } finally {
                    if (callback != null) {
                        if (null != mp3PicModel) {
                            if (!TextUtils.isEmpty(mp3PicModel.getUrl())) {
                                mAlbumPicMap.put(picId, mp3PicModel.getUrl());
                                SharePreferenceUtil.setString(KEY_ALBUM_PIC, GsonUtil.toJson(mAlbumPicMap));
                                callback.onResponse(mp3PicModel.getUrl());
                            } else {
                                callback.onFailure(-3, "data picUrl is null");
                            }
                        } else {
                            callback.onFailure(-2, "data is null");
                        }
                    }
                }


            }
        });
    }

    public static void requestBaiKe(String key, BaiKeCallback callback) {

        String url = "http://baike.baidu.com/api/openapi/BaikeLemmaCardApi?scope=103&format=json&appid=379020&bk_key="
                + key + "&bk_length=600";
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "e = " + e.getMessage());
                if (callback != null) {
                    callback.onFailure(-1, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.i(TAG, "json = " + json);
                BaikeModel baikeModel = null;
                try {
                    Type type = new TypeToken<BaikeModel>() {
                    }.getType();
                    baikeModel = GsonUtil.fromJson(json, type);
                } catch (Exception e) {
                    LogUtil.e(TAG, "baikeModel e = " + e.getMessage());
                } finally {
                    if (callback != null) {
                        if (null != baikeModel) {
                            callback.onResponse(baikeModel);
                        } else {
                            callback.onFailure(-2, "data is null");
                        }
                    }
                }
            }
        });
    }

    public static void requestWeather(String weatherId, WeatherCallback callback) {
        // http://www.weather.com.cn/data/sk/101010100.html
        // http://www.weather.com.cn/data/cityinfo/101010100.html
        String url = "http://www.weather.com.cn/data/cityinfo/" + weatherId + ".html";
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "e = " + e.getMessage());
                if (callback != null) {
                    callback.onFailure(-1, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.i(TAG, "json = " + json);
                WeatherInfoCityModel cityModel = null;
                try {
                    Type type = new TypeToken<WeatherInfoCityModel>() {
                    }.getType();
                    cityModel = GsonUtil.fromJson(json, type);
                } catch (Exception e) {
                    LogUtil.e(TAG, "cityModel e = " + e.getMessage());
                } finally {
                    if (callback != null) {
                        if (null != cityModel) {
                            requestWeatherSK(weatherId, callback, cityModel);
                        } else {
                            callback.onFailure(-2, "data is null");
                        }
                    }
                }
            }
        });
    }

    private static void requestWeatherSK(String weatherId, WeatherCallback callback,
                                        WeatherInfoCityModel cityModel) {
        // http://www.weather.com.cn/data/sk/101010100.html
        // http://www.weather.com.cn/data/cityinfo/101010100.html
        String url = "http://www.weather.com.cn/data/sk/" + weatherId + ".html";
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "e = " + e.getMessage());
                if (callback != null) {
                    callback.onFailure(-1, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.i(TAG, "json = " + json);
                WeatherInfoSkModel skModel = null;
                try {
                    Type type = new TypeToken<WeatherInfoSkModel>() {
                    }.getType();
                    skModel = GsonUtil.fromJson(json, type);
                } catch (Exception e) {
                    LogUtil.e(TAG, "skModel e = " + e.getMessage());
                } finally {
                    if (callback != null) {
                        if (null != skModel) {
                            callback.onResponse(new WeatherInfoModel(cityModel, skModel));
                        } else {
                            callback.onFailure(-2, "data is null");
                        }
                    }
                }
            }
        });
    }


    public interface MusicCallback {
        void onFailure(int code, String msg);

        void onResponse(List<MusicModel> list);
    }

    public interface LyricCallback {
        void onFailure(int code, String msg);

        void onResponse(LyricModel lyricModel);
    }

    public interface AlbumCallback {
        void onFailure(int code, String msg);

        void onResponse(String albumPic);
    }

    public interface BaiKeCallback {
        void onFailure(int code, String msg);

        void onResponse(BaikeModel baikeModel);
    }

    public interface WeatherCallback {
        void onFailure(int code, String msg);

        void onResponse(WeatherInfoModel weatherInfoModel);
    }


}
