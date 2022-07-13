package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.ui.widget.TitleView;
import com.thfw.user.login.UserManager;

/**
 * 账号管理
 */
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
    private LinearLayout mLlUserId;
    private TextView mTvUserIdValue;
    private TextView mTvPhone;
    private LinearLayout mLlUserAccount;
    private TextView mTvUserAccountValue;

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
        // start 微信QQ 暂时去除 =================================
        mLlWechat = (LinearLayout) findViewById(R.id.ll_wechat);
        mTvBind = (TextView) findViewById(R.id.tv_bind);
        mTvBindValue = (TextView) findViewById(R.id.tv_bind_value);
        mLlQq = (LinearLayout) findViewById(R.id.ll_qq);
        mTvBindQq = (TextView) findViewById(R.id.tv_bind_qq);
        mTvBindQqValue = (TextView) findViewById(R.id.tv_bind_qq_value);
        // end 微信QQ 暂时去除 =================================
        mLlPassword = (LinearLayout) findViewById(R.id.ll_password);

        mLlUserId = (LinearLayout) findViewById(R.id.ll_user_id);
        mTvUserIdValue = (TextView) findViewById(R.id.tv_user_id_value);
        mTvUserIdValue.setText(UserManager.getInstance().getUID());
        mTvPhone = (TextView) findViewById(R.id.tv_phone);

        mLlMobile.setOnClickListener(v -> {
            startActivity(new Intent(mContext, BindMobileActivity.class));
        });
        mLlPassword.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SetPasswordOriginActivity.class));
        });


        mLlUserAccount = (LinearLayout) findViewById(R.id.ll_user_account);
        mTvUserAccountValue = (TextView) findViewById(R.id.tv_user_account_value);


    }

    @Override
    protected void onResume() {
        super.onResume();
        String mobile = UserManager.getInstance().getUser().getMobile();
        if (!TextUtils.isEmpty(mobile)) {
            mTvPhone.setText(mobile);
        }

        String account = null;
        if (UserManager.getInstance().getUser().getUserInfo() != null) {
            account = UserManager.getInstance().getUser().getUserInfo().account;
        }
        if (!TextUtils.isEmpty(account)) {
            mTvUserAccountValue.setText(account);
        } else {
            mTvUserAccountValue.setText("暂无");
        }
    }

    @Override
    public void initData() {

    }
}