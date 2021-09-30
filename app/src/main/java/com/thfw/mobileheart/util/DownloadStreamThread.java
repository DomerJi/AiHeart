package com.thfw.mobileheart.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

/**
 * 下载线程
 */
public class DownloadStreamThread extends Thread {
    final String targetFileAbsPath;
    String urlStr;
    private String TAG = DownloadStreamThread.class.getSimpleName();

    public DownloadStreamThread(String urlStr, String targetFileAbsPath) {
        this.urlStr = urlStr;
        this.targetFileAbsPath = targetFileAbsPath;
    }

    @Override
    public void run() {
        super.run();
        int count;
        File targetFile = new File(targetFileAbsPath);
        try {
            boolean n = targetFile.createNewFile();
            Log.d(TAG, "Create new file: " + n + ", " + targetFile);
        } catch (IOException e) {
            Log.e(TAG, "run: ", e);
        }
        try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            connection.connect();
            int contentLength = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(targetFileAbsPath);

            byte[] buffer = new byte[1024];
            long total = 0;
            while ((count = input.read(buffer)) != -1) {
                total += count;
                Log.d(TAG, String.format(Locale.CHINA, "Download progress: %.2f%%", 100 * (total / (double) contentLength)));
                output.write(buffer, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
        }
    }
}
