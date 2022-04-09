package com.thfw.mobileheart.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.ui.widget.TitleView;

public class SimpleTextActivity extends BaseActivity {


    private static final String KEY_TITLE = "key.title";
    private TitleView mTitleRobotView;
    private TextView mTvDetail;

    public static void startActivity(Context context, String title, String detail) {
        context.startActivity(new Intent(context, SimpleTextActivity.class)
                .putExtra(KEY_TITLE, title)
                .putExtra(KEY_DATA, detail));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_smiple_text;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleView) findViewById(R.id.titleView);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);

        mTitleRobotView.setCenterText(getIntent().getStringExtra(KEY_TITLE));
        mTvDetail.setText(getIntent().getStringExtra(KEY_DATA));
    }

    @Override
    public void initData() {

    }
}