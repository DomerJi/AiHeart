package com.thfw.robotheart.activitys.me;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.view.TitleRobotView;

public class SimpleTextActivity extends RobotBaseActivity {


    private static final String KEY_TITLE = "key.title";
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.TextView mTvDetail;

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

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);

        mTitleRobotView.setCenterText(getIntent().getStringExtra(KEY_TITLE));
        mTvDetail.setText(getIntent().getStringExtra(KEY_DATA));
    }

    @Override
    public void initData() {

    }
}