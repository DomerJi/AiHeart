package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.tools.PictureFileUtils;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.constants.AgreeOn;
import com.thfw.mobileheart.util.FileSizeUtil;
import com.thfw.ui.base.BaseActivity;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;

public class SettingActivity extends BaseActivity {

    private android.widget.LinearLayout mLlAccountSafe;
    private android.widget.LinearLayout mLlInfo;
    private android.widget.LinearLayout mLlClearCache;
    private android.widget.LinearLayout mLlPrivacyPolicy;
    private android.widget.LinearLayout mLlAbout;
    private android.widget.Button mBtLogout;
    private LinearLayout mLlVersion;
    private android.widget.TextView mTvVersion;
    private TextView mTvCache;
    private long fileSize;
    private FileSizeUtil.AppCache appCache;
    private Handler mHandler;
    private long deleteNum;

    @Override
    public int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mLlAccountSafe = (LinearLayout) findViewById(R.id.ll_account_safe);
        mLlInfo = (LinearLayout) findViewById(R.id.ll_info);
        mLlClearCache = (LinearLayout) findViewById(R.id.ll_clear_cache);
        mLlPrivacyPolicy = (LinearLayout) findViewById(R.id.ll_privacy_policy);
        mLlAbout = (LinearLayout) findViewById(R.id.ll_about);
        mBtLogout = (Button) findViewById(R.id.bt_logout);
        mLlInfo.setOnClickListener(v -> {
            startActivity(new Intent(mContext, InfoActivity.class));
        });
        mLlAccountSafe.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AccountSafeActivity.class));
        });
        mLlPrivacyPolicy.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_MSG);
        });
        mLlAbout.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_ABOUT);
        });
        mBtLogout.setOnClickListener(v -> {
            UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
            finish();
        });
        appCache = new FileSizeUtil.AppCache();

        mTvCache = (TextView) findViewById(R.id.tv_cache);
        fileSize = appCache.getAppCacheLong();
        mTvCache.setText(appCache.formatFileSize(fileSize));
        mLlClearCache.setOnClickListener(v -> {
            clearCache();
        });

        mLlVersion = (LinearLayout) findViewById(R.id.ll_version);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mTvVersion.setText(Util.getAppVersion(mContext));
        mLlVersion.setOnClickListener(v -> {
            ToastUtil.show("检测更新");
        });

    }

    private void clearCache() {
        if (fileSize <= 0) {
            return;
        }
        deleteNum = fileSize / 10;
        if (deleteNum <= 0) {
            deleteNum = 1;
        }
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (mHandler == null || fileSize < 0) {
                    return;
                }
                fileSize = fileSize - deleteNum;

                if (fileSize > 0) {
                    sendEmptyMessageDelayed(0, 90);
                } else {
                    fileSize = 0;
                }
                mTvCache.setText(appCache.formatFileSize(fileSize));
            }
        };
        mHandler.sendEmptyMessageDelayed(0, 0);
        appCache.clearAppCache(new FileSizeUtil.AppCache.clearCacheListener() {
            @Override
            public void onSuccess(String fileSize) {
                LogUtil.d(TAG, "clearAppCache onSuccess ");
                PictureFileUtils.deleteAllCacheDirFile(mContext);
            }

            @Override
            public void onFail() {
                LogUtil.d(TAG, "clearAppCache onFail ");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            fileSize = -1;
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void initData() {

    }
}