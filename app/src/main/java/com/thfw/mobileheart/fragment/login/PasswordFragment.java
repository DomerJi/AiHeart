package com.thfw.mobileheart.fragment.login;

import static android.content.Context.VIBRATOR_SERVICE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.ClickCountUtils;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.PrivateSetActivity;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.activity.login.ForgetPasswordActivity;
import com.thfw.mobileheart.activity.login.LoginActivity;
import com.thfw.mobileheart.constants.AgreeOn;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.utils.EditTextUtil;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.user.login.UserManager;
import com.thfw.user.models.HistoryAccount;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/8/4 16:19
 * Describe:密码登录
 */
public class PasswordFragment extends BaseFragment<LoginPresenter> implements LoginPresenter.LoginUi<TokenModel> {
    private ConstraintLayout mClTop;
    private RoundedImageView mRivIcon;
    private TextView mTvCountry;
    private EditText mEtMobile;
    private EditText mEtPassword;
    private Button mBtLogin;
    private TextView mTvLoginByMobile;
    private TextView mTvForgetPassword;
    private LinearLayout mClBottom;
    private RoundedImageView mRivWechat;
    private RoundedImageView mRivQq;
    // 协议
    private TextView mTvProduct3g;
    private TextView mTvProductUser;
    private TextView mTvProductMsg;
    private TextView mTvProductAgree;
    private String phone;
    private ImageView mIvSeePassword;
    private TextView mTvNoAccount;
    private CheckBox mCbProduct;
    private RelativeLayout mRlAccountCache;
    private RoundedImageView mRivCache01;
    private TextView mTvMobileCache01;
    private ImageView mIvArrowCache;
    private RelativeLayout mRlAccountCache02;
    private RoundedImageView mRivCache02;
    private TextView mTvMobileCache02;
    private RelativeLayout mRlAccountCache03;
    private RelativeLayout mRlAccount;
    private String mUseCacheAccount;
    private ImageView mIvClearCache01;
    private ImageView mIvClearCache02;

    @Override
    public int getContentView() {
        return R.layout.fragment_login_by_password;
    }

    @Override
    public LoginPresenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void initView() {

        mClTop = (ConstraintLayout) findViewById(R.id.cl_top);
        mRivIcon = (RoundedImageView) findViewById(R.id.riv_icon);
        mTvCountry = (TextView) findViewById(R.id.tv_country);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mTvLoginByMobile = (TextView) findViewById(R.id.tv_login_by_mobile);
        mTvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        findViewById(R.id.tv_password_login_title).setOnClickListener(v -> {
            if (ClickCountUtils.click(10)) {
                startActivity(new Intent(mContext, PrivateSetActivity.class));
            }
        });
        MyTextWatcher myTextWatcher = new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = getInputPhone();
                String password = mEtPassword.getText().toString();
                mBtLogin.setEnabled((phone != null && phone.length() > 3) && (password != null && password.length() > 5));
            }
        };
        mEtPassword.addTextChangedListener(myTextWatcher);
        mEtMobile.addTextChangedListener(myTextWatcher);

        mBtLogin.setEnabled(false);
        mTvLoginByMobile.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                LoginActivity activity = (LoginActivity) getActivity();
                activity.getFragmentLoader().load(LoginActivity.BY_OTHER);
                hideInput();
            }
        });
        mTvNoAccount = findViewById(R.id.tv_no_account);
        mTvNoAccount.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                LoginActivity activity = (LoginActivity) getActivity();
                Bundle bundle = new Bundle();
                bundle.putBoolean("register", true);
                activity.getFragmentLoader().load(LoginActivity.BY_MOBILE).setArguments(bundle);
                hideInput();
            }
        });
        mIvSeePassword = findViewById(R.id.iv_see_password);
        mIvSeePassword.setOnClickListener(v -> {

            mIvSeePassword.setSelected(!mIvSeePassword.isSelected());
            LogUtil.i("mIvSeePassword.isSelected() = " + mIvSeePassword.isSelected());
            if (mIvSeePassword.isSelected()) {
                // 如果选中，显示密码
                mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 否则隐藏密码
                mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            mEtPassword.setSelection(mEtPassword.getText().length());
        });
        if (!MyApplication.getApp().isLan()) {
            mTvForgetPassword.setOnClickListener(v -> {
                ForgetPasswordActivity.startActivity(mContext, ForgetPasswordActivity.BY_MOBILE);
            });
        }
        mTvProduct3g = (TextView) findViewById(R.id.tv_product_3g);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
        LoginActivity.agreedClickDialog(mCbProduct);
        mBtLogin.setOnClickListener(v -> {
            if (!mCbProduct.isChecked()) {
                LoginActivity.agreeDialog(getActivity(), new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        if (view.getId() == com.thfw.ui.R.id.tv_right) {
                            mCbProduct.setChecked(true);
                            mBtLogin.performClick();
                        }
                    }
                });
                return;
            }

            phone = getInputPhone();
            String password = mEtPassword.getText().toString();
            LoadingDialog.show(getActivity(), "登录中");
            mPresenter.loginByPassword(phone, password);
        });
        EditTextUtil.setEditTextInhibitInputSpeChatAndSpace(mEtMobile);
        EditTextUtil.setEditTextInhibitInputSpace(mEtPassword);

        // 两个历史账户和其他账户
        mRlAccountCache = (RelativeLayout) findViewById(R.id.rl_account_cache);
        mRlAccount = (RelativeLayout) findViewById(R.id.rl_account);
        mRivCache01 = (RoundedImageView) findViewById(R.id.riv_cache01);
        mTvMobileCache01 = (TextView) findViewById(R.id.tv_mobile_cache01);
        mIvArrowCache = (ImageView) findViewById(R.id.iv_arrow_cache);
        mRlAccountCache02 = (RelativeLayout) findViewById(R.id.rl_account_cache02);
        mRivCache02 = (RoundedImageView) findViewById(R.id.riv_cache02);
        mTvMobileCache02 = (TextView) findViewById(R.id.tv_mobile_cache02);
        mRlAccountCache03 = (RelativeLayout) findViewById(R.id.rl_account_cache03);
        mIvClearCache02 = (ImageView) findViewById(R.id.iv_clear_cache02);
        mIvClearCache01 = (ImageView) findViewById(R.id.iv_clear_cache01);
        getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideInput();
                if (mRlAccountCache03.getVisibility() == View.VISIBLE) {
                    mRlAccountCache02.setVisibility(View.GONE);
                    mRlAccountCache03.setVisibility(View.GONE);
                    mIvArrowCache.animate().rotation(360).setDuration(300);
                    goVibrator();
                    return true;

                }
                return false;
            }
        });
        updateHistory();
    }

    private String getInputPhone() {
        if (mRlAccount.getVisibility() == View.VISIBLE) {
            return mEtMobile.getText().toString();
        } else {
            return mUseCacheAccount;
        }
    }

    private void updateHistory() {
        HashMap<String, HistoryAccount> accountHashMap = UserManager.getHistoryAccount();
        List<HistoryAccount> listAccount = new ArrayList<>();

        int size = accountHashMap == null ? 0 : accountHashMap.size();
        if (accountHashMap != null) {
            for (HistoryAccount historyAccount : accountHashMap.values()) {
                listAccount.add(historyAccount);
            }
            Collections.sort(listAccount);
        }
        switch (size) {
            case 0:
                mRlAccount.setVisibility(View.VISIBLE);
                mRlAccountCache.setVisibility(View.GONE);
                mRlAccountCache02.setVisibility(View.GONE);
                mRlAccountCache03.setVisibility(View.GONE);
                break;
            case 1:
                mTvMobileCache01.setText(listAccount.get(0).simpleAccount);
                mUseCacheAccount = listAccount.get(0).account;
                GlideUtil.loadBlur(mContext, listAccount.get(0).avatar, mRivCache01);
                mRlAccount.setVisibility(View.GONE);
                mRlAccountCache.setVisibility(View.VISIBLE);
                mRlAccountCache02.setVisibility(View.GONE);
                mRlAccountCache03.setVisibility(View.GONE);
//                LoginActivity.mTvRightAgreed = false;
//                mCbProduct.setChecked(false);
                break;
            case 2:
                mTvMobileCache01.setText(listAccount.get(0).simpleAccount);
                mUseCacheAccount = listAccount.get(0).account;
                GlideUtil.loadBlur(mContext, listAccount.get(0).avatar, mRivCache01);

                mTvMobileCache02.setText(listAccount.get(1).simpleAccount);
                GlideUtil.loadBlur(mContext, listAccount.get(1).avatar, mRivCache02);
                mRlAccount.setVisibility(View.GONE);
                mRlAccountCache.setVisibility(View.VISIBLE);
                mRlAccountCache02.setVisibility(View.GONE);
                mRlAccountCache03.setVisibility(View.GONE);
//                LoginActivity.mTvRightAgreed = false;
//                mCbProduct.setChecked(false);
                break;
        }
        mIvClearCache01.setOnClickListener(v3 -> {
            UserManager.removeHistoryAccount(mUseCacheAccount);
            updateHistory();
            mRlAccount.setVisibility(View.VISIBLE);
            mRlAccountCache.setVisibility(View.GONE);
            mRlAccountCache02.setVisibility(View.GONE);
            mRlAccountCache03.setVisibility(View.GONE);
            goVibrator();
        });
        mIvClearCache02.setOnClickListener(v3 -> {
            if (listAccount.get(0).account.equals(mUseCacheAccount)) {
                UserManager.removeHistoryAccount(listAccount.get(1).account);
            } else {
                UserManager.removeHistoryAccount(listAccount.get(0).account);
            }
            updateHistory();
        });
        mRlAccountCache.setOnClickListener(v -> {
            goVibrator();
            if (mRlAccountCache03.getVisibility() == View.VISIBLE) {
                mRlAccountCache02.setVisibility(View.GONE);
                mRlAccountCache03.setVisibility(View.GONE);
                mIvArrowCache.animate().rotation(360).setDuration(300);
            } else {
                mIvArrowCache.animate().rotation(180).setDuration(300);
                if (size == 2) {
                    mRlAccountCache02.setVisibility(View.VISIBLE);
                    mRlAccountCache02.setOnClickListener(v2 -> {

                        if (listAccount.get(0).account.equals(mUseCacheAccount)) {
                            mUseCacheAccount = listAccount.get(1).account;
                            mTvMobileCache01.setText(listAccount.get(1).simpleAccount);
                            GlideUtil.loadBlur(mContext, listAccount.get(1).avatar, mRivCache01);
                            mTvMobileCache02.setText(listAccount.get(0).simpleAccount);
                            GlideUtil.loadBlur(mContext, listAccount.get(0).avatar, mRivCache02);

                        } else {
                            mUseCacheAccount = listAccount.get(0).account;
                            mTvMobileCache01.setText(listAccount.get(0).simpleAccount);
                            GlideUtil.loadBlur(mContext, listAccount.get(0).avatar, mRivCache01);
                            mTvMobileCache02.setText(listAccount.get(1).simpleAccount);
                            GlideUtil.loadBlur(mContext, listAccount.get(1).avatar, mRivCache02);
                        }
                        mRlAccountCache02.setVisibility(View.GONE);
                        mRlAccountCache03.setVisibility(View.GONE);
                        mIvArrowCache.animate().rotation(360).setDuration(300);
                        goVibrator();
                    });
                }
                mRlAccountCache03.setVisibility(View.VISIBLE);
                mRlAccountCache03.setOnClickListener(v1 -> {
                    mRlAccount.setVisibility(View.VISIBLE);
                    mRlAccountCache.setVisibility(View.GONE);
                    mRlAccountCache02.setVisibility(View.GONE);
                    mRlAccountCache03.setVisibility(View.GONE);
                    goVibrator();
                });
            }


        });
    }

    private void goVibrator() {
        Vibrator vibrator = (Vibrator) MyApplication.getApp().getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(100);
        }
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (isVisible) {
            mEtMobile.setText(LoginActivity.INPUT_PHONE);
            if (!mCbProduct.isChecked()) {
                LoginActivity.agreeDialog(getActivity(), new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        if (view.getId() == com.thfw.ui.R.id.tv_right) {
                            mCbProduct.setChecked(true);
                        }
                    }
                });
            }
        } else {
            if (mRlAccount.getVisibility() == View.VISIBLE) {
                LoginActivity.INPUT_PHONE = mEtMobile.getText().toString();
            } else {
                LoginActivity.INPUT_PHONE = "";
            }
        }
    }


    @Override
    public void initData() {
        mTvProduct3g.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_3G);
        });
        mTvProductUser.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_USER);
        });
        mTvProductMsg.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_MSG);
        });
        mTvProductAgree.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_AGREE);
        });
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        LhXkHelper.putAction(PasswordFragment.class, new SpeechToAction("忘记密码", () -> {
            mTvForgetPassword.performClick();
        }));

        LhXkHelper.putAction(PasswordFragment.class, new SpeechToAction(mTvLoginByMobile.getText().toString(), () -> {
            mTvLoginByMobile.performClick();
        }));

        LhXkHelper.putAction(PasswordFragment.class, new SpeechToAction("没有账号立即注册,立即注册", () -> {
            mTvNoAccount.performClick();
        }));

        LhXkHelper.putAction(PasswordFragment.class, new SpeechToAction(mBtLogin.getText().toString(), () -> {
            mBtLogin.performClick();
        }));
    }

    @Override
    public void onSuccess(TokenModel data) {
        LoadingDialog.hide();
        LoginActivity.login(getActivity(), data, phone);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        if (HttpResult.isServerTimeNoValid(throwable.code)) {
            DialogFactory.createSimple(getActivity(), throwable.getMessage());
        } else {
            ToastUtil.show(throwable.getMessage());
        }
    }


}
