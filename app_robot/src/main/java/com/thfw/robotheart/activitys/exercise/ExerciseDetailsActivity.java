package com.thfw.robotheart.activitys.exercise;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioItemModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ExerciseLogcateAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;

import java.util.List;

public class ExerciseDetailsActivity extends RobotBaseActivity {

    private android.widget.ImageView mIvBg;
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.TextView mTvVideoTitle;
    private android.widget.TextView mTvVideoContent;
    private android.widget.LinearLayout mLlLike;
    private android.widget.TextView mTvLikeTitle;
    private androidx.recyclerview.widget.RecyclerView mRvLike;
    private android.widget.TextView mTvLikeChanged;
    private androidx.constraintlayout.widget.ConstraintLayout mClBg;
    private ExerciseLogcateAdapter mLogcateAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_exercise_details;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mIvBg = (ImageView) findViewById(R.id.iv_bg);
        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mTvVideoTitle = (TextView) findViewById(R.id.tv_video_title);
        mTvVideoContent = (TextView) findViewById(R.id.tv_video_content);
        mLlLike = (LinearLayout) findViewById(R.id.ll_like);
        mTvLikeTitle = (TextView) findViewById(R.id.tv_like_title);
        mRvLike = (RecyclerView) findViewById(R.id.rv_like);
        mTvLikeChanged = (TextView) findViewById(R.id.tv_like_changed);
        mClBg = (ConstraintLayout) findViewById(R.id.cl_bg);
    }

    @Override
    public void initData() {
        mRvLike.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mLogcateAdapter = new ExerciseLogcateAdapter(null);
        mLogcateAdapter.setOnRvItemListener(new OnRvItemListener<AudioItemModel>() {
            @Override
            public void onItemClick(List<AudioItemModel> list, int position) {
                startActivity(new Intent(mContext, ExerciseIngActivity.class));
            }
        });
        mRvLike.setAdapter(mLogcateAdapter);
    }
}