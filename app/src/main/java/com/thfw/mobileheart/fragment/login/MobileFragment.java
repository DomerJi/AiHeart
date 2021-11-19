package com.thfw.mobileheart.fragment.login;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.activity.login.ForgetPasswordActivity;
import com.thfw.mobileheart.activity.login.LoginActivity;
import com.thfw.mobileheart.constants.AgreeOn;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;
import com.thfw.ui.widget.VerificationCodeView;

/**
 * Author:pengs
 * Date: 2021/8/4 16:19
 * Describe:手机验证码登录
 */
public class MobileFragment extends BaseFragment {
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

    @Override
    public int getContentView() {
        return R.layout.fragment_login_by_mobile;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mClTop = (ConstraintLayout) findViewById(R.id.cl_top);
        mRivIcon = (RoundedImageView) findViewById(R.id.riv_icon);
        mTvCountry = (TextView) findViewById(R.id.tv_country);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mVfcode = (VerificationCodeView) findViewById(R.id.vfcode);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mTvLoginByPassword = (TextView) findViewById(R.id.tv_login_by_password);
        mTvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        mTvLoginByPassword.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                LoginActivity activity = (LoginActivity) getActivity();
                activity.getFragmentLoader().load(LoginActivity.BY_PASSWORD);
            }
        });
        mVfcode.setVerificationListener(new VerificationCodeView.VerificationListener() {
            @Override
            public void gainVerificationCodeClick() {

            }
        });
        mTvForgetPassword.setOnClickListener(v -> {
            ForgetPasswordActivity.startActivity(mContext, ForgetPasswordActivity.BY_MOBILE);
        });
        mTvProduct3g = (TextView) findViewById(R.id.tv_product_3g);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
        mRivIconBg = (RoundedImageView) findViewById(R.id.riv_icon_bg);
        mCheckBox = (CheckBox) findViewById(R.id.cb_product);
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

}
