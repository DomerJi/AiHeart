package com.thfw.base.net;


import android.text.TextUtils;

import com.thfw.base.utils.LogUtil;

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
            String json = responseBody.string();
            LogUtil.d("CommonInterceptor", "json = " + json);
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
//        requestBuilder.addHeader("Authorization", "TOKEN");
//        requestBuilder.addHeader("Cookie", "BDUSS=");
        if (tokenListener != null && requestBuilder != null) {
            String token = tokenListener.getToken();
            if (!TextUtils.isEmpty(token)) {
                requestBuilder.addHeader("Token", token);
            }
        }
        return requestBuilder.build();
    }


    public static void setTokenListener(OnTokenListener tokenListener) {
        CommonInterceptor.tokenListener = tokenListener;
    }

    private static OnTokenListener tokenListener;

    public interface OnTokenListener {
        String getToken();
    }
}
