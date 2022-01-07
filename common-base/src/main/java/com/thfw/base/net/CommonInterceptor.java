package com.thfw.base.net;


import android.text.TextUtils;

import com.thfw.base.utils.LogUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 通用拦截器
 *
 * @param
 * @return
 */
public class CommonInterceptor implements Interceptor {

    @Override
    public synchronized Response intercept(Chain chain) throws IOException {
        Request request = rebuildRequest(chain.request());
        Response response = chain.proceed(request);
        String json = response.peekBody(Long.MAX_VALUE).string();
        LogUtil.d("CommonInterceptor", "json = " + json);
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
        if (tokenListener != null) {
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
