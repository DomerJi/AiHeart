package com.thfw.robotheart.fragments.login;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.WebActivity;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.constants.AgreeOn;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.utils.EditTextUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

public class LoginMobileFragment extends RobotBaseFragment<LoginPresenter> implements LoginPresenter.LoginUi<CommonModel> {

    private TextView mTvCountry;
    private EditText mEtMobile;
    private Button mBtGetCode;
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
    private TextView mTvLoginByMobile;

    public LoginMobileFragment() {
        // Required empty public constructor
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_login_mobile;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {

        mTvLoginByPassword = (TextView) findViewById(R.id.tv_login_by_password);
        mTvCountry = (TextView) findViewById(R.id.tv_country);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mBtGetCode = (Button) findViewById(R.id.bt_get_code);
        mTvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
        Util.addUnderLine(mTvProductUser, mTvProductMsg, mTvProductAgree);
        mTvLoginByFace = (TextView) findViewById(R.id.tv_login_by_face);
        mTvLoginByMobile = (TextView) findViewById(R.id.tv_login_by_mobile);
        mTvLoginByMobile.setVisibility(View.GONE);
        initAgreeClick();
        EditTextUtil.setEditTextInhibitInputSpace(mEtMobile);
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
    public void initData() {
        mTvLoginByPassword.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_PASSWORD);
        });
        mBtGetCode.setEnabled(false);
        mBtGetCode.setOnClickListener(v -> {
            LoadingDialog.show(getActivity(), "发送验证码");
            mPresenter.onSendCode(getPhoneNumber(), LoginPresenter.SendType.LOGIN);
        });

        mEtMobile.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtGetCode.setEnabled(RegularUtil.isPhone(String.valueOf(s)));
            }
        });

        mTvLoginByFace.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_FACE);
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
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    private String getPhoneNumber() {
        return mEtMobile.getText().toString();
    }

    private void toMobileCode() {
        LoginActivity loginActivity = (LoginActivity) getActivity();
        loginActivity.getFragmentLoader().put(LoginActivity.KEY_PHONE_NUMBER, getPhoneNumber());
        loginActivity.getFragmentLoader().load(LoginActivity.BY_MOBILE_CODE);
    }

    @Override
    public void onSuccess(CommonModel data) {
        LoadingDialog.hide();
        ToastUtil.show("验证码发送成功");
        toMobileCode();

    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        if (NetworkUtil.isNetConnected(mContext)) {
            toMobileCode();
        } else {
            ToastUtil.show(throwable.getMessage());
        }
    }
}