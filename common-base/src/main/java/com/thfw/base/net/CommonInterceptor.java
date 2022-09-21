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

    public static final String TOKEN = "Token";

    private static OnTokenListener tokenListener;

    public static void setTokenListener(OnTokenListener tokenListener) {
        CommonInterceptor.tokenListener = tokenListener;
    }

    public static String getToken() {
        return tokenListener != null ? tokenListener.getToken() : null;
    }

    @Override
    public synchronized Response intercept(Chain chain) throws IOException {
        Request request = rebuildRequest(chain.request());
        Response response = chain.proceed(request);
        if (LogUtil.isLogEnabled()) {
            String json = response.peekBody(Long.MAX_VALUE).string();
            LogUtil.d("CommonInterceptor", "json = " + json);
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

        addToken(requestBuilder);

        return requestBuilder.build();
    }

    /**
     * 添加 Token
     *
     * @param requestBuilder
     */
    public static void addToken(Request.Builder requestBuilder) {
        String token = CommonInterceptor.getToken();
        if (!TextUtils.isEmpty(token)) {
            requestBuilder.addHeader(TOKEN, token);
        }
    }

    public interface OnTokenListener {
        String getToken();
    }
}
