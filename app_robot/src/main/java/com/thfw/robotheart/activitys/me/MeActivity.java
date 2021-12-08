package com.thfw.robotheart.activitys.me;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.user.login.UserManager;

public class MeActivity extends RobotBaseActivity {

    private TitleRobotView mTitleRobotView;
    private com.makeramen.roundedimageview.RoundedImageView mRivAvatar;
    private android.widget.TextView mTvNickname;
    private android.widget.LinearLayout mLlHistory;
    private android.widget.LinearLayout mLlTest;
    private android.widget.LinearLayout mLlExercise;
    private android.widget.LinearLayout mLlMusic;
    private android.widget.LinearLayout mLlRead;
    private android.widget.LinearLayout mLlStudy;
    private android.widget.RelativeLayout mRlMsg;
    private android.widget.RelativeLayout mRlCollection;
    private android.widget.RelativeLayout mRlWork;
    private android.widget.RelativeLayout mRlBackHelp;
    private android.widget.RelativeLayout mRlAccountManager;

    @Override
    public int getContentView() {
        return R.layout.activity_me;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mTvNickname = (TextView) findViewById(R.id.tv_nickname);
        mLlHistory = (LinearLayout) findViewById(R.id.ll_history);
        mLlTest = (LinearLayout) findViewById(R.id.ll_test);
        mLlExercise = (LinearLayout) findViewById(R.id.ll_exercise);
        mLlMusic = (LinearLayout) findViewById(R.id.ll_music);
        mLlRead = (LinearLayout) findViewById(R.id.ll_read);
        mLlStudy = (LinearLayout) findViewById(R.id.ll_study);
        mRlMsg = (RelativeLayout) findViewById(R.id.rl_msg);
        mRlCollection = (RelativeLayout) findViewById(R.id.rl_collection);
        mRlWork = (RelativeLayout) findViewById(R.id.rl_work);
        mRlBackHelp = (RelativeLayout) findViewById(R.id.rl_back_help);
        mRlAccountManager = (RelativeLayout) findViewById(R.id.rl_account_manager);
    }

    @Override
    public void initData() {
        mRivAvatar.setOnClickListener(v -> {
            if (!UserManager.getInstance().isLogin()) {
                LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
            }
        });

        mRlAccountManager.setOnClickListener(v -> {
            if (!UserManager.getInstance().isLogin()) {
                LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
            } else {
                startActivity(new Intent(mContext, AccountManagerActivity.class));
            }
        });

        mRlMsg.setOnClickListener(v -> {
            if (!UserManager.getInstance().isLogin()) {
                LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
            } else {
                startActivity(new Intent(mContext, InfoActivity.class));
            }
        });
    }
}