package com.thfw.mobileheart.activity.exercise;

import android.content.Context;
import android.content.Intent;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.activity.test.TestingActivity;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;

public class ExerciseActivity extends BaseActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ExerciseActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_exercise;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}