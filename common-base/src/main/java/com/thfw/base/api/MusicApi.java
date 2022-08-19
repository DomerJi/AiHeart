package com.thfw.base.api;

import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.thfw.base.models.LyricModel;
import com.thfw.base.models.MusicModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

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
                Type type = new TypeToken<List<MusicModel>>() {
                }.getType();
                List<MusicModel> list = GsonUtil.fromJson(json, type);

                if (callback != null) {
                    if (!EmptyUtil.isEmpty(list)) {
                        callback.onResponse(list);
                    } else {
                        callback.onFailure(-2, "datas is null");
                    }
                }
                Log.i(TAG, "json = " + json);
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
                Type type = new TypeToken<LyricModel>() {
                }.getType();
                LyricModel lyricModel = GsonUtil.fromJson(json, type);

                if (callback != null) {
                    if (null != lyricModel) {
                        callback.onResponse(lyricModel);
                    } else {
                        callback.onFailure(-2, "data is null");
                    }
                }
                Log.i(TAG, "json = " + json);
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
}
