package com.thfw.mobileheart.fragment.login;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.activity.login.ForgetPasswordActivity;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;
import com.thfw.ui.widget.VerificationCodeView;

/**
 * Author:pengs
 * Date: 2021/8/4 16:19
 * Describe:忘记密码
 */
public class ForgetFragment extends BaseFragment {
    private ConstraintLayout mClTop;
    private RoundedImageView mRivIconBg;
    private RoundedImageView mRivIcon;
    private TextView mTvCountry;
    private EditText mEtMobile;
    private VerificationCodeView mVfcode;
    private Button mBtNext;

    @Override
    public int getContentView() {
        return R.layout.fragment_login_forget_password;
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
        mTvCountry = (TextView) findViewById(R.id.tv_country);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mVfcode = (VerificationCodeView) findViewById(R.id.vfcode);
        mBtNext = (Button) findViewById(R.id.bt_next);
        mBtNext.setOnClickListener(v -> {
            if (getActivity() instanceof ForgetPasswordActivity) {
                ForgetPasswordActivity activity = (ForgetPasswordActivity) getActivity();
                activity.getFragmentLoader().load(ForgetPasswordActivity.BY_SET_PASSWORD);
            }
        });
    }

    @Override
    public void initData() {

    }
}
