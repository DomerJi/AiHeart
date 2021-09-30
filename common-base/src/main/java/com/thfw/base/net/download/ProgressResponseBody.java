package com.thfw.base.net.download;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private final String url;
    private BufferedSource bufferedSource;
    private long skip = 0;


    ProgressResponseBody(String url, ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.url = url;
    }

    ProgressResponseBody(String url, ResponseBody responseBody, ProgressListener progressListener, long skip) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.url = url;
        this.skip = skip;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
            // todo 跳过会缺少数据
//            try {
//                bufferedSource.skip(skip);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return bufferedSource;
    }

    private Source source(final Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                progressListener.update(url, totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}