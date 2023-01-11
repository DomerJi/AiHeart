package com.thfw.mobileheart.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentActivity;

import com.thfw.base.ContextApp;
import com.thfw.base.base.IPresenter;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.MyPreferences;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.MainActivity;
import com.thfw.mobileheart.activity.organ.AskForSelectActivity;
import com.thfw.mobileheart.activity.settings.InfoActivity;
import com.thfw.mobileheart.activity.settings.PrivacyPolicyActivity;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.mobileheart.fragment.MeFragment;
import com.thfw.mobileheart.fragment.login.LoginByFaceFragment;
import com.thfw.mobileheart.fragment.login.MobileFragment;
import com.thfw.mobileheart.fragment.login.OtherLoginFragment;
import com.thfw.mobileheart.fragment.login.PasswordFragment;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.mobileheart.util.FragmentLoader;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.thfw.user.models.User;

import org.opencv.android.Static2Helper;

public class LoginActivity extends BaseActivity {
    public static final boolean AGREE_CLICK_DIALOG = false;
    public static final int BY_MOBILE = 0;
    public static final int BY_PASSWORD = 1;
    public static final int BY_FORGET = 2;
    public static final int BY_FACE = 3;
    public static final int BY_OTHER = 4;
    // 登录后播放唤醒动画
    public static final String KEY_LOGIN_BEGIN = "login.begin";
    public static final String KEY_LOGIN_BEGIN_TTS = "login.begin.tts";
    public static String INPUT_PHONE = "";
    private int type;
    private FragmentLoader fragmentLoader;
    public static boolean mTvRightAgreed;

    public static void startActivity(Context context, int type) {
        context.startActivity(new Intent(context, LoginActivity.class).putExtra(KEY_DATA, type));
        MyPreferences.getInstance(MyApplication.getApp()).setAgreePrivacyAgreement(false);
    }

    public static boolean login(Activity activity, TokenModel data, String mobile) {
        if (data != null && !TextUtils.isEmpty(data.token)) {
            User user = new User();
            user.setToken(data.token);
            if (RegularUtil.isPhone(mobile)) {
                user.setMobile(mobile);
            }
            user.setSetUserInfo(data.isSetUserInfo());
            user.setOrganization(data.organization);
            user.setAuthTypeList(data.getAuthType());
            if (!data.isNoOrganization()) {
                if (EmptyUtil.isEmpty(data.getAuthType()) || !data.getAuthType().contains(ContextApp.getDeviceTypeStr())) {
                    DialogFactory.createSimple((FragmentActivity) activity,
                            MyApplication.getApp().getResources().getString(R.string.this_device_no_auth_login));
                    return false;
                }
            }
            UserManager.addHistoryAccount(mobile,"");
            LogUtil.d("UserManager.getInstance().isLogin() = " + UserManager.getInstance().isLogin());
            if (data.isNoOrganization()) {
                // todo 手机加入组织机构比较复杂
                user.setLoginStatus(LoginStatus.LOGOUT_HIDE);
                UserManager.getInstance().login(user);
                AskForSelectActivity.startForResult(activity, true);
            } else if (data.isNoSetUserInfo()) {
                user.setLoginStatus(LoginStatus.LOGOUT_HIDE);
                UserManager.getInstance().login(user);
                InfoActivity.startActivityFirst(activity);
            } else {
                user.setLoginStatus(LoginStatus.LOGINED);
                UserManager.getInstance().login(user);
                MyPreferences.getInstance(MyApplication.getApp()).setAgreePrivacyAgreement(true);

            }
            activity.finish();
            return true;
        } else {
            ToastUtil.show("token 参数错误");
            return false;
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        MainActivity.resetInit();
        MeFragment.resetInitFaceState();
        CommonParameter.setOrganizationId("");
        type = getIntent().getIntExtra(KEY_DATA, BY_MOBILE);
        fragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);

        fragmentLoader.add(BY_MOBILE, new MobileFragment());
        fragmentLoader.add(BY_PASSWORD, new PasswordFragment());
        fragmentLoader.add(BY_FACE, new LoginByFaceFragment());
        fragmentLoader.add(BY_OTHER, new OtherLoginFragment());

        fragmentLoader.load(type);
    }

    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }

    public FragmentLoader getFragmentLoader() {
        return fragmentLoader;
    }

    @Override
    public void initData() {

        if (!UserManager.getInstance().isTrueLogin()) {
            MainActivity.setShowLoginAnim(true);
            SharePreferenceUtil.setBoolean(KEY_LOGIN_BEGIN, true);
            SharePreferenceUtil.setBoolean(KEY_LOGIN_BEGIN_TTS, true);
        } else {
            MyPreferences.getInstance(getApplicationContext()).setAgreePrivacyAgreement(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Static2Helper.initOpenCV(true);
    }

    @Override
    public void onDestroy() {
        INPUT_PHONE = "";
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        if (!UserManager.getInstance().isLogin()) {
            MyApplication.kill();
        }
    }

    public static void agreeDialog(FragmentActivity activity, OnViewClickListener onViewClickListener) {
        if (mTvRightAgreed) {
            return;
        }
        DialogFactory.createCustomDialog(activity, new DialogFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                String html = "请你务必审慎阅读、充分理解 " +
                        " <font color='" + UIConfig.COLOR_AGREE + "'>《用户服务协议》</font>" +
                        "和<font color='" + UIConfig.COLOR_AGREE + "'>《隐私保护政策》</font>各条款。<p> </p>" +
                        "如您同意所列条款，请点击\"同意\"按钮，开始使用我们的产品和服务。";

                mTvHint.setText(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY));
                mTvHint.setOnClickListener(v -> {
                    activity.startActivity(new Intent(activity, PrivacyPolicyActivity.class));
                });
                mTvTitle.setText("欢迎使用AI咨询师");
                mTvLeft.setText("拒绝");
                mTvRight.setText("同意");

            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (onViewClickListener != null) {
                    if (view.getId() == com.thfw.ui.R.id.tv_right) {
                        mTvRightAgreed = true;
                        MyPreferences.getInstance(MyApplication.getApp()).setAgreePrivacyAgreement(true);
                    }
                    onViewClickListener.onViewClick(viewHolder, view, tDialog);
                }

                tDialog.dismiss();
            }
        }, false, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!mTvRightAgreed) {
                    MyApplication.kill();
                }
            }
        });
    }

    public static void agreedClickDialog(CheckBox mCbProduct) {
        if (LoginActivity.AGREE_CLICK_DIALOG) {
            mCbProduct.setOnClickListener(v -> {
                if (mCbProduct.isChecked()) {
                    mCbProduct.setChecked(false);
                    LoginActivity.agreeDialog((FragmentActivity) mCbProduct.getContext(), new OnViewClickListener() {
                        @Override
                        public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                            if (view.getId() == com.thfw.ui.R.id.tv_right) {
                                mCbProduct.setChecked(true);
                            }
                        }
                    });
                }
            });
        }
    }

}