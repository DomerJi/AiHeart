package com.thfw.robotheart.activitys.login;

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
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

public class BindPhoneActivity extends RobotBaseActivity<LoginPresenter>
        implements LoginPresenter.LoginUi<CommonModel>, TimingHelper.WorkListener {
    public static final String KEY_RESULT = "key.result";
    private TextView mTv01;
    private EditText mEtPhone;
    private TextView mTv02;
    private EditText mEtCode;
    private Button mBtSubmit;
    private TextView mTvSendSuccess;
    private Button mBtGetCode;
    private String phone;
    private int secondCount;
    private String bindPhone;

    @Override
    public int getContentView() {
        return R.layout.activity_bing_phone;
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
        mTvSendSuccess = (TextView) findViewById(R.id.tv_send_success);
        mBtGetCode = (Button) findViewById(R.id.bt_get_code);
    }

    @Override
    public void initData() {
        mTvSendSuccess.setText("更换手机号后，下次登录请使用新手机号登录；当前手机号："
                + UserManager.getInstance().getUser().getMobile());
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
            mBtGetCode.setEnabled(false);
            secondCount = 60;
            TimingHelper.getInstance().addWorkArriveListener(this);
        });


        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = mEtCode.getText().toString();
                boolean canSubmit = !TextUtils.isEmpty(code) && code.length() >= 4;
                mBtSubmit.setEnabled(canSubmit);
            }
        };
        mEtCode.addTextChangedListener(myTextWatcher);

        mBtSubmit.setEnabled(false);
        mBtSubmit.setOnClickListener(v -> {
            String code = mEtCode.getText().toString();
            LoadingDialog.show(BindPhoneActivity.this, "修改中...");
            bindPhone = phone;
            mPresenter.onBindPhone(bindPhone, code);
        });

    }

    private void sendCode() {
        new LoginPresenter(new LoginPresenter.LoginUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return BindPhoneActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                ToastUtil.show("验证码发送成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {

            }
        }).onSendCode(phone, LoginPresenter.SendType.BIND_PHONE);
    }


    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(CommonModel data) {
        LoadingDialog.hide();
        ToastUtil.show("修改成功");
        setResult(RESULT_OK, getIntent().putExtra(KEY_RESULT, bindPhone));
        finish();
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