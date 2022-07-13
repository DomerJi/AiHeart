package com.thfw.robotheart.activitys.login;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.fragments.login.SetPasswordCodeFragment;
import com.thfw.robotheart.fragments.login.SetPasswordFirstFragment;
import com.thfw.robotheart.fragments.login.SetPasswordOriginFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.user.login.UserManager;

/**
 * 设置密码
 */
public class SetPasswordActivity extends RobotBaseActivity {

    public static final int SET_CODE = 0;
    public static final int SET_ORIGIN = 1;
    public static final int SET_FIRST = 2;
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.FrameLayout mFlContent;
    private FragmentLoader mFragmentLoader;

    public static void startActivity(Context context, int type) {
        context.startActivity(new Intent(context, SetPasswordActivity.class).putExtra(KEY_DATA, type));
    }

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
        if (!UserManager.getInstance().isLogin()) {
            mTitleRobotView.setCenterText("忘记密码");
        }
    }

    @Override
    public void initData() {
        int type = getIntent().getIntExtra(KEY_DATA, SET_ORIGIN);
        mFragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        mFragmentLoader.add(SET_CODE, new SetPasswordCodeFragment());
        mFragmentLoader.add(SET_ORIGIN, new SetPasswordOriginFragment());
        mFragmentLoader.add(SET_FIRST, new SetPasswordFirstFragment());
        mFragmentLoader.load(type);
    }

    public FragmentLoader getFragmentLoader() {
        return mFragmentLoader;
    }
}