package com.thfw.base.net.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ProgressInterceptor implements Interceptor {

    private ProgressListener progressListener;
    private long skip = 0;

    public ProgressInterceptor(ProgressListener progressListener, long skip) {
        this.progressListener = progressListener;
        this.skip = skip;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(chain.request().url().url().toString(), originalResponse.body(), progressListener, skip))
                .build();
    }

}