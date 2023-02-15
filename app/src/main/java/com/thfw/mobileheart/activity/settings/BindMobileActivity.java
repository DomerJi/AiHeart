package com.thfw.mobileheart.activity.settings;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.VerificationCodeView;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * 绑定手机号
 */
public class BindMobileActivity extends BaseActivity<LoginPresenter> implements LoginPresenter.LoginUi<CommonModel>, TimingHelper.WorkListener {
    public static final String KEY_RESULT = "key.result";
    private EditText mEtPhone;
    private Button mBtSubmit;
    private TextView mTvHint;
    private Button mBtGetCode;
    private String phone;
    private int secondCount;
    private String bindPhone;
    private VerificationCodeView mVFcode;

    @Override
    public int getContentView() {
        return R.layout.activity_bind_mobile;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {

        mEtPhone = (EditText) findViewById(R.id.et_mobile);
        mVFcode = (VerificationCodeView) findViewById(R.id.vfcode);
        mBtSubmit = (Button) findViewById(R.id.bt_bind);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
    }

    @Override
    public void initData() {
        String oldMobile = UserManager.getInstance().getUser().getMobile();
        if (!TextUtils.isEmpty(oldMobile)) {
            mTvHint.setText("当前手机号：" + oldMobile);
        }


        mVFcode.getGainVerification().setEnabled(false);

        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = mVFcode.getVerificationCode().getText().toString();
                boolean canSubmit = !TextUtils.isEmpty(code) && code.length() >= 4;
                phone = mEtPhone.getText().toString();
                mBtSubmit.setEnabled(canSubmit && RegularUtil.isPhone(phone));
                mVFcode.getGainVerification().setEnabled(RegularUtil.isPhone(phone));
            }
        };
        mVFcode.getVerificationCode().addTextChangedListener(myTextWatcher);
        mEtPhone.addTextChangedListener(myTextWatcher);
        mVFcode.setVerificationListener(new VerificationCodeView.VerificationListener() {
            @Override
            public void gainVerificationCodeClick() {
                sendCode();
            }
        });

        mBtSubmit.setEnabled(false);
        mBtSubmit.setOnClickListener(v -> {
            String code = mVFcode.getVerificationCode().toString();
            LoadingDialog.show(BindMobileActivity.this, "修改中...");
            bindPhone = phone;
            mPresenter.onBindPhone(bindPhone, code);
        });

    }

    private void sendCode() {
        new LoginPresenter(new LoginPresenter.LoginUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return BindMobileActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                mVFcode.sendGainVerificationStatus(true);
                ToastUtil.show("验证码发送成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                mVFcode.sendGainVerificationStatus(false);
                ToastUtil.show("验证码发送失败");
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

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        LhXkHelper.putAction(BindMobileActivity.class, new SpeechToAction("获取验证码,重新获取", () -> {
            mVFcode.getGainVerification().performClick();
        }));
        LhXkHelper.putAction(BindMobileActivity.class, new SpeechToAction("确定关联,确认关联", () -> {
            mBtSubmit.performClick();
        }));
    }
}