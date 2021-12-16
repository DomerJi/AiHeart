package com.thfw.robotheart.fragments.login;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.activitys.login.SetPasswordActivity;
import com.thfw.ui.base.BaseFragment;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.User;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

public class LoginPasswordFragment extends BaseFragment<LoginPresenter> implements LoginPresenter.LoginUi<TokenModel> {

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
    private TextView mTvProduct3g;
    private TextView mTvProductUser;
    private TextView mTvProductMsg;
    private TextView mTvProductAgree;

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
        mTvProduct3g = (TextView) findViewById(R.id.tv_product_3g);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);

        Util.addUnderLine(mTvProduct3g, mTvProductUser, mTvProductMsg, mTvProductAgree);


    }

    @Override
    public void initData() {
        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEtMobile.getText().toString();
                String password = mEtPassword.getText().toString();
                boolean canLogin = RegularUtil.isPhone(phone) && !TextUtils.isEmpty(password) && password.length() >= 6;
                mBtLogin.setEnabled(canLogin);
            }
        };

        mEtMobile.addTextChangedListener(myTextWatcher);
        mEtPassword.addTextChangedListener(myTextWatcher);
        mBtLogin.setEnabled(false);
        mBtLogin.setOnClickListener(v -> {
            String phone = mEtMobile.getText().toString();
            String password = mEtPassword.getText().toString();
            LoadingDialog.show(getActivity(), "登录中");
            mPresenter.loginByPassword(phone, password);
        });
        mTvLoginByMobile.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_MOBILE);
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
        if (data != null && !TextUtils.isEmpty(data.token)) {
            ToastUtil.show("登录成功");
            User user = new User();
            user.setToken(data.token);
            user.setMobile(mEtMobile.getText().toString());
            user.setLoginStatus(LoginStatus.LOGINED);
            UserManager.getInstance().login(user);
            LogUtil.d(TAG, "UserManager.getInstance().isLogin() = " + UserManager.getInstance().isLogin());
            getActivity().finish();
        } else {
            ToastUtil.show("token 参数错误");
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        ToastUtil.show(throwable.getMessage());
    }
}