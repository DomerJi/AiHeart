package com.thfw.mobileheart.activity.settings;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.StringUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.utils.EditTextUtil;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

public class SetPasswordFirstActivity extends BaseActivity<LoginPresenter> implements LoginPresenter.LoginUi<CommonModel> {

    private EditText mEtPasswordNew;
    private EditText mEtPasswordNewConfirm;
    private Button mBtSubmit;
    private TitleView mTitleView;
    private EditText mEtPassword;
    private EditText mEtNewPassword;
    private EditText mEtNewPassword01;
    private TextView mTvHint;

    @Override
    public int getContentView() {
        return R.layout.activity_set_password_first;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {

        mEtPasswordNew = (EditText) findViewById(R.id.et_new_password);
        mEtPasswordNewConfirm = (EditText) findViewById(R.id.et_new_password01);
        mBtSubmit = (Button) findViewById(R.id.bt_submit);
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPasswordNew);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPasswordNewConfirm);
    }

    @Override
    public void initData() {
        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = mEtPasswordNew.getText().toString();
                String passwordNew = mEtPasswordNewConfirm.getText().toString();

                boolean canSubmit = RegularUtil.isPassword(password)
                        && StringUtil.contentEquals(password, passwordNew);
                boolean visible2Password = (RegularUtil.isPassword(password) || RegularUtil.isPassword(passwordNew))
                        && !StringUtil.contentEquals(password, passwordNew);
                mTvHint.setVisibility(visible2Password ? View.VISIBLE : View.INVISIBLE);
                mBtSubmit.setEnabled(canSubmit);

            }
        };

        mEtPasswordNewConfirm.addTextChangedListener(myTextWatcher);
        mEtPasswordNew.addTextChangedListener(myTextWatcher);
        mBtSubmit.setEnabled(false);
        mBtSubmit.setOnClickListener(v -> {
            LoadingDialog.show(SetPasswordFirstActivity.this, "????????????");
            String password = mEtPasswordNew.getText().toString();
            String passwordNew = mEtPasswordNewConfirm.getText().toString();
            mPresenter.setPasswordByFirst(password);
        });

        mEtPasswordNew.setOnFocusChangeListener((v, hasFocus) -> {
            mEtPasswordNew.setBackgroundResource(hasFocus ? R.drawable.et_underline_selected : R.drawable.et_underline_un_selected);
        });
        mEtPasswordNewConfirm.setOnFocusChangeListener((v, hasFocus) -> {
            mEtPasswordNewConfirm.setBackgroundResource(hasFocus ? R.drawable.et_underline_selected : R.drawable.et_underline_un_selected);
        });

    }


    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(CommonModel data) {
        LoadingDialog.hide();
        ToastUtil.show("????????????");
        finish();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        ToastUtil.show("????????????:" + throwable.getMessage());
    }
}
