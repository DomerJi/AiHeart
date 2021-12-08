package com.thfw.robotheart.activitys.login;

import android.content.Context;
import android.content.Intent;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.fragments.login.LoginMobileCodeFragment;
import com.thfw.robotheart.fragments.login.LoginMobileFragment;
import com.thfw.robotheart.fragments.login.LoginPasswordFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.ui.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    public static final int BY_MOBILE = 0;
    public static final int BY_PASSWORD = 1;
    public static final int BY_FORGET = 2;
    public static final int BY_MOBILE_CODE = 4;

    public static final String KEY_PHONE_NUMBER = "phone_number";
    private int type;
    private FragmentLoader fragmentLoader;


    public static void startActivity(Context context, int type) {
        context.startActivity(new Intent(context, LoginActivity.class).putExtra(KEY_DATA, type));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        type = getIntent().getIntExtra(KEY_DATA, BY_MOBILE);
        fragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);

        fragmentLoader.add(BY_MOBILE, new LoginMobileFragment());
        fragmentLoader.add(BY_PASSWORD, new LoginPasswordFragment());
        fragmentLoader.add(BY_MOBILE_CODE, new LoginMobileCodeFragment());

        fragmentLoader.load(type);
    }

    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }

    public FragmentLoader getFragmentLoader() {
        return fragmentLoader;
    }

    @Override
    public void initData() {
    }
}