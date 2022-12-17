package com.thfw.robotheart.activitys.set;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.DownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.thfw.base.base.IPresenter;
import com.thfw.base.models.VersionModel;
import com.thfw.base.net.download.TestDownLoad;
import com.thfw.base.utils.AppUtils;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.service.AutoUpdateService;
import com.thfw.robotheart.util.Dormant;

import java.io.File;
import java.math.BigDecimal;

/**
 * 更新版本
 */
public class SystemAppActivity extends RobotBaseActivity {
    private static final String TAG = SystemAppActivity.class.getSimpleName();

    private TextView title;
    private TextView version;
    private TextView size;
    private TextView time;
    private TextView content;
    private TextView start;
    private TextView mTvProgress;
    private LinearLayout mLlProgress;
    private ProgressBar mPbDowning;

    private String mFilePath;
    private DownloadTask downloadTask;

    /**
     * 将文件大小kb转换成M
     *
     * @param fileSizeKb
     * @return fileSizeM
     */
    public static String toFileSizeM(String fileSizeKb) {
        String fileSizeM = "";
        if (fileSizeKb == null)
            fileSizeM = "0" + "B";
        long fSize = Long.valueOf(fileSizeKb);
        if (fSize < 1024) {
            fileSizeM = fileSizeKb + "B";//字节
        } else if (fSize > 1024) {
            BigDecimal bg = new BigDecimal(fSize);
            BigDecimal flsize = new BigDecimal(fSize);
            BigDecimal temp = new BigDecimal(1024);
            if (-1 == temp.compareTo(flsize) || 0 == temp.compareTo(flsize)) {
                //四色五入保留2位小数(根据需求随意调整)
                flsize = bg.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
                fileSizeM = flsize + "KB";
            }
            if (-1 == temp.compareTo(flsize) || 0 == temp.compareTo(flsize)) { //MB
                //四色五入保留2位小数(根据需求随意调整)
                flsize = flsize.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
                fileSizeM = flsize + "M";
            }
        }
        return fileSizeM;
    }

    @Override
    public void initView() {

        mTvProgress = findViewById(R.id.tv_down_progress);
        mLlProgress = (LinearLayout) findViewById(R.id.ll_progress);
        mPbDowning = (ProgressBar) findViewById(R.id.pb_downing);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_system_app;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initData() {
        if (BuglyUtil.getVersionModel() == null) {
            ToastUtil.show("您的版本是最新的");
            finish();
            return;
        }

        version = findViewById(R.id.version);
        size = findViewById(R.id.size);
        content = findViewById(R.id.content);
        start = findViewById(R.id.start);

        VersionModel versionModel = BuglyUtil.getVersionModel();

        /*获取策略信息，初始化界面信息*/
        version.setText(versionModel.getVersion());
        size.setText(toFileSizeM(versionModel.getSize()));
        content.setText(versionModel.getDes());

        try {
            initDownLoader();
        } catch (Exception e) {
            finish();
            ToastUtil.show("升级失败");
            return;
        }
        /*为下载按钮设置监听*/
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int status = downloadTask.getStatus();
                if (status == FileDownloadStatus.completed) {
                    File file = new File(downloadTask.getPath());
                    if (file.exists() && file.length() <= 0) {
                        downloadTask.start();
                        return;
                    }
                    checkPermission();
                } else if (FileDownloadStatus.isIng(status)) {
                    FileDownloader.getImpl().pause(downloadTask.getId());
                } else {
                    if (!downloadTask.isRunning()) {
                        if (downloadTask.isUsing()) {
                            if (downloadTask.reuse()) {
                                downloadTask.start();
                            }
                        } else {

                            downloadTask.start();
                        }
                    }
                }
                refreshStatus();
                if (!NetworkUtil.isNetConnected(mContext)) {
                    ToastUtil.show("请连接网络后再试");
                    return;
                }
            }
        });
        if (downloadTask.isUsing()) {
            if (downloadTask.reuse()) {
                downloadTask.start();
            }
        } else {
            downloadTask.start();
        }
        refreshStatus();
    }

    private void initDownLoader() {

        downloadTask = (DownloadTask) BuglyUtil.getApkDownLoad()
                .setCallbackProgressTimes(1000)
                .setMinIntervalUpdateSpeed(300)
                .setCallbackProgressMinInterval(300)
                .setWifiRequired(false);
        downloadTask.setListener(new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                LogUtil.i(TAG, "pending soFarBytes = " + soFarBytes + " ; totalBytes = " + totalBytes);
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                LogUtil.i(TAG, "connected soFarBytes = " + soFarBytes + " ; totalBytes = " + totalBytes);
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                LogUtil.i(TAG, "progress soFarBytes = " + soFarBytes + " ; totalBytes = " + totalBytes);
                if (!isMeResumed()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (totalBytes <= 0) {
                            start.setEnabled(true);
                            start.setText("立即更新");
                            ToastUtil.show("资源错误");
                            return;
                        }
                        BigDecimal progressObj = new BigDecimal(soFarBytes)
                                .multiply(new BigDecimal(100))
                                .divide(new BigDecimal(totalBytes), 2, BigDecimal.ROUND_HALF_UP);

                        int progress = (int) Math.round(progressObj.doubleValue());

                        Log.d(TAG, "progress  - > " + progress);
                        Dormant.reset();
                        mPbDowning.setProgress(progress);
                        mTvProgress.setText(progressObj.doubleValue() + "%");
                        if (soFarBytes == totalBytes) {
                            refreshStatus();
                        }
                    }
                });
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
            }

            @Override
            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                LogUtil.i(TAG, "completed ");
                mPbDowning.setProgress(100);
                mTvProgress.setText("100%");
                refreshStatus();
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                refreshStatus();
                LogUtil.i(TAG, "paused soFarBytes = " + soFarBytes + " ; totalBytes = " + totalBytes);

            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        });
    }


    private void refreshStatus() {
        runOnUiThread(() -> {
            int status = downloadTask.getStatus();
            if (status == FileDownloadStatus.completed || downloadTask.isReusedOldFile()) {
                File file = new File(downloadTask.getPath());
                if (!file.exists() || file.length() <= 100) {
                    start.setEnabled(true);
                    start.setText("重试(资源错误)");
                } else {
                    start.setEnabled(true);
                    start.setText("安装");
                }
            } else if (FileDownloadStatus.isIng(status)) {
                mLlProgress.setVisibility(View.VISIBLE);
                start.setEnabled(true);
                start.setText("暂停");
            } else {
                start.setEnabled(true);
                start.setText("开始下载");
            }
        });

    }

    @Override
    public void onDestroy() {
        if (downloadTask != null) {
            downloadTask.pause();
        }
        super.onDestroy();
    }


    public void checkPermission() {
        SystemAppActivity.this.mFilePath = TestDownLoad.getApkPath(BuglyUtil.getVersionModel().getDownloadUrl());
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                //没有权限让调到设置页面进行开启权限；
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                startActivityForResult(intent, 10086);
            } else {
                install();
            }
        } else {
            install();
        }
    }

    private void install() {

        if (mFilePath != null) {
            SharePreferenceUtil.setLong(AutoUpdateService.AFTER_TIME, 0);
            AppUtils.installApk(SystemAppActivity.this, mFilePath);
            Log.e(TAG, "Beta.installApk ============================== " + mFilePath + "_" + new File(mFilePath).length());
        } else {
            Log.e(TAG, "Beta.installApk ============================== fail");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10086) {
            //返回权限后，执行自己的逻辑；
            install();
        }
    }


}