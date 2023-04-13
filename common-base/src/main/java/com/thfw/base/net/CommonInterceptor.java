package com.thfw.base.net;


import android.text.TextUtils;

import com.thfw.base.ContextApp;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RobotUtil2;

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
    public static final String CLIENT_TYPE = "client-type";
    //    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICE_TYPE = "device-type";
    private static final String CHECK_VERSION_URL = "version/latest";
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
        LogUtil.i(" requestBuilder.build().url() =111 " + GsonUtil.toJson(requestBuilder.build()));


        if (!TextUtils.isEmpty(token)) {
            requestBuilder.addHeader(TOKEN, token);
        }

        requestBuilder.addHeader(CLIENT_TYPE, ContextApp.getContentType());

        if (requestBuilder.build().url().toString().endsWith(CHECK_VERSION_URL)) {
            switch (ContextApp.getDeviceType()) {
                case ContextApp.DeviceType.PAD:
                case ContextApp.DeviceType.MOBILE:
                    requestBuilder.addHeader(DEVICE_TYPE, CommonParameter.DeviceType.MOBILE);
                    break;
                case ContextApp.DeviceType.ROBOT:
                    if (RobotUtil2.isInstallRobot()) {
                        requestBuilder.addHeader(DEVICE_TYPE, CommonParameter.DeviceType.ROBOT);
                    } else {
                        requestBuilder.addHeader(DEVICE_TYPE, CommonParameter.DeviceType.PAD);
                    }
                    break;
            }
        } else {
            requestBuilder.addHeader(DEVICE_TYPE, CommonParameter.getDeviceType());
        }

    }


    public interface OnTokenListener {
        String getToken();
    }
}
