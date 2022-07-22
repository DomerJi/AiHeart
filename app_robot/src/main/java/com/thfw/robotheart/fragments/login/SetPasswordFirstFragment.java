package com.thfw.robotheart.fragments.login;

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
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.utils.EditTextUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * Author:pengs
 * Date: 2021/12/7 9:19
 * Describe:Todo
 */
public class SetPasswordFirstFragment extends RobotBaseFragment<LoginPresenter> implements LoginPresenter.LoginUi<CommonModel> {

    private TextView mTv02;
    private EditText mEtPasswordNew;
    private TextView mTv03;
    private EditText mEtPasswordNewConfirm;
    private Button mBtSubmit;

    @Override
    public int getContentView() {
        return R.layout.fragment_set_password_first;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {


        mTv02 = (TextView) findViewById(R.id.tv_02);
        mEtPasswordNew = (EditText) findViewById(R.id.et_password_new);
        mTv03 = (TextView) findViewById(R.id.tv_03);
        mEtPasswordNewConfirm = (EditText) findViewById(R.id.et_password_new_confirm);
        mBtSubmit = (Button) findViewById(R.id.bt_submit);

        EditTextUtil.setEditTextInhibitInputSpace(mEtPasswordNew);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPasswordNewConfirm);
    }

    @Override
    public void initData() {
        mBtSubmit.setEnabled(true);


        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = mEtPasswordNew.getText().toString();
                String passwordNew = mEtPasswordNewConfirm.getText().toString();

                boolean canSubmit = RegularUtil.isPassword(password)
                        && StringUtil.contentEquals(password, passwordNew);

                mBtSubmit.setEnabled(canSubmit);

            }
        };

        mEtPasswordNewConfirm.addTextChangedListener(myTextWatcher);
        mEtPasswordNew.addTextChangedListener(myTextWatcher);
        mBtSubmit.setEnabled(false);
        mBtSubmit.setOnClickListener(v -> {
            LoadingDialog.show(getActivity(), "正在修改");
            String password = mEtPasswordNew.getText().toString();
            mPresenter.setPasswordByFirst(password);
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
        getActivity().finish();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        ToastUtil.show("修改失败:" + throwable.getMessage());
    }
}
