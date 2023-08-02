package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.thfw.base.base.IPresenter;
import com.thfw.base.base.SpeechToAction;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.UserInfoPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.TitleView;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * 账号管理
 */
public class AccountSafeActivity extends BaseActivity {


    private static final int BIND_CODE = 9;
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
        if (!MyApplication.getApp().isLan()) {
            mLlMobile.setOnClickListener(v -> {
                startActivityForResult(new Intent(mContext, BindMobileActivity.class), BIND_CODE);
            });
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case BIND_CODE:
                String phoneNumber = data.getStringExtra(BindMobileActivity.KEY_RESULT);
                mTvPhone.setText(phoneNumber);
                onUpdateInfo("mobile", phoneNumber);
                UserManager.getInstance().getUser().getUserInfo().mobile = phoneNumber;
                UserManager.getInstance().notifyUserInfo();
                break;
        }
    }


    public void onUpdateInfo(String key, Object value) {
        onUpdateInfo(NetParams.crete()
                .add("key", key)
                .add("value", value));
    }

    public void onUpdateInfo(NetParams netParams) {

        LoadingDialog.show(this, "保存中...");
        new UserInfoPresenter(new UserInfoPresenter.UserInfoUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return AccountSafeActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                LoadingDialog.hide();
                ToastUtil.show("保存成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LoadingDialog.hide();
                ToastUtil.show("保存失败");

            }
        }).onUpdate(netParams);
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        LhXkHelper.putAction(AccountSafeActivity.class, new SpeechToAction("手机号码", () -> {
            mLlMobile.performClick();
        }));
        LhXkHelper.putAction(AccountSafeActivity.class, new SpeechToAction("登录密码", () -> {
            mLlPassword.performClick();
        }));
    }
}