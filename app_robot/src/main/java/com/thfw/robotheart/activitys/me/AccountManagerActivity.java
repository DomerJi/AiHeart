package com.thfw.robotheart.activitys.me;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.thfw.base.base.IPresenter;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.UserInfoPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.login.BindPhoneActivity;
import com.thfw.robotheart.activitys.login.SetPasswordActivity;
import com.thfw.robotheart.view.DialogRobotFactory;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.User;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.trello.rxlifecycle2.LifecycleProvider;

public class AccountManagerActivity extends RobotBaseActivity {


    private static final int BIND_CODE = 9;
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.Button mBtLogout;
    private android.widget.RelativeLayout mRlUserId;
    private android.widget.TextView mTvUserId;
    private android.widget.RelativeLayout mRlCollection;
    private android.widget.RelativeLayout mRlWork;
    private android.widget.RelativeLayout mRlPhoneChange;
    private android.widget.TextView mTvUserPhone;
    private android.widget.ImageView mIvPhoneArrow;
    private android.widget.RelativeLayout mRlPasswordChange;
    private android.widget.TextView mTvUserPassword;
    private android.widget.ImageView mIvPasswrodArrow;
    private TextView mTvUserNickname;
    private TextView mTvUserName;

    @Override
    public int getContentView() {
        return R.layout.activity_account_manager;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mBtLogout = (Button) findViewById(R.id.bt_logout);
        mRlUserId = (RelativeLayout) findViewById(R.id.rl_user_id);
        mTvUserId = (TextView) findViewById(R.id.tv_user_id);
        mRlCollection = (RelativeLayout) findViewById(R.id.rl_collection);
        mRlWork = (RelativeLayout) findViewById(R.id.rl_work);
        mRlPhoneChange = (RelativeLayout) findViewById(R.id.rl_phone_change);
        mTvUserPhone = (TextView) findViewById(R.id.tv_user_phone);
        mIvPhoneArrow = (ImageView) findViewById(R.id.iv_phone_arrow);
        mRlPasswordChange = (RelativeLayout) findViewById(R.id.rl_password_change);
        mTvUserPassword = (TextView) findViewById(R.id.tv_user_password);
        mIvPasswrodArrow = (ImageView) findViewById(R.id.iv_passwrod_arrow);
        mTvUserNickname = (TextView) findViewById(R.id.tv_user_nickname);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
    }

    @Override
    public void initData() {
        if (!UserManager.getInstance().isLogin()) {
            finish();
            return;
        }
        setUserInfo();
        mBtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogRobotFactory.createCustomDialog(AccountManagerActivity.this, new DialogRobotFactory.OnViewCallBack() {

                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        if (view.getId() == R.id.tv_right) {
                            UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
                            ToastUtil.show("成功退出");
                            finish();
                        }
                        tDialog.dismiss();
                    }

                    @Override
                    public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                        mTvHint.setText("确定退出登录吗");
                        mTvTitle.setVisibility(View.GONE);
                        mTvLeft.setText("取消");
                        mTvRight.setText("确定");
                    }
                });
            }
        });
        mRlPasswordChange.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SetPasswordActivity.class));
        });

        // 填写手机号
        mRlPhoneChange.setOnClickListener(v -> {
            startActivityForResult(new Intent(mContext, BindPhoneActivity.class), BIND_CODE);
        });
    }

    @Override
    public UserObserver addObserver() {
        return new UserObserver() {
            @Override
            public void onChanged(UserManager accountManager, User user) {
                setUserInfo();
            }
        };
    }

    private void setUserInfo() {
        if (!UserManager.getInstance().isLogin()) {
            return;
        }
        User.UserInfo userInfo = UserManager.getInstance().getUser().getUserInfo();
        if (userInfo != null) {
            mTvUserId.setText(String.valueOf(userInfo.id));
            mTvUserPhone.setText(userInfo.mobile);
            mTvUserName.setText(userInfo.trueName);
            mTvUserNickname.setText(userInfo.userName);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case 9:
                String phoneNumber = data.getStringExtra(BindPhoneActivity.KEY_RESULT);
                mTvUserPhone.setText(phoneNumber);
                onUpdateInfo("mobile", phoneNumber);
                UserManager.getInstance().getUser().getUserInfo().mobile = phoneNumber;
                UserManager.getInstance().notifyUserInfo();
                break;
        }
    }


    public void onUpdateInfo(String key, Object value) {
        onUpdateInfo(NetParams.crete()
                .add("key", key)
                .add("value", value));
    }

    public void onUpdateInfo(NetParams netParams) {

        LoadingDialog.show(this, "保存中...");
        new UserInfoPresenter(new UserInfoPresenter.UserInfoUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return AccountManagerActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                LoadingDialog.hide();
                ToastUtil.show("保存成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LoadingDialog.hide();
                ToastUtil.show("保存失败");

            }
        }).onUpdate(netParams);
    }
}