package com.thfw.mobileheart.activity.exercise;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.mobileheart.adapter.ExerciseAdapter;
import com.thfw.mobileheart.model.ExerciseModel;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

import java.util.List;

public class ExerciseActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvExercise;

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

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvExercise = (RecyclerView) findViewById(R.id.rv_exercise);
    }

    @Override
    public void initData() {

        mRvExercise.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(null);
        exerciseAdapter.setOnRvItemListener(new OnRvItemListener<ExerciseModel>() {
            @Override
            public void onItemClick(List<ExerciseModel> list, int position) {
                ExerciseDetailActivity.startActivity(mContext);
            }
        });
        mRvExercise.setAdapter(exerciseAdapter);
    }
}