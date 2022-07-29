package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.StringUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.utils.EditTextUtil;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

public class SetPasswordOriginActivity extends BaseActivity<LoginPresenter> implements LoginPresenter.LoginUi<CommonModel> {
    private EditText mEtPasswordOrigin;
    private EditText mEtPasswordNew;
    private EditText mEtPasswordNewConfirm;
    private Button mBtSubmit;
    private com.thfw.ui.widget.TitleView mTitleView;
    private EditText mEtNewPassword;
    private EditText mEtNewPassword01;
    private android.widget.TextView mTvHint;
    private TextView mTvForgetPassword2;
    private ImageView mIvSeePassword2;
    private ImageView mIvSeePassword;

    @Override
    public int getContentView() {
        return R.layout.activity_set_password_origin;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {

        mEtPasswordOrigin = (EditText) findViewById(R.id.et_password);
        mEtPasswordNew = (EditText) findViewById(R.id.et_new_password);
        mEtPasswordNewConfirm = (EditText) findViewById(R.id.et_new_password01);
        mTvForgetPassword2 = (TextView) findViewById(R.id.tv_forget_password2);
        mBtSubmit = (Button) findViewById(R.id.bt_submit);
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPasswordOrigin);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPasswordNew);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPasswordNewConfirm);
    }

    @Override
    public void initData() {
        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String origin = mEtPasswordOrigin.getText().toString();
                String password = mEtPasswordNew.getText().toString();
                String passwordNew = mEtPasswordNewConfirm.getText().toString();

                boolean canSubmit = !TextUtils.isEmpty(origin) && origin.length() >= 6
                        && RegularUtil.isPassword(password)
                        && StringUtil.contentEquals(password, passwordNew);
                boolean visible2Password = (RegularUtil.isPassword(password) || RegularUtil.isPassword(passwordNew))
                        && !StringUtil.contentEquals(password, passwordNew);
                mTvHint.setVisibility(visible2Password ? View.VISIBLE : View.INVISIBLE);
                mBtSubmit.setEnabled(canSubmit);

            }
        };

        mEtPasswordNewConfirm.addTextChangedListener(myTextWatcher);
        mEtPasswordOrigin.addTextChangedListener(myTextWatcher);
        mEtPasswordNew.addTextChangedListener(myTextWatcher);
        mBtSubmit.setEnabled(false);
        mBtSubmit.setOnClickListener(v -> {
            LoadingDialog.show(SetPasswordOriginActivity.this, "正在修改");
            String origin = mEtPasswordOrigin.getText().toString();
            String password = mEtPasswordNew.getText().toString();
            String passwordNew = mEtPasswordNewConfirm.getText().toString();
            mPresenter.setPasswordByOrigin(origin, password);
        });

        mEtPasswordOrigin.setOnFocusChangeListener((v, hasFocus) -> {
            mEtPasswordOrigin.setBackgroundResource(hasFocus ? R.drawable.et_underline_selected : R.drawable.et_underline_un_selected);
        });
        mEtPasswordNew.setOnFocusChangeListener((v, hasFocus) -> {
            mEtPasswordNew.setBackgroundResource(hasFocus ? R.drawable.et_underline_selected : R.drawable.et_underline_un_selected);
        });
        mEtPasswordNewConfirm.setOnFocusChangeListener((v, hasFocus) -> {
            mEtPasswordNewConfirm.setBackgroundResource(hasFocus ? R.drawable.et_underline_selected : R.drawable.et_underline_un_selected);
        });

        mTvForgetPassword2.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(mContext, SetPasswordFirstActivity.class));
        });

        mIvSeePassword = findViewById(R.id.iv_see_password);
        mIvSeePassword2 = findViewById(R.id.iv_see_password2);
        mIvSeePassword.setOnClickListener(v -> {

            mIvSeePassword.setSelected(!mIvSeePassword.isSelected());
            LogUtil.i("mIvSeePassword.isSelected() = " + mIvSeePassword.isSelected());
            if (mIvSeePassword.isSelected()) {
                // 如果选中，显示密码
                mEtPasswordNew.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 否则隐藏密码
                mEtPasswordNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            mEtPasswordNew.setSelection(mEtPasswordNew.getText().length());
        });

        mIvSeePassword2.setOnClickListener(v -> {

            mIvSeePassword2.setSelected(!mIvSeePassword2.isSelected());
            LogUtil.i("mIvSeePassword.isSelected() = " + mIvSeePassword2.isSelected());
            if (mIvSeePassword2.isSelected()) {
                // 如果选中，显示密码
                mEtPasswordNewConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 否则隐藏密码
                mEtPasswordNewConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            mEtPasswordNewConfirm.setSelection(mEtPasswordNewConfirm.getText().length());
        });



    }


    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(CommonModel data) {
        LoadingDialog.hide();
        ToastUtil.show("修改成功");
        finish();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        ToastUtil.show("修改失败:" + throwable.getMessage());
    }
}
