package com.thfw.mobileheart.activity.settings;

import android.widget.LinearLayout;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.constants.AgreeOn;
import com.thfw.ui.widget.TitleView;

public class PrivacyPolicyActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.LinearLayout mLlUser;
    private android.widget.LinearLayout mLlSelfMsg;

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

        mTitleView = (TitleView) findViewById(R.id.titleView);
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