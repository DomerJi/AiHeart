package com.thfw.mobileheart.activity.exercise;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ExerciseModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.UserToolPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.adapter.ExerciseAdapter;
import com.thfw.mobileheart.lhxk.InstructScrollHelper;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

public class ExerciseActivity extends BaseActivity<UserToolPresenter> implements UserToolPresenter.UserToolUi<List<ExerciseModel>> {

    private TitleView mTitleView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private ExerciseAdapter mExerciseAdapter;
    private com.thfw.ui.widget.LoadingView mLoadingView;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ExerciseActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_exercise;
    }

    @Override
    public UserToolPresenter onCreatePresenter() {
        return new UserToolPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);
        mRvList = (RecyclerView) findViewById(R.id.rv_exercise);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));

        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mExerciseAdapter = new ExerciseAdapter(null);
        mExerciseAdapter.setOnRvItemListener(new OnRvItemListener<ExerciseModel>() {
            @Override
            public void onItemClick(List<ExerciseModel> list, int position) {
                ExerciseModel model = list.get(position);
                ExerciseDetailActivity.startActivity(mContext, model);
            }
        });
        mRvList.setAdapter(mExerciseAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onGetList();
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<ExerciseModel> data) {
        mExerciseAdapter.setDataListNotify(data);
        mLoadingView.hide();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mExerciseAdapter == null || mExerciseAdapter.getItemCount() == 0) {
            mLoadingView.showFail(v -> {
                mPresenter.onGetList();
            });
        }
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        new InstructScrollHelper(ExerciseActivity.class, mRvList);
    }
}