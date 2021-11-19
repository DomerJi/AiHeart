package com.thfw.robotheart.activitys;

import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.HourUtil;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {


        TextView mTvWeak = findViewById(R.id.tv_weak);
        String ymd = HourUtil.getYYMMDD(System.currentTimeMillis()).replaceAll("-", "/");

        mTvWeak.setText(ymd + "  " + HourUtil.getWeek(System.currentTimeMillis()));
    }

    @Override
    public void initData() {

    }
}