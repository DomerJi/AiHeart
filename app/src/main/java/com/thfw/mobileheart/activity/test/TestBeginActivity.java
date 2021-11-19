package com.thfw.mobileheart.activity.test;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

public class TestBeginActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.ImageView mIvShare;
    private android.widget.Button mBtnBegin;

    @Override
    public int getContentView() {
        return R.layout.activity_test_begin;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mBtnBegin = (Button) findViewById(R.id.btn_begin);
    }

    @Override
    public void initData() {
        mBtnBegin.setOnClickListener(v -> {
            startActivity(new Intent(mContext, TestProgressIngActivity.class));
        });
    }
}