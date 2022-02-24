package com.thfw.robotheart.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.set.SystemAppActivity;
import com.thfw.robotheart.fragments.sets.SetUpdateFragment;
import com.thfw.robotheart.view.DialogRobotFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;

public class AutoUpdateService extends IntentService {
    private static final String ACTION_UPLOAD_IMG = "com.thfw.robotheart.service.action.AUTO_LOAD_APK";
    public static final String ACTION_CONTROL = "com.thfw.robotheart.service.action.CMD_CONTROL";

    private static final String TAG = AutoUpdateService.class.getSimpleName();
    public static final String AFTER_TIME = "after.install";

    private static Context mContext;

    public static class CmdControl {
        public static final int PLAY = 1;
        public static final int PAUSE = 0;
    }

    public static void startUpdate(Context context) {
        if (!SharePreferenceUtil.getBoolean(SetUpdateFragment.SET_AUTO_UPDATE_BOOLEAN, true)) {
            Log.e(TAG, "startUpdate 未开启自动更新");
            return;
        }
        DownloadTask task = Beta.getStrategyTask();
        if (task == null) {
            LogUtil.d(TAG, "startUpdate task == null");
            return;
        }
        if (task.getStatus() == DownloadTask.COMPLETE) {
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
            LogUtil.d(TAG, "showHintInstallApk 距离上次提示安装不足【两】小时");
            return;
        }
        if (context == null || !(context instanceof FragmentActivity)) {
            LogUtil.d(TAG, "showHintInstallApk context == null || !(context instanceof FragmentActivity)");
            return;
        }
        SharePreferenceUtil.setLong(AFTER_TIME, System.currentTimeMillis());

        DialogRobotFactory.createCustomDialog((FragmentActivity) context, new DialogRobotFactory.OnViewCallBack() {
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
                    DownloadTask downloadTask = Beta.getStrategyTask();
                    if (downloadTask != null && downloadTask.getStatus() == DownloadTask.COMPLETE) {
                        context.startActivity(new Intent(context, SystemAppActivity.class));
                        LogUtil.d(TAG, "现在 立即安装");
                    }
                }
            }
        });
    }

    /**
     * 弹框间隔提醒更新
     *
     * @return
     */
    public static boolean isLimitTimeInstall() {
        return (System.currentTimeMillis() - SharePreferenceUtil.getLong(AFTER_TIME, 0)) > 2 * 60 * 60 * 1000;
    }


    public AutoUpdateService() {
        super("AutoUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtil.d(TAG, "onHandleIntent start");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_IMG.equals(action)) {
                final int cmd = intent.getIntExtra(ACTION_CONTROL, -1);
                DownloadTask task = Beta.getStrategyTask();
                LogUtil.d(TAG, "onHandleIntent cmd = " + cmd);
                LogUtil.d(TAG, "onHandleIntent task.status = " + task.getStatus());
                if (task == null) {
                    Log.e(TAG, "task == null");
                    return;
                }
                switch (cmd) {
                    case CmdControl.PLAY:
                        if (task.getStatus() == DownloadTask.COMPLETE) {
                            LogUtil.d(TAG, "onHandleIntent complete = 已经下载完成可以提示用户安装/或自动安装");
                            showHintInstallApk(mContext);
                        } else if (task.getStatus() != DownloadTask.DOWNLOADING) {
                            handleAutoUpdate();
                            task = Beta.startDownload();
                        }
                        break;
                    case CmdControl.PAUSE:
                        if (task.getStatus() == DownloadTask.DOWNLOADING) {
                            task.stop();
                        }
                        break;
                }
            }
        }
        LogUtil.d(TAG, "onHandleIntent end");
    }

    private void handleAutoUpdate() {

        LogUtil.d(TAG, "handleAutoUpdate start");

        /*注册下载监听，监听下载事件*/
        Beta.registerDownloadListener(new DownloadListener() {
            @Override
            public void onReceive(DownloadTask task) {
                updateBtn(task);
                if (task.getTotalLength() <= 0) {
                    return;
                }
                int progress = (int) (task.getSavedLength() * 100 / task.getTotalLength());
                Log.d(TAG, "progress  - > " + progress);

            }

            @Override
            public void onCompleted(DownloadTask task) {
                updateBtn(task);
                Log.d(TAG, "progress - > onCompleted");
            }

            @Override
            public void onFailed(DownloadTask task, int code, String extMsg) {
                updateBtn(task);
                Log.d(TAG, "progress - > Failed");
            }
        });

        LogUtil.d(TAG, "handleAutoUpdate end");


    }

    public void updateBtn(DownloadTask task) {
        if (task == null) {
            return;
        }
        Log.d(TAG, "status = " + task.getStatus());
        /*根据下载任务状态设置按钮*/
        switch (task.getStatus()) {
            case DownloadTask.INIT:
            case DownloadTask.DELETED:
            case DownloadTask.FAILED: {
                // start.setText("立即更新");
            }
            break;
            case DownloadTask.COMPLETE: {
                // start.setText("安装");
            }
            break;
            case DownloadTask.DOWNLOADING: {
                // start.setText("暂停");
            }
            break;
            case DownloadTask.PAUSED: {
                // start.setText("继续下载");
            }
            break;
        }
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
}
