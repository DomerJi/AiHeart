package com.thfw.mobileheart.fragment.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TaskItemModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TaskPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.task.TaskDetailsActivity;
import com.thfw.mobileheart.adapter.TaskAdapter;
import com.thfw.mobileheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class TaskFragment extends BaseFragment<TaskPresenter> implements TaskPresenter.TaskUi<List<TaskItemModel>> {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private TaskAdapter mTaskAdapter;
    private PageHelper<TaskItemModel> mPageHelper;

    public TaskFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_task;
    }

    @Override
    public TaskPresenter onCreatePresenter() {
        return new TaskPresenter(this);
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {

        mTaskAdapter = new TaskAdapter(null);
        mRvList.setAdapter(mTaskAdapter);
        mTaskAdapter.setOnRvItemListener(new OnRvItemListener<TaskItemModel>() {
            @Override
            public void onItemClick(List<TaskItemModel> list, int position) {
                TaskItemModel itemModel = list.get(position);
                if (itemModel.getStatus() == 2) {
                    ToastUtil.show("已过期");
                    return;
                } else if (itemModel.getStatus() == 3) {
                    ToastUtil.show("已废弃");
                    return;
                }
                TaskDetailsActivity.startActivity(mContext, itemModel.getId());
            }
        });

        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.onGetList(type, mPageHelper.getPage());
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPageHelper.onRefresh();
                mPresenter.onGetList(type, mPageHelper.getPage());
            }
        });
        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, mTaskAdapter);
        mPresenter.onGetList(type, mPageHelper.getPage());
    }


    @Override
    public LifecycleProvider getLifecycleProvider() {
        return TaskFragment.this;
    }

    @Override
    public void onSuccess(List<TaskItemModel> data) {
        mPageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.onGetList(type, mPageHelper.getPage());
        });
    }
}