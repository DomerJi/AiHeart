package com.thfw.mobileheart.fragment.login;

import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.login.LoginActivity;

/**
 * Author:pengs
 * Date: 2022/3/1 14:46
 * Describe:其他方式登录
 */
public class OtherLoginFragment extends BaseFragment {
    private ConstraintLayout mClTop;
    private RoundedImageView mRivIconBg;
    private RoundedImageView mRivIcon;
    private TextView mTvMobileLogin;
    private TextView mTvAccountLogin;
    private TextView mTvFaceLogin;

    @Override
    public int getContentView() {
        return R.layout.fragment_other_login;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mClTop = (ConstraintLayout) findViewById(R.id.cl_top);
        mRivIconBg = (RoundedImageView) findViewById(R.id.riv_icon_bg);
        mRivIcon = (RoundedImageView) findViewById(R.id.riv_icon);
        mTvMobileLogin = (TextView) findViewById(R.id.tv_mobile_login);
        mTvAccountLogin = (TextView) findViewById(R.id.tv_account_login);
        mTvFaceLogin = (TextView) findViewById(R.id.tv_face_login);
    }

    @Override
    public void initData() {
        mTvMobileLogin.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                LoginActivity activity = (LoginActivity) getActivity();
                activity.getFragmentLoader().load(LoginActivity.BY_MOBILE);
            }
        });

        mTvAccountLogin.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                LoginActivity activity = (LoginActivity) getActivity();
                activity.getFragmentLoader().load(LoginActivity.BY_PASSWORD);
            }
        });

        mTvFaceLogin.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                LoginActivity activity = (LoginActivity) getActivity();
                activity.getFragmentLoader().load(LoginActivity.BY_FACE);
            }
        });
    }
}
