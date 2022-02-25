package com.thfw.robotheart.fragments.help;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.CommonProblemModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.me.SimpleTextActivity;
import com.thfw.robotheart.adapter.CommonProblemAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.robotheart.view.CustomRefreshLayout;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommonProblemFragment extends RobotBaseFragment<OtherPresenter> implements OtherPresenter.OtherUi<List<CommonProblemModel>> {


    private CustomRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private PageHelper<CommonProblemModel> mPageHelper;

    public CommonProblemFragment() {
        super();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_common_problem;
    }

    @Override
    public OtherPresenter onCreatePresenter() {
        return new OtherPresenter(this);
    }

    @Override
    public void initView() {

        mRefreshLayout = (CustomRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {
        CommonProblemAdapter commonProblemAdapter = new CommonProblemAdapter(null);
        commonProblemAdapter.setOnRvItemListener(new OnRvItemListener<CommonProblemModel>() {
            @Override
            public void onItemClick(List<CommonProblemModel> list, int position) {
                SimpleTextActivity.startActivity(mContext,
                        list.get(position).getQuestion(),
                        list.get(position).getAnswer());
            }
        });
        mRvList.setAdapter(commonProblemAdapter);
        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, commonProblemAdapter);
        mPageHelper.setRefreshEnable(false);
        mPresenter.onGetCommonProblem(mPageHelper.getPage());
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.onGetCommonProblem(mPageHelper.getPage());
            }
        });


    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<CommonProblemModel> data) {
        mPageHelper.onSuccess(data, false);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.onGetCommonProblem(mPageHelper.getPage());
        });
    }
}