package com.thfw.mobileheart.activity.test;

import android.content.Context;
import android.content.Intent;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;

public class TestingActivity extends BaseActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestingActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_testing;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}