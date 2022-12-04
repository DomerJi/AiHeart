package com.thfw.base.api;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.thfw.base.models.BaikeModel;
import com.thfw.base.models.GuPiaoModel;
import com.thfw.base.models.LyricModel;
import com.thfw.base.models.Mp3PicModel;
import com.thfw.base.models.MusicModel;
import com.thfw.base.models.WeatherDetailsModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.SharePreferenceUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
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
        String url = "http://aider.meizu.com/app/weather/listWeather?cityIds=" + weatherId;
        LogUtil.e(TAG, "url = " + url);
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
                WeatherDetailsModel weatherDetailsModel = null;
                try {
                    Type type = new TypeToken<WeatherDetailsModel>() {
                    }.getType();
                    weatherDetailsModel = GsonUtil.fromJson(json, type);
                } catch (Exception e) {
                    LogUtil.e(TAG, "weatherDetailsModel e = " + e.getMessage());
                } finally {
                    if (callback != null) {
                        if (null != weatherDetailsModel) {
                            callback.onResponse(weatherDetailsModel);
                        } else {
                            callback.onFailure(-2, "data is null");
                        }
                    }
                }
            }
        });
    }

    public static void requestGupiao(String key, GuPiaoCallback callback) {
        // https://suggest3.sinajs.cn/suggest/type=1&key=天和防务&name=1
        // https://zj.v.api.aa1.cn/api/gupiao-01/?gp=sz300397

        String url = "https://suggest3.sinajs.cn/suggest/type=&key=" + key + "&name=result";
        LogUtil.e(TAG, "url = " + url);
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
                /**
                 * var result="sz000651,1,000651,sz000651,格力电器,,712;sh143870,1,143870,sh143870,18格力02,,48176;sh143869,1,143869,sh143869,18格力01,,48174;sh110030,1,110030,sh110030,格力转债,,16425;sh600185,1,600185,sh600185,格力地产,,1109;sh122474,1,122474,sh122474,15格力债,,18567";
                 var 1="天和防务,11,300397,sz300397,天和防务,,天和防务,99,1,ESG";

                 var result="tcehy.us,1,tcehy.us,tcehy.us,腾讯公司,,25748;
                 of001637,1,001637,of001637,嘉实腾讯自选股大数据策略股票,,29260;
                 0700hk,1,0700,0700hk,腾讯控股,,5385;4536hk,1,4536,4536hk,腾讯瑞银九十二购,,6470;1412hk,1,1412,1412hk,腾讯摩通九一零购,,7544;tctzf.us,1,tctzf.us,tctzf.us,腾讯公司,,25759;0419hk,1,0419,0419hk,华谊腾讯娱乐,,6163";

                 var 1="baba.us,1,baba.us,baba.us,阿里巴巴,,13015;9988hk,1,9988,9988hk,阿里巴巴-SW,,6705";
                 */
                if (!TextUtils.isEmpty(json)) {
                    json = json.replace("var result=\"", "");
                    json = json.replace("\";", "");
                    LogUtil.i(TAG, "new json = " + json);
                }

                GuPiaoModel guPiaoModel = null;
                try {
                    LogUtil.i(TAG, "company =start ");
                    String[] company = json.split(";");
                    LogUtil.i(TAG, "company = " + Arrays.toString(company));
                    for (String c : company) {
                        if ((guPiaoModel = checkCompany(c)) != null) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "guPiaoModel e = " + e.getMessage());
                } finally {
                    if (callback != null) {
                        if (guPiaoModel != null) {
                            requestGupiao2(guPiaoModel, callback);
                        } else {
                            callback.onFailure(-2, "data is null");
                        }
                    }
                }
            }
        });
    }

    private static void requestGupiao2(GuPiaoModel guPiaoModel, GuPiaoCallback callback) {
        // https://suggest3.sinajs.cn/suggest/type=1&key=天和防务&name=1
        // https://zj.v.api.aa1.cn/api/gupiao-01/?gp=sz300397

        String url = "https://zj.v.api.aa1.cn/api/gupiao-01/?gp=" + guPiaoModel.requestGp();
        LogUtil.e(TAG, "url = " + url);
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
                /**
                 *v_sz300397="51~天和防务~300397~10.21~10.14~10.14~57973~31743~26229~10.20~665~10.19~360~10.18~454~10.17~267~10.16~427~10.21~11~10.22~1092~10.23~1007~10.24~786~10.25~1222~~20221202161454~0.07~0.69~10.24~10.12~10.21/57973/59135005~57973~5914~1.45~-49.53~~10.24~10.12~1.18~40.90~52.85~2.72~12.17~8.11~0.73~-1945~10.20~-119.73~-73.29~~~0.92~5913.5005~0.0000~0~ A~GP-A-CYB~-33.22~3.24~0.00~-5.49~-4.56~16.88~8.30~-2.30~-6.24~-11.53~400600143~517636745~-30.92~-22.36~400600143~~~-31.98~0.10~";
                 * Fatal error: Allowed memory size of 134217728 bytes exhausted (tried to allocate 12288 bytes) in /www/wwwroot/zj.v.api.aa1.cn/api/gupiao-01/yuyinzhuanwenzixr.php on line 25
                 *
                 *
                 * 以~分割字符串	string	根据数字判断
                 * 1	string	股票名字
                 * 2	string	股票代码
                 * 3	string	当前价格
                 * 4	string	昨收
                 * 5	string	今开
                 * 6	string	成交量（手）
                 * 7	string	外盘
                 * 8	string	内盘
                 * 9	string	买一
                 * 10	string	买一量（手）
                 * 11～18	string	买二 买五
                 * 19～20	string	卖一
                 * 20	string	卖一量
                 * 21～28	string	卖二 卖五
                 * 29	string	最近逐笔成交
                 * 30	string	时间
                 * 31	string	涨跌
                 * 32	string	涨跌%
                 * 33	string	最高
                 * 34	string	最低
                 * 35	string	价格/成交量（手）/成交额
                 * 36	string	成交量（手）
                 * 37	string	成交额（万）
                 * 38	string	换手率
                 * 39	string	市盈率
                 * 40～41	string	最高
                 * 42	string	最低
                 * 43	string	涨幅
                 * 44	string	流通市值
                 * 45	string	总市值
                 * 46	string	市净率
                 * 47	string	涨停价
                 * 48	string	跌停价
                 */


                try {

                    if (!TextUtils.isEmpty(json)) {
                        int end = json.indexOf("\";");
                        int start = json.indexOf("=\"");
                        if (start != -1 && end != -1) {
                            String[] data = json.substring(start, end).split("~");
                            guPiaoModel.data = data;
                            if (data.length > 45) {
                                if (guPiaoModel != null) {
                                    callback.onResponse(guPiaoModel);
                                }
                                return;
                            }
                        }

                    }
                    if (callback != null) {
                        callback.onFailure(-2, "data is null");
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "guPiaoModel2 e = " + e.getMessage());
                }
            }
        });
    }

    private static GuPiaoModel checkCompany(String company) {
        GuPiaoModel guPiaoModel = null;
        String[] code = company.split(",");
        if (code.length < 4) {
            return guPiaoModel;
        }
        LogUtil.d(TAG, "code = " + Arrays.toString(code));
        String gpCode = code[2];
        String gpCodeMarket = code[3];
        if (!TextUtils.isEmpty(gpCode)) {
            LogUtil.d(TAG, "gpCode = " + gpCode);
            if (gpCodeMarket.startsWith(GuPiaoModel.MarketType.SH)) {
                guPiaoModel = new GuPiaoModel();
                guPiaoModel.code = gpCode.replace(GuPiaoModel.MarketType.SH, "");
                guPiaoModel.market = GuPiaoModel.MarketType.SH;
                guPiaoModel.company = code[4];
                return guPiaoModel;
            } else if (gpCodeMarket.startsWith(GuPiaoModel.MarketType.SZ)) {
                guPiaoModel = new GuPiaoModel();
                guPiaoModel.code = gpCode.replace(GuPiaoModel.MarketType.SZ, "");
                guPiaoModel.market = GuPiaoModel.MarketType.SZ;
                guPiaoModel.company = code[4];
                return guPiaoModel;
            } else if (gpCodeMarket.endsWith(GuPiaoModel.MarketType.HK) || (RegularUtil.isNumber(gpCode) && gpCode.length() > 4)) {
                guPiaoModel = new GuPiaoModel();
                guPiaoModel.code = gpCode.replace(GuPiaoModel.MarketType.HK, "");
                guPiaoModel.market = GuPiaoModel.MarketType.HK;
                guPiaoModel.company = code[4];
                return guPiaoModel;
            }
        }

        return guPiaoModel;
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

    public interface GuPiaoCallback {

        void onFailure(int code, String msg);

        void onResponse(GuPiaoModel data);
    }

    public interface BaiKeCallback {
        void onFailure(int code, String msg);

        void onResponse(BaikeModel baikeModel);
    }

    public interface WeatherCallback {
        void onFailure(int code, String msg);

        void onResponse(WeatherDetailsModel weatherDetailsModel);
    }


}
