package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;

public class SettingActivity extends BaseActivity {

    private android.widget.LinearLayout mLlAccountSafe;
    private android.widget.LinearLayout mLlInfo;
    private android.widget.LinearLayout mLlNotice;
    private android.widget.LinearLayout mLlClearCache;
    private android.widget.LinearLayout mLlPrivacyPolicy;
    private android.widget.LinearLayout mLlAbout;
    private android.widget.Button mBtLogout;

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
        mLlNotice = (LinearLayout) findViewById(R.id.ll_notice);
        mLlClearCache = (LinearLayout) findViewById(R.id.ll_clear_cache);
        mLlPrivacyPolicy = (LinearLayout) findViewById(R.id.ll_privacy_policy);
        mLlAbout = (LinearLayout) findViewById(R.id.ll_about);
        mBtLogout = (Button) findViewById(R.id.bt_logout);
        mLlInfo.setOnClickListener(v -> {
            startActivity(new Intent(mContext, InfoActivity.class));
        });
    }

    @Override
    public void initData() {

    }
}