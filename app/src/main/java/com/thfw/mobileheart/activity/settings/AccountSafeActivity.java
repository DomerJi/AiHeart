package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

public class AccountSafeActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.LinearLayout mLlMobile;
    private android.widget.LinearLayout mLlWechat;
    private android.widget.TextView mTvBind;
    private android.widget.TextView mTvBindValue;
    private android.widget.LinearLayout mLlQq;
    private android.widget.TextView mTvBindQq;
    private android.widget.TextView mTvBindQqValue;
    private android.widget.LinearLayout mLlPassword;

    @Override
    public int getContentView() {
        return R.layout.activity_account_safe;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mLlMobile = (LinearLayout) findViewById(R.id.ll_mobile);
        mLlWechat = (LinearLayout) findViewById(R.id.ll_wechat);
        mTvBind = (TextView) findViewById(R.id.tv_bind);
        mTvBindValue = (TextView) findViewById(R.id.tv_bind_value);
        mLlQq = (LinearLayout) findViewById(R.id.ll_qq);
        mTvBindQq = (TextView) findViewById(R.id.tv_bind_qq);
        mTvBindQqValue = (TextView) findViewById(R.id.tv_bind_qq_value);
        mLlPassword = (LinearLayout) findViewById(R.id.ll_password);
        mLlMobile.setOnClickListener(v -> {
            startActivity(new Intent(mContext, BindMobileActivity.class));
        });
    }

    @Override
    public void initData() {

    }
}