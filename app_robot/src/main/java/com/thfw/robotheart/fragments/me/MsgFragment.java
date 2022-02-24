package com.thfw.robotheart.fragments.me;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TaskItemModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TaskPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.TaskAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;


public class MsgFragment extends RobotBaseFragment<TaskPresenter> implements TaskPresenter.TaskUi<List<TaskItemModel>> {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private TaskAdapter mTaskAdapter;
    private PageHelper<TaskItemModel> mPageHelper;

    public MsgFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_msg;
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

            }
        });
        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, mTaskAdapter);
        mPresenter.onGetList(type, mPageHelper.getPage());

    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return MsgFragment.this;
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