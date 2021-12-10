package com.thfw.robotheart.fragments.help;

import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.ui.base.RobotBaseFragment;

public class AboutMeFragment extends RobotBaseFragment {

    private TextView mTvHint;
    private TextView mTvVersion;
    private LinearLayout mLlCheckVersion;
    private TextView mTvCheckVersion;

    @Override
    public int getContentView() {
        return R.layout.fragment_about_me;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mLlCheckVersion = (LinearLayout) findViewById(R.id.ll_check_version);
        mTvCheckVersion = (TextView) findViewById(R.id.tv_check_version);
    }

    @Override
    public void initData() {
        String hint = "<font color='#91dff3'>产品概述：</font>"
                + getResources().getString(R.string.bigText2);
        mTvHint.setText(Html.fromHtml(hint));
    }
}