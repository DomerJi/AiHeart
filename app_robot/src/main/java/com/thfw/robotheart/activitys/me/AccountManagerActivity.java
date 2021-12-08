package com.thfw.robotheart.activitys.me;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.login.SetPasswordActivity;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;

public class AccountManagerActivity extends RobotBaseActivity {


    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.Button mBtLogout;
    private android.widget.RelativeLayout mRlUserId;
    private android.widget.TextView mTvUserId;
    private android.widget.RelativeLayout mRlCollection;
    private android.widget.EditText mEtUserNickname;
    private android.widget.RelativeLayout mRlWork;
    private android.widget.EditText mEtUserName;
    private android.widget.RelativeLayout mRlPhoneChange;
    private android.widget.TextView mTvUserPhone;
    private android.widget.ImageView mIvPhoneArrow;
    private android.widget.RelativeLayout mRlPasswordChange;
    private android.widget.TextView mTvUserPassword;
    private android.widget.ImageView mIvPasswrodArrow;

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
        mEtUserNickname = (EditText) findViewById(R.id.et_user_nickname);
        mRlWork = (RelativeLayout) findViewById(R.id.rl_work);
        mEtUserName = (EditText) findViewById(R.id.et_user_name);
        mRlPhoneChange = (RelativeLayout) findViewById(R.id.rl_phone_change);
        mTvUserPhone = (TextView) findViewById(R.id.tv_user_phone);
        mIvPhoneArrow = (ImageView) findViewById(R.id.iv_phone_arrow);
        mRlPasswordChange = (RelativeLayout) findViewById(R.id.rl_password_change);
        mTvUserPassword = (TextView) findViewById(R.id.tv_user_password);
        mIvPasswrodArrow = (ImageView) findViewById(R.id.iv_passwrod_arrow);
    }

    @Override
    public void initData() {
        mBtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
                ToastUtil.show("成功退出");
                finish();
            }
        });
        mRlPasswordChange.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SetPasswordActivity.class));
        });
    }
}