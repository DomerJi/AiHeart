package com.thfw.robotheart.activitys.set;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.util.Dormant;

public class DormantActivity extends RobotBaseActivity implements Dormant.MinuteChangeListener {

    private android.widget.ImageView mIvAnim;
    private android.widget.TextView mTvTime;
    private String mOriginalText;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, DormantActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_dormant;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mIvAnim = (ImageView) findViewById(R.id.iv_anim);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mOriginalText = mTvTime.getText().toString();
    }

    @Override
    public void initData() {
        Dormant.setMinuteChangeListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        finish();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onDestroy() {
        Dormant.setMinuteChangeListener(null);
        super.onDestroy();
    }

    @Override
    public void onChange() {
        mTvTime.setText(mOriginalText + "（已休眠" + Dormant.getSleepTime() + "分钟）");
    }
}