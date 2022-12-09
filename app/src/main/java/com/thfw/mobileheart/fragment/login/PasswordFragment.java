package com.thfw.mobileheart.fragment.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.ClickCountUtils;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.PrivateSetActivity;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.activity.login.ForgetPasswordActivity;
import com.thfw.mobileheart.activity.login.LoginActivity;
import com.thfw.mobileheart.constants.AgreeOn;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.utils.EditTextUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * Author:pengs
 * Date: 2021/8/4 16:19
 * Describe:密码登录
 */
public class PasswordFragment extends BaseFragment<LoginPresenter> implements LoginPresenter.LoginUi<TokenModel> {
    private ConstraintLayout mClTop;
    private RoundedImageView mRivIcon;
    private TextView mTvCountry;
    private EditText mEtMobile;
    private EditText mEtPassword;
    private Button mBtLogin;
    private TextView mTvLoginByMobile;
    private TextView mTvForgetPassword;
    private LinearLayout mClBottom;
    private RoundedImageView mRivWechat;
    private RoundedImageView mRivQq;
    // 协议
    private TextView mTvProduct3g;
    private TextView mTvProductUser;
    private TextView mTvProductMsg;
    private TextView mTvProductAgree;
    private String phone;
    private ImageView mIvSeePassword;
    private TextView mTvNoAccount;
    private CheckBox mCbProduct;

    @Override
    public int getContentView() {
        return R.layout.fragment_login_by_password;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {

        mClTop = (ConstraintLayout) findViewById(R.id.cl_top);
        mRivIcon = (RoundedImageView) findViewById(R.id.riv_icon);
        mTvCountry = (TextView) findViewById(R.id.tv_country);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mTvLoginByMobile = (TextView) findViewById(R.id.tv_login_by_mobile);
        mTvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        findViewById(R.id.tv_password_login_title).setOnClickListener(v -> {
            if (ClickCountUtils.click(10)) {
                startActivity(new Intent(mContext, PrivateSetActivity.class));
            }
        });
        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEtMobile.getText().toString();
                String password = mEtPassword.getText().toString();
                mBtLogin.setEnabled((phone != null && phone.length() > 3) && (password != null && password.length() > 5));
            }
        };
        mEtPassword.addTextChangedListener(myTextWatcher);
        mEtMobile.addTextChangedListener(myTextWatcher);

        mBtLogin.setEnabled(false);
        mTvLoginByMobile.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                LoginActivity activity = (LoginActivity) getActivity();
                activity.getFragmentLoader().load(LoginActivity.BY_OTHER);
                hideInput();
            }
        });
        mTvNoAccount = findViewById(R.id.tv_no_account);
        mTvNoAccount.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                LoginActivity activity = (LoginActivity) getActivity();
                Bundle bundle = new Bundle();
                bundle.putBoolean("register",true);
                activity.getFragmentLoader().load(LoginActivity.BY_MOBILE).setArguments(bundle);
                hideInput();
            }
        });
        mIvSeePassword = findViewById(R.id.iv_see_password);
        mIvSeePassword.setOnClickListener(v -> {

            mIvSeePassword.setSelected(!mIvSeePassword.isSelected());
            LogUtil.i("mIvSeePassword.isSelected() = " + mIvSeePassword.isSelected());
            if (mIvSeePassword.isSelected()) {
                // 如果选中，显示密码
                mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 否则隐藏密码
                mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            mEtPassword.setSelection(mEtPassword.getText().length());
        });

        mTvForgetPassword.setOnClickListener(v -> {
            ForgetPasswordActivity.startActivity(mContext, ForgetPasswordActivity.BY_MOBILE);
        });
        mTvProduct3g = (TextView) findViewById(R.id.tv_product_3g);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
        LoginActivity.agreedClickDialog(mCbProduct);
        mBtLogin.setOnClickListener(v -> {
            if (!mCbProduct.isChecked()) {
                LoginActivity.agreeDialog(getActivity(), new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        if (view.getId() == com.thfw.ui.R.id.tv_right) {
                            mCbProduct.setChecked(true);
                            mBtLogin.performClick();
                        }
                    }
                });
                return;
            }

            phone = mEtMobile.getText().toString();
            if (System.currentTimeMillis() - SharePreferenceUtil.getLong(phone, 0)
                    < HourUtil.LEN_HOUR) {
                HandlerUtil.getMainHandler().postDelayed(() -> {
                    ToastUtil.show("账号或密码错误");
                }, 1000);
                return;
            }
            String password = mEtPassword.getText().toString();
            LoadingDialog.show(getActivity(), "登录中");
            mPresenter.loginByPassword(phone, password);
        });
        EditTextUtil.setEditTextInhibitInputSpeChatAndSpace(mEtMobile);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPassword);
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (isVisible) {
            mEtMobile.setText(LoginActivity.INPUT_PHONE);
        } else {
            LoginActivity.INPUT_PHONE = mEtMobile.getText().toString();
        }
    }


    @Override
    public void initData() {
        mTvProduct3g.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_3G);
        });
        mTvProductUser.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_USER);
        });
        mTvProductMsg.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_MSG);
        });
        mTvProductAgree.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_AGREE);
        });
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(TokenModel data) {
        LoadingDialog.hide();
        LoginActivity.login(getActivity(), data, phone);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        if (HttpResult.isServerTimeNoValid(throwable.code)) {
            DialogFactory.createSimple(getActivity(), throwable.getMessage());
        } else {
            ToastUtil.show(throwable.getMessage());
        }
    }
}
