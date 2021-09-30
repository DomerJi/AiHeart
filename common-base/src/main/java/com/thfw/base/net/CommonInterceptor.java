package com.thfw.base.net;


import com.thfw.base.BuildConfig;
import com.thfw.base.utils.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 通用拦截器
 *
 * @param
 * @return
 */
public class CommonInterceptor implements Interceptor {

    @Override
    public synchronized Response intercept(Chain chain) {
        Request request = rebuildRequest(chain.request());
        Response response = null;

        try {
            response = chain.proceed(request);
            Charset charset = Charset.forName("UTF-8");
            ResponseBody responseBody = response.peekBody(Long.MAX_VALUE);
            Reader jsonReader = new InputStreamReader(responseBody.byteStream(), charset);
            BufferedReader reader = new BufferedReader(jsonReader);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(e.toString());
        }
        return response;
    }

    private Request rebuildRequest(Request request) {
        Request.Builder requestBuilder;
        if ("POST".equals(request.method())) {
            requestBuilder = request.newBuilder();
        } else {
            requestBuilder = request.newBuilder();
        }
        // TODO 添加头部 cookie token
        requestBuilder.addHeader("Authorization", "TOKEN");
        requestBuilder.addHeader("Cookie", "BDUSS=");
        if (BuildConfig.DEBUG) {

        }
        return requestBuilder.build();
    }
}
