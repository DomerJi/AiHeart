package com.thfw.robotheart.fragments.login;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.WebActivity;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.activitys.login.SetPasswordActivity;
import com.thfw.robotheart.constants.AgreeOn;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.utils.EditTextUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

public class LoginPasswordFragment extends RobotBaseFragment<LoginPresenter> implements LoginPresenter.LoginUi<TokenModel> {

    private TextView mTvLoginByMobile;
    private EditText mEtMobile;
    private EditText mEtPassword;
    private ImageView mIvSeePassword;
    private Button mBtLogin;
    private TextView mTvForgetPassword;
    private LinearLayout mClBottom;
    private RoundedImageView mRivWechat;
    private RoundedImageView mRivQq;
    private CheckBox mCbProduct;
    private TextView mTvProductUser;
    private TextView mTvProductMsg;
    private TextView mTvProductAgree;
    private TextView mTvLoginByFace;
    private TextView mTvLoginByPassword;

    public LoginPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_login_password;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {

        mTvLoginByMobile = (TextView) findViewById(R.id.tv_login_by_mobile);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mIvSeePassword = (ImageView) findViewById(R.id.iv_see_password);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mTvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);

        mTvLoginByFace = (TextView) findViewById(R.id.tv_login_by_face);
        mTvLoginByPassword = (TextView) findViewById(R.id.tv_login_by_password);
        mTvLoginByPassword.setVisibility(View.GONE);
        Util.addUnderLine(mTvProductUser, mTvProductMsg, mTvProductAgree);
        EditTextUtil.setEditTextInhibitInputSpeChatAndSpace(mEtMobile);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPassword);
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

        initAgreeClick();

    }

    private void initAgreeClick() {
        mTvProductAgree.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_AGREE);
        });
        mTvProductUser.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_USER);
        });
        mTvProductMsg.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_MSG);
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
        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEtMobile.getText().toString();
                String password = mEtPassword.getText().toString();
                boolean canLogin = (phone != null && phone.length() > 3) && !TextUtils.isEmpty(password) && password.length() >= 6;
                mBtLogin.setEnabled(canLogin);
            }
        };

        mEtMobile.addTextChangedListener(myTextWatcher);
        mEtPassword.addTextChangedListener(myTextWatcher);
        mBtLogin.setEnabled(false);
        mBtLogin.setOnClickListener(v -> {
            if (!CommonParameter.isValid()) {
                LoginActivity.showOrganIdNoValid(getActivity());
                return;
            }
            String phone = mEtMobile.getText().toString();
            String password = mEtPassword.getText().toString();
            LoadingDialog.show(getActivity(), "登录中");
            mPresenter.loginByPassword(phone, password);
        });
        mTvLoginByMobile.setOnClickListener(v -> {
            if (!CommonParameter.isValid()) {
                LoginActivity.showOrganIdNoValid(getActivity());
                return;
            }
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_MOBILE);
        });
        mTvLoginByFace.setOnClickListener(v -> {
            if (!CommonParameter.isValid()) {
                LoginActivity.showOrganIdNoValid(getActivity());
                return;
            }
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_FACE);
        });
        mTvForgetPassword.setOnClickListener(v -> {
            SetPasswordActivity.startActivity(mContext, SetPasswordActivity.SET_CODE);
        });
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(TokenModel data) {
        LoadingDialog.hide();
        if (!LoginActivity.login(getActivity(), data, mEtMobile.getText().toString())) {
            LoginActivity.onLoginFail(getActivity());
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        LogUtil.d(throwable.toString());
        ToastUtil.show(throwable.getMessage());
        if (HttpResult.isOrganValid(throwable.code)) {
            LoginActivity.showOrganIdNoValid(getActivity());
        } else {
            LoginActivity.onLoginFail(getActivity());
        }
    }


}