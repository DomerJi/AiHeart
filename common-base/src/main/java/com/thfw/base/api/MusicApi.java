package com.thfw.base.api;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.thfw.base.models.BaikeModel;
import com.thfw.base.models.GuPiaoModel;
import com.thfw.base.models.MusicModel;
import com.thfw.base.models.WeatherDetailsModel;
import com.thfw.base.net.OkHttpUtil;
import com.thfw.base.net.SSL;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RegularUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
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

    public static void request(String name, MusicCallback callback) {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                // https://search.kuwo.cn/r.s?all=%E6%9E%97%E5%AD%90%E7%A5%A5&ft=music&%20itemset=web_2013&client=kt&pn=0&rn=5&rformat=json&encoding=utf8
                .url("https://search.kuwo.cn/r.s?all=" + name + "&ft=music&%20itemset=web_2013&client=kt&pn=0&rn=15&rformat=json&encoding=utf8")
                .get().build();
        /**
         * 	'abslist': [{
         * 		'AARTIST': 'George&nbsp;Lam###Sally&nbsp;Yeh',
         * 		'ALBUM': '2013年江苏卫视春晚',
         * 		'ALBUMID': '266850',
         * 		'ARTIST': '林子祥&叶蒨文',
         * 		'ARTISTID': '646',
         * 		'COPYRIGHT': '0',
         * 		'CanSetRing': '1',
         * 		'CanSetRingback': '1',
         * 		'DC_TARGETID': '3240843',
         * 		'DC_TARGETTYPE': 'music',
         * 		'DURATION': '171',
         * 		'FORMATS': 'WMA96|WMA128|MP3H|MP3192|MP3128|AAC48|AAC24|EXMV720P|EXMV700|EXMV500|EXMP4L|EXMP4HV|EXMP4',
         * 		'HASECHO': '0',
         * 		'IS_POINT': '0',
         * 		'MKVRID': 'MV_260024',
         * 		'MP3NSIG1': '2987303865',
         * 		'MP3NSIG2': '367591860',
         * 		'MP3RID': 'MP3_3240843',
         * 		'MUSICRID': 'MUSIC_3240843',
         * 		'MUTI_VER': '0',
         * 		'MVFLAG': '1',
         * 		'MVPIC': '324/24/57/3166051194.jpg',
         * 		'NAME': '选择',
         * 		'NEW': '0',
         * 		'NSIG1': '1624304761',
         * 		'NSIG2': '2240890720',
         * 		'ONLINE': '1',
         * 		'PAY': '0',
         * 		'PICPATH': '',
         * 		'PLAYCNT': '81267',
         * 		'SCORE100': '67',
         * 		'SIG1': '1624304761',
         * 		'SIG2': '2240890720',
         * 		'SONGNAME': '选择(Live)',
         * 		'SUBLIST': [],
         * 		'SUBTITLE': '',
         * 		'TAG': 'http://music.hyey.com/music/483/200506/13d/747592736.wma',
         * 		'ad_subtype': '',
         * 		'ad_type': '',
         * 		'allartistid': '646&36',
         * 		'audiobookpayinfo': {
         * 			'download': '0',
         * 			'play': '0'
         *                },
         * 		'barrage': '0',
         * 		'cache_status': '1',
         * 		'content_type': '0',
         * 		'fpay': '0',
         * 		'hts_MVPIC': 'https://img4.kuwo.cn/wmvpic/324/24/57/3166051194.jpg',
         * 		'info': 'xxxx',
         * 		'iot_info': '',
         * 		'isdownload': '0',
         * 		'isshowtype': '0',
         * 		'isstar': '0',
         * 		'mp4sig1': '4194141894',
         * 		'mp4sig2': '212024382',
         * 		'mvpayinfo': {
         * 			'download': '0',
         * 			'play': '0',
         * 			'vid': '268889'
         *        },
         * 		'originalsongtype': '0',
         * 		'payInfo': {
         * 			'cannotDownload': '0',
         * 			'cannotOnlinePlay': '0',
         * 			'download': '1000',
         * 			'feeType': {
         * 				'album': '0',
         * 				'bookvip': '0',
         * 				'song': '0',
         * 				'vip': '1'
         *            },
         * 			'limitfree': '0',
         * 			'listen_fragment': '0',
         * 			'local_encrypt': '0',
         * 			'ndown': '000001',
         * 			'nplay': '000001',
         * 			'overseas_ndown': '0',
         * 			'overseas_nplay': '0',
         * 			'play': '1000',
         * 			'refrain_end': '0',
         * 			'refrain_start': '0',
         * 			'tips_intercept': '0'
         *        },
         * 		'spPrivilege': '0',
         * 		'subsStrategy': '0',
         * 		'subsText': '',
         * 		'terminal': '',
         * 		'tme_musician_adtype': '0',
         * 		'tpay': '0',
         * 		'web_albumpic_short': '120/90/63/1524470202.jpg',
         * 		'web_artistpic_short': '120/19/84/1010990435.jpg',
         * 		'web_timingonline': ''* 	}
         */
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
                    Type type = new TypeToken<MusicModel.MusicResult>() {
                    }.getType();
                    MusicModel.MusicResult result = GsonUtil.fromJson(json, type);
                    list = result.abslist;
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

    public static void requestLyric(String lrcUrl, LyricCallback callback) {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient = httpClient.newBuilder()
                .sslSocketFactory(new SSL(OkHttpUtil.sslSocket()), OkHttpUtil.sslSocket())
                .build();
        Request request = new Request.Builder()
                .url(lrcUrl)
                .get().build();
        LogUtil.i(TAG, "lrcUrl -> " + lrcUrl);
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
                if (callback != null) {
                    if (!TextUtils.isEmpty(json)) {
                        callback.onResponse(json);
                    } else {
                        callback.onFailure(-2, "data is null");
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
        httpClient = httpClient.newBuilder()
                .sslSocketFactory(new SSL(OkHttpUtil.sslSocket()), OkHttpUtil.sslSocket())
                .build();
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

        String url = "http://qt.gtimg.cn/q=" + guPiaoModel.requestGp();
        LogUtil.e(TAG, "url = " + url);
        OkHttpClient httpClient = new OkHttpClient();
        httpClient = httpClient.newBuilder()
                .sslSocketFactory(new SSL(OkHttpUtil.sslSocket()), OkHttpUtil.sslSocket())
                .build();
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

        void onResponse(String lyricStr);
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
