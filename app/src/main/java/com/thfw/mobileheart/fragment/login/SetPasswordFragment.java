package com.thfw.mobileheart.fragment.login;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.activity.login.ForgetPasswordActivity;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseFragment;

/**
 * Author:pengs
 * Date: 2021/8/4 16:19
 * Describe:重置密码
 */
public class SetPasswordFragment extends BaseFragment {

    private TextView mTvProductAgree;
    private ConstraintLayout mClTop;
    private RoundedImageView mRivIconBg;
    private RoundedImageView mRivIcon;
    private TextView mTvCountry;
    private EditText mEtMobile;
    private TextView mTvPassword;
    private EditText mEtPassword;
    private Button mBtConfirm;

    @Override
    public int getContentView() {
        return R.layout.fragment_forget_set_password;
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
        mTvPassword = (TextView) findViewById(R.id.tv_password);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtConfirm = (Button) findViewById(R.id.bt_confirm);

        mBtConfirm.setOnClickListener(v -> {
            if (getActivity() instanceof ForgetPasswordActivity) {
                ForgetPasswordActivity activity = (ForgetPasswordActivity) getActivity();
                activity.getFragmentLoader().load(ForgetPasswordActivity.BY_SUSSES);
            }
        });
    }

    @Override
    public void initData() {

    }
}
