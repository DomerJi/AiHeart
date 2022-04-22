package com.thfw.mobileheart.fragment.login;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.TokenModel;
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
import com.thfw.ui.dialog.LoadingDialog;
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

        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEtMobile.getText().toString();
                String password = mEtPassword.getText().toString();
                mBtLogin.setEnabled(RegularUtil.isPhone(phone) && (password != null && password.length() > 5));
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

        mTvForgetPassword.setOnClickListener(v -> {
            ForgetPasswordActivity.startActivity(mContext, ForgetPasswordActivity.BY_MOBILE);
        });
        mTvProduct3g = (TextView) findViewById(R.id.tv_product_3g);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);

        mBtLogin.setOnClickListener(v -> {
            phone = mEtMobile.getText().toString();
            String password = mEtPassword.getText().toString();
            LoadingDialog.show(getActivity(), "登录中");
            mPresenter.loginByPassword(phone, password);
        });
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
        ToastUtil.show(throwable.getMessage());
    }
}
