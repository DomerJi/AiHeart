package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.widget.Button;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

public class HelpHintsActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.Button mBtHelpBack;

    @Override
    public int getContentView() {
        return R.layout.activity_help_hints;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mBtHelpBack = (Button) findViewById(R.id.bt_help_back);
    }

    @Override
    public void initData() {
        mBtHelpBack.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MeWillHelpBackActivity.class));
        });
    }
}