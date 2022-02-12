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

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.ui.base.RobotBaseActivity;

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
        initData();
    }

    @Override
    public void initData() {
        if (Beta.getUpgradeInfo() == null) {
            ToastUtil.show("您的版本是最新的");
            finish();
            return;
        }
        version = findViewById(R.id.version);
        size = findViewById(R.id.size);
        content = findViewById(R.id.content);
        start = findViewById(R.id.start);

        /*获取下载任务，初始化界面信息*/
        updateBtn(Beta.getStrategyTask());
        /*获取策略信息，初始化界面信息*/
        version.setText(Beta.getUpgradeInfo().versionName);
        size.setText(toFileSizeM(Beta.getUpgradeInfo().fileSize + ""));
        content.setText(Beta.getUpgradeInfo().newFeature);

        /*为下载按钮设置监听*/
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTask task = Beta.startDownload();
                updateBtn(task);
                if (task.getStatus() == DownloadTask.DOWNLOADING) {
                    mLlProgress.setVisibility(View.VISIBLE);
                } else if (task.getStatus() == DownloadTask.COMPLETE) {
                    checkPermission();
                }
            }
        });
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
                mPbDowning.setProgress(progress);
                mTvProgress.setText(progress + "%");
            }

            @Override
            public void onCompleted(DownloadTask task) {
                updateBtn(task);
                Log.d(TAG, "progress - > onCompleted");
                mPbDowning.setProgress(100);
                mTvProgress.setText("100%");
            }

            @Override
            public void onFailed(DownloadTask task, int code, String extMsg) {
                updateBtn(task);
                Log.d(TAG, "progress - > Failed");
                mTvProgress.setText("failed");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*注销下载监听*/
        Beta.unregisterDownloadListener();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_system_app;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
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
                start.setText("立即更新");
            }
            break;
            case DownloadTask.COMPLETE: {
                start.setText("安装");
            }
            break;
            case DownloadTask.DOWNLOADING: {
                start.setText("暂停");
            }
            break;
            case DownloadTask.PAUSED: {
                start.setText("继续下载");
            }
            break;
        }
    }


    public void checkPermission() {
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
        DownloadTask task = Beta.startDownload();
        if (task != null && task.getStatus() == DownloadTask.COMPLETE) {
            Beta.installApk(task.getSaveFile());
            Log.e(TAG, "Beta.installApk ============================== ");
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