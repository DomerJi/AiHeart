package com.thfw.robotheart.activitys.set;

import android.widget.LinearLayout;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.activitys.WebActivity;
import com.thfw.robotheart.constants.AgreeOn;
import com.thfw.robotheart.view.TitleRobotView;

public class PrivacyPolicyActivity extends RobotBaseActivity {

    private TitleRobotView mTitleView;
    private LinearLayout mLlUser;
    private LinearLayout mLlSelfMsg;

    @Override
    public int getContentView() {
        return R.layout.activity_privacy_policy;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleRobotView) findViewById(R.id.titleView);
        mLlUser = (LinearLayout) findViewById(R.id.ll_user);
        mLlSelfMsg = (LinearLayout) findViewById(R.id.ll_self_msg);
    }

    @Override
    public void initData() {

        mLlSelfMsg.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_MSG);
        });

        mLlUser.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_USER);
        });


    }
}