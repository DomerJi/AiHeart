package com.thfw.mobileheart.activity.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.DownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.thfw.base.models.VersionModel;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.settings.SystemAppActivity;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;

public class AutoUpdateService extends IntentService {
    public static final String ACTION_CONTROL = "com.thfw.robotheart.service.action.CMD_CONTROL";
    public static final String AFTER_TIME = "after.install";
    private static final String ACTION_UPLOAD_IMG = "com.thfw.robotheart.service.action.AUTO_LOAD_APK";
    private static final String TAG = AutoUpdateService.class.getSimpleName();
    private static Context mContext;

    public AutoUpdateService() {
        super("AutoUpdateService");
    }

    public static void startUpdate(Context context) {
        VersionModel versionModel = BuglyUtil.getVersionModel();

        if (versionModel == null) {
            LogUtil.d(TAG, "startUpdate versionModel == null");
            return;
        }

        DownloadTask downloadTask = (DownloadTask) BuglyUtil.getApkDownLoad();
        if (downloadTask == null) {
            return;
        }
        if (downloadTask.isRunning()) {
            return;
        }
        if (downloadTask.getStatus() == FileDownloadStatus.completed) {
            LogUtil.d(TAG, "startUpdate complete = 已经下载完成可以提示用户安装/或自动安装");
            showHintInstallApk(context);
            return;
        }
        if (!NetworkUtil.isWifiConnected(context)) {
            LogUtil.d(TAG, "startUpdate isWifiConnected = false");
            return;
        }

        mContext = context;
        Intent intent = new Intent(context, AutoUpdateService.class);
        intent.setAction(ACTION_UPLOAD_IMG);
        intent.putExtra(ACTION_CONTROL, CmdControl.PLAY);
        context.startService(intent);
    }

    public static void stopUpdate(Context context) {
        Intent intent = new Intent(context, AutoUpdateService.class);
        intent.setAction(ACTION_UPLOAD_IMG);
        intent.putExtra(ACTION_CONTROL, CmdControl.PAUSE);
        context.startService(intent);
    }

    private static void showHintInstallApk(Context context) {
        if (!isLimitTimeInstall()) {
            LogUtil.d(TAG, "showHintInstallApk 距离上次提示安装不足【23】小时");
            return;
        }
        if (context == null || !(context instanceof FragmentActivity)) {
            LogUtil.d(TAG, "showHintInstallApk context == null || !(context instanceof FragmentActivity)");
            return;
        }
        SharePreferenceUtil.setLong(AFTER_TIME, System.currentTimeMillis());

        DialogFactory.createCustomDialog((FragmentActivity) context, new DialogFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvHint.setText(R.string.finishDownlaodApk);
                mTvTitle.setVisibility(View.GONE);
                mTvLeft.setText("以后");
                mTvRight.setText("现在");
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                tDialog.dismiss();
                if (view.getId() == R.id.tv_left) {
                    LogUtil.d(TAG, "以后 立即安装");
                } else {
                    context.startActivity(new Intent(context, SystemAppActivity.class));
                }
            }
        }, false);
    }

    /**
     * 弹框间隔提醒更新
     *
     * @return
     */
    public static boolean isLimitTimeInstall() {
        return (System.currentTimeMillis() - SharePreferenceUtil.getLong(AFTER_TIME, 0)) > 23 * 60 * 60 * 1000;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtil.d(TAG, "onHandleIntent start");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_IMG.equals(action)) {
                final int cmd = intent.getIntExtra(ACTION_CONTROL, -1);
                DownloadTask downloadTask = (DownloadTask) BuglyUtil.getApkDownLoad()
                        .setCallbackProgressTimes(100)
                        .setMinIntervalUpdateSpeed(1000)
                        .setCallbackProgressMinInterval(300)
                        .setWifiRequired(true);
                LogUtil.d(TAG, "onHandleIntent downloadTask.status = " + downloadTask.getStatus());
                switch (cmd) {
                    case CmdControl.PLAY:

                        downloadTask.setListener(new FileDownloadSampleListener() {
                            @Override
                            protected void completed(BaseDownloadTask task) {
                                super.completed(task);
                                showHintInstallApk(mContext);
                            }
                        });
                        if (downloadTask.isUsing()) {
                            if (downloadTask.reuse()) {
                                downloadTask.start();
                            }
                        } else {
                            downloadTask.start();
                        }
                        break;
                    case CmdControl.PAUSE:
                        if (downloadTask.isRunning()) {
                            FileDownloader.getImpl().pause(downloadTask.getId());
                        }
                        break;
                }
            }
        }
        LogUtil.d(TAG, "onHandleIntent end");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    public static class CmdControl {
        public static final int PLAY = 1;
        public static final int PAUSE = 0;
    }
}
