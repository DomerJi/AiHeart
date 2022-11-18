package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.models.VersionModel;
import com.thfw.base.net.download.ProgressListener;
import com.thfw.base.net.download.TestDownLoad;
import com.thfw.base.utils.AppUtils;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.service.AutoUpdateService;

import java.io.File;
import java.math.BigDecimal;

/**
 * 更新版本
 */
public class SystemAppActivity extends BaseActivity {
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

        /*为下载按钮设置监听*/
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("安装".equals(start.getText()) && mFilePath != null) {
                    checkPermission();
                    return;
                }

                if (!NetworkUtil.isNetConnected(mContext)) {
                    ToastUtil.show("请连接网络后再试");
                    return;
                }

                start.setEnabled(false);
                start.setText("正在下载...");
                mLlProgress.setVisibility(View.VISIBLE);

                TestDownLoad.test(BuglyUtil.getVersionModel().getDownloadUrl(), SystemAppActivity.this, new ProgressListener() {
                    @Override
                    public void update(String url, long bytesRead, long contentLength, boolean done, String filePath) {
                        Log.i("TestDownLoad", "bytesRead = " + bytesRead
                                + " contentLength = " + contentLength + " filePath = " + filePath);
                        if (!isMeResumed()) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (contentLength <= 0) {
                                    ToastUtil.show("资源错误");
                                    return;
                                }
                                int progress = (int) (bytesRead * 100 / contentLength);
                                Log.d(TAG, "progress  - > " + progress);

                                mPbDowning.setProgress(progress);
                                mTvProgress.setText(progress + "%");
                                if (progress == 100) {
                                    SystemAppActivity.this.mFilePath = TestDownLoad.getApkPath(BuglyUtil.getVersionModel().getDownloadUrl());
                                    start.setEnabled(true);
                                    start.setText("安装");
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_system_app;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
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