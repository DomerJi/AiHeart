package com.thfw.mobileheart.fragment.login;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.login.ForgetPasswordActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.utils.EditTextUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * Author:pengs
 * Date: 2021/8/4 16:19
 * Describe:重置密码
 */
public class SetPasswordFragment extends BaseFragment<LoginPresenter> implements LoginPresenter.LoginUi<CommonModel> {

    private TextView mTvProductAgree;
    private ConstraintLayout mClTop;
    private RoundedImageView mRivIconBg;
    private RoundedImageView mRivIcon;
    private TextView mTvCountry;
    private EditText mEtMobile;
    private TextView mTvPassword;
    private EditText mEtPassword;
    private Button mBtConfirm;
    private ImageView mIvSeePassword;
    private ImageView mIvSeePassword2;

    @Override
    public int getContentView() {
        return R.layout.fragment_forget_set_password;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
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

        EditTextUtil.setEditTextInhibitInputSpace(mEtPassword);
        EditTextUtil.setEditTextInhibitInputSpace(mEtMobile);
        mBtConfirm.setOnClickListener(v -> {
            if (getActivity() instanceof ForgetPasswordActivity) {

                ForgetPasswordActivity activity = (ForgetPasswordActivity) getActivity();
                String phone = activity.getFragmentLoader().get(ForgetPasswordActivity.KEY_PHONE);
                String code = activity.getFragmentLoader().get(ForgetPasswordActivity.KEY_CODE);
                String pass01 = mEtMobile.getText().toString();
                LoadingDialog.show(getActivity(), "重置中");
                mPresenter.setPasswordByCode(phone, code, pass01);
            }
        });

        checkPassword();
        mEtMobile.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPassword();
            }
        });

        mEtPassword.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPassword();
            }
        });


        mIvSeePassword = findViewById(R.id.iv_see_password);
        mIvSeePassword2 = findViewById(R.id.iv_see_password2);
        mIvSeePassword.setOnClickListener(v -> {

            mIvSeePassword.setSelected(!mIvSeePassword.isSelected());
            LogUtil.i("mIvSeePassword.isSelected() = " + mIvSeePassword.isSelected());
            if (mIvSeePassword.isSelected()) {
                // 如果选中，显示密码
                mEtMobile.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 否则隐藏密码
                mEtMobile.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            mEtMobile.setSelection(mEtMobile.getText().length());
        });

        mIvSeePassword2.setOnClickListener(v -> {

            mIvSeePassword2.setSelected(!mIvSeePassword2.isSelected());
            LogUtil.i("mIvSeePassword.isSelected() = " + mIvSeePassword2.isSelected());
            if (mIvSeePassword2.isSelected()) {
                // 如果选中，显示密码
                mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 否则隐藏密码
                mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            mEtPassword.setSelection(mEtPassword.getText().length());
        });


    }

    private void checkPassword() {
        String pass01 = mEtMobile.getText().toString();
        String pass02 = mEtPassword.getText().toString();
        mBtConfirm.setEnabled(RegularUtil.isPassword(pass01) && pass01.equals(pass02));
    }

    @Override
    public void initData() {

    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return SetPasswordFragment.this;
    }

    @Override
    public void onSuccess(CommonModel data) {
        LoadingDialog.hide();
        if (getActivity() instanceof ForgetPasswordActivity) {
            ForgetPasswordActivity activity = (ForgetPasswordActivity) getActivity();
            activity.getFragmentLoader().load(ForgetPasswordActivity.BY_SUSSES);
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        ToastUtil.show(throwable.getMessage());
        LoadingDialog.hide();
    }
}
