package com.thfw.mobileheart.fragment.login;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.login.ForgetPasswordActivity;
import com.thfw.ui.widget.VerificationCodeView;
import com.trello.rxlifecycle2.LifecycleProvider;

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
    private String phone;


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
                activity.getFragmentLoader().put(ForgetPasswordActivity.KEY_PHONE, phone);
                String code = mVfcode.getVerificationCode().getText().toString();
                activity.getFragmentLoader().put(ForgetPasswordActivity.KEY_CODE, code);
                activity.getFragmentLoader().load(ForgetPasswordActivity.BY_SET_PASSWORD);
            }
        });
        onCheckCanLogin();
        mVfcode.setVerificationListener(new VerificationCodeView.VerificationListener() {
            @Override
            public void gainVerificationCodeClick() {
                sendCode();
            }
        });

        mEtMobile.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onCheckCanLogin();
            }
        });
        mVfcode.getVerificationCode().addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onCheckCanLogin();
            }
        });
    }

    private void onCheckCanLogin() {
        phone = mEtMobile.getText().toString();
        mVfcode.getGainVerification().setEnabled(RegularUtil.isPhone(phone));
        mBtNext.setEnabled(RegularUtil.isPhone(phone) && mVfcode.getVerificationCode().getText().length() > 3);
    }


    private void sendCode() {
        new LoginPresenter(new LoginPresenter.LoginUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return ForgetFragment.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                mVfcode.sendGainVerificationStatus(true);
                ToastUtil.show("验证码发送成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {

            }
        }).onSendCode(mEtMobile.getText().toString(), LoginPresenter.SendType.SET_PASSWORD);
    }


    @Override
    public void initData() {

    }
}
