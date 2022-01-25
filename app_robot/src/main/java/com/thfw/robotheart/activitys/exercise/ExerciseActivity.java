package com.thfw.robotheart.activitys.exercise;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ExerciseModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.UserToolPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ExerciseAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

public class ExerciseActivity extends RobotBaseActivity<UserToolPresenter> implements UserToolPresenter.UserToolUi<List<ExerciseModel>> {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private ExerciseAdapter mExerciseAdapter;
    private com.thfw.ui.widget.LoadingView mLoadingView;

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

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new GridLayoutManager(mContext, 2));

        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mExerciseAdapter = new ExerciseAdapter(null);
        mExerciseAdapter.setOnRvItemListener(new OnRvItemListener<ExerciseModel>() {
            @Override
            public void onItemClick(List<ExerciseModel> list, int position) {
                ExerciseModel model = list.get(position);
                ExerciseDetailsActivity.startActivity(mContext, model);
            }
        });
        mRvList.setAdapter(mExerciseAdapter);

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
        mLoadingView.showFail(v -> {
            mPresenter.onGetList();
        });
    }
}