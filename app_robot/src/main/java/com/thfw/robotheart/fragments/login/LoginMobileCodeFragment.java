package com.thfw.robotheart.fragments.login;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.WebActivity;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.activitys.me.InfoActivity;
import com.thfw.robotheart.activitys.me.SelectOrganizationActivity;
import com.thfw.robotheart.constants.AgreeOn;
import com.thfw.ui.base.RobotBaseFragment;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.InputBoxSquareView;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.User;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

public class LoginMobileCodeFragment extends RobotBaseFragment<LoginPresenter>
        implements LoginPresenter.LoginUi<TokenModel>, TimingHelper.WorkListener {

    private InputBoxSquareView mIbvCode;
    private Button mBtGetCode;
    private TextView mTvRegetCode;
    private LinearLayout mClBottom;
    private RoundedImageView mRivWechat;
    private RoundedImageView mRivQq;
    private CheckBox mCbProduct;
    private TextView mTvProductUser;
    private TextView mTvProductMsg;
    private TextView mTvProductAgree;

    private String code;
    private String phone;

    private int secondCount = 60;
    private LinearLayout mLlLoginCenter;
    private TextView mTvLoginByPassword;
    private TextView mTvLoginByFace;

    public LoginMobileCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_login_mobile_code;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {
        mIbvCode = (InputBoxSquareView) findViewById(R.id.ibv_code);
        mBtGetCode = (Button) findViewById(R.id.bt_get_code);
        mTvRegetCode = (TextView) findViewById(R.id.tv_reget_code);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
        mLlLoginCenter = (LinearLayout) findViewById(R.id.ll_login_center);
        mTvLoginByPassword = (TextView) findViewById(R.id.tv_login_by_password);
        mTvLoginByFace = (TextView) findViewById(R.id.tv_login_by_face);
        Util.addUnderLine(mTvProductUser, mTvProductMsg, mTvProductAgree);
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
    public void initData() {

        mTvRegetCode.setText("重新获取(" + secondCount + ")");

        mTvRegetCode.setOnClickListener(v -> {
            if (secondCount <= 0) {
                LoginActivity loginActivity = (LoginActivity) getActivity();
                phone = loginActivity.getFragmentLoader().get(LoginActivity.KEY_PHONE_NUMBER);
                new LoginPresenter(new LoginPresenter.LoginUi<CommonModel>() {
                    @Override
                    public LifecycleProvider getLifecycleProvider() {
                        return LoginMobileCodeFragment.this;
                    }

                    @Override
                    public void onSuccess(CommonModel data) {
                        ToastUtil.show("获取验证码成功");
                        onVisible(true);
                    }

                    @Override
                    public void onFail(ResponeThrowable throwable) {

                    }
                }).onSendCode(phone, LoginPresenter.SendType.LOGIN);
            }
        });

        mIbvCode.setOnInputCompleteListener(new InputBoxSquareView.OnInputCompleteListener() {


            @Override
            public void onComplete(String text) {
                code = text;
                mBtGetCode.setEnabled(true);
                hideInput();
                mBtGetCode.performClick();
            }

            @Override
            public void onChanged(String text) {
                mBtGetCode.setEnabled(mIbvCode.inputCodeSuccess());
            }
        });

        mBtGetCode.setEnabled(false);
        mBtGetCode.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            phone = loginActivity.getFragmentLoader().get(LoginActivity.KEY_PHONE_NUMBER);
            LoadingDialog.show(getActivity(), "登录中");
            mPresenter.loginByMobile(phone, code);
        });

        mTvLoginByPassword.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_PASSWORD);
        });

        mTvLoginByFace.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_FACE);
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
            user.setMobile(phone);
            user.setLoginStatus(LoginStatus.LOGINED);
            UserManager.getInstance().login(user);
            LogUtil.d(TAG, "UserManager.getInstance().isLogin() = " + UserManager.getInstance().isLogin());
            if (data.isNoOrganization()) {
                SelectOrganizationActivity.isNoSetUserInfo = data.isNoSetUserInfo();
                SelectOrganizationActivity.startActivity(mContext, true);
            } else if (data.isNoSetUserInfo()) {
                InfoActivity.startActivityFirst(mContext);
            }
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

    @Override
    public void onArrive() {
        secondCount--;
        mTvRegetCode.setText("重新获取(" + secondCount + ")");
        if (secondCount <= 0) {
            mTvRegetCode.setText("重新获取");
            TimingHelper.getInstance().removeWorkArriveListener(this);
        }
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (isVisible) {
            secondCount = 60;
            mTvRegetCode.setText("重新获取(" + secondCount + ")");
            TimingHelper.getInstance().addWorkArriveListener(this);
        } else {
            TimingHelper.getInstance().removeWorkArriveListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        TimingHelper.getInstance().removeWorkArriveListener(this);
    }

    @Override
    public WorkInt workInt() {
        return WorkInt.SECOND;
    }
}