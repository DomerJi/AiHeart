package com.thfw.robotheart.activitys.login;

import android.widget.FrameLayout;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.fragments.login.SetPasswordCodeFragment;
import com.thfw.robotheart.fragments.login.SetPasswordOriginFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.BaseActivity;

public class SetPasswordActivity extends BaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.FrameLayout mFlContent;

    public static final int SET_CODE = 0;
    public static final int SET_ORIGIN = 1;
    private FragmentLoader mFragmentLoader;

    @Override
    public int getContentView() {
        return R.layout.activity_set_password;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
    }

    @Override
    public void initData() {
        mFragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        mFragmentLoader.add(SET_CODE, new SetPasswordCodeFragment());
        mFragmentLoader.add(SET_ORIGIN, new SetPasswordOriginFragment());
        mFragmentLoader.load(SET_ORIGIN);
    }

    public FragmentLoader getFragmentLoader() {
        return mFragmentLoader;
    }
}