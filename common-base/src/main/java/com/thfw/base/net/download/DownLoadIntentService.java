package com.thfw.base.net.download;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.thfw.base.utils.LogUtil;

import java.util.HashMap;

/**
 * 下载服务
 */
public class DownLoadIntentService extends IntentService {

    public static final String KEY_DOWNLOAD_URL = "download_url";
    public static final String TAG = DownLoadIntentService.class.getSimpleName();
    public static ProgressListener progressListener;
    private static HashMap<String, ControlProgressListener> progressMap = new HashMap<>();

    public DownLoadIntentService() {
        super(DownLoadIntentService.class.getSimpleName());
    }

    public static void begin(Context context, String url) {
        Intent intent = new Intent(context, DownLoadIntentService.class);
        intent.putExtra(KEY_DOWNLOAD_URL, url);
        context.startService(intent);
    }

    public static void setProgressListener(ProgressListener progressListener) {
        DownLoadIntentService.progressListener = progressListener;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String url = intent.getStringExtra(KEY_DOWNLOAD_URL);
        LogUtil.d(TAG, "progressMap -> " + progressMap + " _ " + progressMap.toString());
        if (progressMap.containsKey(TestDownLoad.getBaseUrl() + url)) {
            ControlProgressListener progressListener = progressMap.get(TestDownLoad.getBaseUrl() + url);
            if (progressListener != null) {
                progressListener.stop();
                progressMap.remove(TestDownLoad.getBaseUrl() + url);
                return;
            }
        }
        progressMap.remove(TestDownLoad.getBaseUrl() + url);
        ControlProgressListener progressListener = new ControlProgressListener();
        progressMap.put(TestDownLoad.getBaseUrl() + url, progressListener);
        TestDownLoad.test(url, progressListener);
    }

    public class ControlProgressListener implements ProgressListener {

        private boolean downing;

        public ControlProgressListener() {
            downing = true;
        }

        public void stop() {
            this.downing = false;
        }

        public boolean isDowning() {
            return downing;
        }

        @Override
        public void update(String url, long bytesRead, long contentLength, boolean done,String filePath) {
//            Log.e(TAG, "update -> url = " + url
//                    + "; bytesRead = " + bytesRead
//                    + "; contentLength = " + contentLength);
            if (DownLoadIntentService.progressListener != null) {
                DownLoadIntentService.progressListener.update(url, bytesRead, contentLength, done,filePath);
            }
            if (done) {
                progressMap.remove(url);
            }
        }
    }


}
