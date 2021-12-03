package com.thfw.robotheart.activitys.test;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.ui.base.RobotBaseActivity;

public class TestActivity extends RobotBaseActivity {

    @Override
    public int getContentView() {
        return R.layout.activity_test;
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