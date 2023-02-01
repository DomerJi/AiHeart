package com.thfw.mobileheart.fragment.login;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.activity.login.ForgetPasswordActivity;
import com.thfw.mobileheart.activity.login.LoginActivity;
import com.thfw.mobileheart.constants.AgreeOn;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.widget.VerificationCodeView;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * Author:pengs
 * Date: 2021/8/4 16:19
 * Describe:手机验证码登录
 */
public class MobileFragment extends BaseFragment<LoginPresenter> implements LoginPresenter.LoginUi<TokenModel> {
    private ConstraintLayout mClTop;
    private RoundedImageView mRivIcon;
    private TextView mTvCountry;
    private EditText mEtMobile;
    private VerificationCodeView mVfcode;
    private Button mBtLogin;
    private TextView mTvLoginByPassword;
    private TextView mTvForgetPassword;
    private LinearLayout mClBottom;
    private RoundedImageView mRivWechat;
    private RoundedImageView mRivQq;
    // 协议
    private TextView mTvProduct3g;
    private TextView mTvProductUser;
    private TextView mTvProductMsg;
    private TextView mTvProductAgree;
    private RoundedImageView mRivIconBg;
    private CheckBox mCheckBox;
    private String phone;
    private TextView mTvMobileTitle;

    @Override
    public int getContentView() {
        return R.layout.fragment_login_by_mobile;
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
        mVfcode = (VerificationCodeView) findViewById(R.id.vfcode);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mTvMobileTitle = (TextView) findViewById(R.id.tv_mobile_title);
        mTvLoginByPassword = (TextView) findViewById(R.id.tv_login_by_password);
        mTvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        mTvLoginByPassword.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                LoginActivity activity = (LoginActivity) getActivity();
                activity.getFragmentLoader().load(LoginActivity.BY_OTHER);
                hideInput();
            }
        });
        mVfcode.setVerificationListener(new VerificationCodeView.VerificationListener() {
            @Override
            public void gainVerificationCodeClick() {
                new LoginPresenter(new LoginPresenter.LoginUi<CommonModel>() {
                    @Override
                    public LifecycleProvider getLifecycleProvider() {
                        return MobileFragment.this;
                    }

                    @Override
                    public void onSuccess(CommonModel data) {
                        mVfcode.sendGainVerificationStatus(true);
                    }

                    @Override
                    public void onFail(ResponeThrowable throwable) {
                        mVfcode.sendGainVerificationStatus(false);
                    }
                }).onSendCode(phone, LoginPresenter.SendType.LOGIN);
            }
        });

        mVfcode.getVerificationCode().addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onCheckCanLogin();
            }
        });

        mEtMobile.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mVfcode.getGainVerification().setEnabled(RegularUtil.isPhone(mEtMobile.getText().toString()));
                onCheckCanLogin();
            }
        });

        onCheckCanLogin();

        mTvForgetPassword.setOnClickListener(v -> {
            ForgetPasswordActivity.startActivity(mContext, ForgetPasswordActivity.BY_MOBILE);
        });
        mTvProduct3g = (TextView) findViewById(R.id.tv_product_3g);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
        mRivIconBg = (RoundedImageView) findViewById(R.id.riv_icon_bg);
        mCheckBox = (CheckBox) findViewById(R.id.cb_product);
        LoginActivity.agreedClickDialog(mCheckBox);
        mBtLogin.setOnClickListener(v -> {
            if (!mCheckBox.isChecked()) {
                LoginActivity.agreeDialog(getActivity(), new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        if (view.getId() == com.thfw.ui.R.id.tv_right) {
                            mCheckBox.setChecked(true);
                            mBtLogin.performClick();
                        }
                    }
                });
                return;
            }
            LoadingDialog.show(getActivity(), "登录中");
            mPresenter.loginByMobile(phone, mVfcode.getVerificationCode().getText().toString());
        });

        getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideInput();
                return false;
            }
        });
    }


    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (isVisible) {
            if (RegularUtil.isNumber(LoginActivity.INPUT_PHONE)
                    && LoginActivity.INPUT_PHONE.startsWith("1")) {
                mEtMobile.setText(LoginActivity.INPUT_PHONE);
            }
            mCheckBox.setChecked(LoginActivity.mTvRightAgreed);
            if (!mCheckBox.isChecked()) {
                LoginActivity.agreeDialog(getActivity(), new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        if (view.getId() == com.thfw.ui.R.id.tv_right) {
                            mCheckBox.setChecked(true);
                        }
                    }
                });
            }
            Bundle bundle = getArguments();
            if (bundle != null && bundle.getBoolean("register")) {
                mTvMobileTitle.setText("免密登录/注册");
                mBtLogin.setText("登录/注册");
            } else {
                mTvMobileTitle.setText("免密登录");
                mBtLogin.setText("登录");
            }
        } else {
            LoginActivity.INPUT_PHONE = mEtMobile.getText().toString();
        }
    }

    private void onCheckCanLogin() {
        phone = mEtMobile.getText().toString();
        mBtLogin.setEnabled(RegularUtil.isPhone(phone) && mVfcode.getVerificationCode().getText().length() > 3);

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
