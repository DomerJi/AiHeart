package com.thfw.base.net.download;

import android.util.Log;

import com.thfw.base.utils.MD5Util;
import com.thfw.base.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class TestDownLoad {

    public static final String TAG = "TestDownLoad";
    public static String[] testUrls = new String[]{
            "android/weixin807android1920_arm64.apk",
            "weixin/mac/WeChatMac.dmg",
            "weixin/Windows/WeChatSetup.exe"
    };
    private static String baseUrl = "https://dldir1.qq.com/weixin/";

    public static String getBaseUrl() {
        return baseUrl;
    }

    private static Retrofit getRetrofit(ProgressListener progressListener, String downUrl) {


        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        ProgressListener mControlProgressListener = new ProgressListener() {
            @Override
            public void update(String url, long bytesRead, long contentLength, boolean done) {
                // tellProgress(url, bytesRead, contentLength, done);
//                        Log.e(TAG, "url = " + url
//                                + "; bytesRead = " + bytesRead
//                                + "; contentLength = " + contentLength);
                progressListener.update(url, bytesRead, contentLength, done);
            }
        };

        retrofitBuilder.baseUrl(getBaseUrl());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .addInterceptor(new ProgressInterceptor(mControlProgressListener, getSkip(downUrl)))
                .build();

        return retrofitBuilder.client(okHttpClient).build();
    }

    private static long getSkip(String downUrl) {
        String fileNmae01 = MD5Util.getMD5String(downUrl).toUpperCase();
        String path = ToastUtil.getAppContext().getCacheDir() + File.separator
                + fileNmae01 + "_" + ".apk";

        File file = new File(path);
        long skip = 0;
        if (file != null && file.exists() && file.length() > 0) {
            try {
                RandomAccessFile raf = new RandomAccessFile(file.getAbsolutePath(), "rw");
                skip = raf.length();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return skip;
            }
        }
        return skip;
    }

    public static void test(String downUrl, ProgressListener progressListener) {

        getRetrofit(progressListener, downUrl).create(ApiService.class)
                .download(downUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        // 处理 ResponseBody 中的流
                        Log.e(TAG, "accept responseBody: " + responseBody.contentLength());
                        write(responseBody, downUrl, progressListener);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept on error: " + downUrl, throwable);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe ");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.e(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    private static void write(ResponseBody responseBody, String downUrl, ProgressListener progressListener) {
        int progress = 0;
        InputStream inputStream = responseBody.byteStream();

        long contentLength = responseBody.contentLength();

//        String fileName = HourUtil.getDate_(System.currentTimeMillis());
        String fileNmae01 = MD5Util.getMD5String(downUrl).toUpperCase();
        String path = ToastUtil.getAppContext().getCacheDir() + File.separator
                + fileNmae01 + "_" + ".apk";

        File file = new File(path);
        RandomAccessFile raf = null;
        // 续传
        long off = 0;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (file.length() == contentLength) {
                progressListener.update(getBaseUrl() + downUrl, file.length(), contentLength, file.length() == contentLength);
                return;
            } else {
                // 移动指针
                try {
                    raf = new RandomAccessFile(file.getAbsolutePath(), "rw");
                    off = raf.length();
                    raf.seek(off);
                    Log.d(TAG, "移动指针 - >" + off);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        try {
            FileOutputStream fileOutputStream = (raf == null) ? new FileOutputStream(file) : null;

            byte[] bytes = new byte[1024];

            int rendLength = 0;
            long currLength = 0;
            // 跳过已读
            if (raf != null) {
                inputStream.skip(off);
                Log.d(TAG, "跳过已读 off -> " + off + " ; skip -> " + off);
                currLength = off;
            }
            while ((rendLength = inputStream.read(bytes)) != -1) {
                if (progressListener instanceof DownLoadIntentService.ControlProgressListener) {
                    if (!((DownLoadIntentService.ControlProgressListener) progressListener).isDowning()) {
                        Log.e(TAG, "progress -> " + progress + " ; ");
                        break;
                    }
                }
                // 追加
                if (raf != null) {
                    raf.write(bytes, 0, rendLength);
                } else {
                    fileOutputStream.write(bytes, 0, rendLength);
                }
                currLength += rendLength;
                progress = (int) (currLength * 100 / contentLength);
                Log.e(TAG, "progress -> " + progress + " ; ");
            }

            if (progress == 100) {
                Log.e(TAG, "下载成功==== path -> " + file.getAbsolutePath());
            }
            responseBody.close();
            inputStream.close();
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            if (raf != null) {
                raf.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
