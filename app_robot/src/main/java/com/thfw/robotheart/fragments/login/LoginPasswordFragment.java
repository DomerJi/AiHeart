package com.thfw.robotheart.fragments.login;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.ui.base.BaseFragment;

public class LoginPasswordFragment extends BaseFragment {

    private TextView mTvLoginByMobile;
    private TextView mTvCountry;
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
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTvLoginByMobile = (TextView) findViewById(R.id.tv_login_by_mobile);
        mTvCountry = (TextView) findViewById(R.id.tv_country);
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
        Util.addUnderLine(mTvProduct3g,mTvProductUser, mTvProductMsg, mTvProductAgree);
    }

    @Override
    public void initData() {
        mTvLoginByMobile.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_MOBILE);
        });
    }
}