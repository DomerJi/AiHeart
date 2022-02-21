package com.thfw.robotheart.fragments.me;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.CollectModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.CollectAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.ui.base.RobotBaseFragment;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class CollectFragment extends RobotBaseFragment<HistoryPresenter>
        implements HistoryPresenter.HistoryUi<List<CollectModel>> {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private PageHelper mPageHelper;
    private CollectAdapter mCollectAdapter;

    public CollectFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_collect;
    }

    @Override
    public HistoryPresenter onCreatePresenter() {
        return new HistoryPresenter(this);
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

        mCollectAdapter = new CollectAdapter(null);
        mRvList.setAdapter(mCollectAdapter);
        mCollectAdapter.setOnRvItemListener(new OnRvItemListener<CollectModel>() {
            @Override
            public void onItemClick(List<CollectModel> list, int position) {

            }
        });

        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.getCollectList(type, mPageHelper.getPage());
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPageHelper.onRefresh();
                mPresenter.getCollectList(type, mPageHelper.getPage());
            }
        });

        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, mCollectAdapter);
        mPresenter.getCollectList(type, mPageHelper.getPage());
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return CollectFragment.this;
    }

    @Override
    public void onSuccess(List<CollectModel> data) {
        mPageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.getCollectList(type, mPageHelper.getPage());
        });
    }
}