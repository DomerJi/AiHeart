package com.thfw.robotheart.fragments.login;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NumberUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.utils.EditTextUtil;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * Author:pengs
 * Date: 2021/12/7 9:19
 * Describe:Todo
 */
public class SetPasswordCodeFragment extends RobotBaseFragment<LoginPresenter>
        implements LoginPresenter.LoginUi<CommonModel>, TimingHelper.WorkListener {
    private TextView mTv01;
    private EditText mEtPhone;
    private TextView mTv02;
    private EditText mEtCode;
    private Button mBtSubmit;
    private TextView mTv03;
    private EditText mEtPassword;
    private TextView mTvSendSuccess;
    private Button mBtGetCode;
    private String phone;
    private int secondCount;

    @Override
    public int getContentView() {
        return R.layout.fragment_set_password_code;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {

        mTv01 = (TextView) findViewById(R.id.tv_01);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mTv02 = (TextView) findViewById(R.id.tv_02);
        mEtCode = (EditText) findViewById(R.id.et_code);
        mBtSubmit = (Button) findViewById(R.id.bt_submit);
        mTv03 = (TextView) findViewById(R.id.tv_03);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mTvSendSuccess = (TextView) findViewById(R.id.tv_send_success);
        mBtGetCode = (Button) findViewById(R.id.bt_get_code);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPassword);
        EditTextUtil.setEditTextInhibitInputSpace(mEtCode);
    }

    @Override
    public void initData() {
        if (!UserManager.getInstance().isLogin()) {
            mTvSendSuccess.setVisibility(View.INVISIBLE);
            mBtGetCode.setVisibility(View.VISIBLE);
            mBtGetCode.setEnabled(false);
            mEtPhone.addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    phone = s.toString();
                    mBtGetCode.setEnabled(RegularUtil.isPhone(phone));
                }
            });
            mBtGetCode.setOnClickListener(v -> {
                sendCode();
                hideInput();
                mBtGetCode.setEnabled(false);
                secondCount = 60;
                TimingHelper.getInstance().addWorkArriveListener(this);
            });
        } else {
            mBtGetCode.setVisibility(View.GONE);
            phone = UserManager.getInstance().getUser().getMobile();
            LogUtil.d(TAG, "phone = " + phone);
            mEtPhone.setFocusable(false);
            mEtPhone.setClickable(false);
            mEtPhone.setText(NumberUtil.getConfoundAccount(phone));
            sendCode();
        }

        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = mEtCode.getText().toString();
                String password = mEtPassword.getText().toString();
                boolean canSubmit = RegularUtil.isPassword(password) && !TextUtils.isEmpty(code) && code.length() >= 4;
                mBtSubmit.setEnabled(canSubmit);
            }
        };
        mEtCode.addTextChangedListener(myTextWatcher);
        mEtPassword.addTextChangedListener(myTextWatcher);

        mBtSubmit.setEnabled(false);
        mBtSubmit.setOnClickListener(v -> {
            String code = mEtCode.getText().toString();
            String password = mEtPassword.getText().toString();
            LoadingDialog.show(getActivity(), "正在修改");
            mPresenter.setPasswordByCode(phone, code, password);
        });

    }

    private void sendCode() {
        new LoginPresenter(new LoginPresenter.LoginUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return SetPasswordCodeFragment.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                ToastUtil.show("验证码发送成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {

            }
        }).onSendCode(phone, LoginPresenter.SendType.SET_PASSWORD);
    }


    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(CommonModel data) {
        LoadingDialog.hide();
        ToastUtil.show("修改成功");
        getActivity().finish();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        ToastUtil.show("修改失败:" + throwable.getMessage());
    }

    @Override
    public void onArrive() {
        secondCount--;
        mBtGetCode.setText("重新获取(" + secondCount + ")");
        if (secondCount <= 0) {
            mBtGetCode.setText("重新获取");
            mBtGetCode.setEnabled(true);
            TimingHelper.getInstance().removeWorkArriveListener(this);
        }
    }

    @Override
    public WorkInt workInt() {
        return WorkInt.SECOND;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TimingHelper.getInstance().removeWorkArriveListener(this);
    }
}
