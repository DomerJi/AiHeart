package com.thfw.mobileheart.activity.login;

import android.content.Context;
import android.content.Intent;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.fragment.login.ForgetFragment;
import com.thfw.mobileheart.fragment.login.SetPasswordFragment;
import com.thfw.mobileheart.fragment.login.SetPasswordScusseFragment;
import com.thfw.mobileheart.util.FragmentLoader;
import com.thfw.ui.base.BaseActivity;

public class ForgetPasswordActivity extends BaseActivity {

    public static final int BY_MOBILE = 0;
    public static final int BY_SET_PASSWORD = 1;
    public static final int BY_SUSSES = 2;

    public static final String KEY_PHONE = "key_phone";
    public static final String KEY_CODE = "key_phone_code";
    private int type;
    private FragmentLoader fragmentLoader;

    public static void startActivity(Context context, int type) {
        context.startActivity(new Intent(context, ForgetPasswordActivity.class).putExtra(KEY_DATA, type));
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

        fragmentLoader.add(BY_MOBILE, new ForgetFragment());
        fragmentLoader.add(BY_SET_PASSWORD, new SetPasswordFragment());
        fragmentLoader.add(BY_SUSSES, new SetPasswordScusseFragment());

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