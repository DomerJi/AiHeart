package com.thfw.base.net.download;

public interface ProgressListener {
    void update(String url, long bytesRead, long contentLength, boolean done,String filePath);
}