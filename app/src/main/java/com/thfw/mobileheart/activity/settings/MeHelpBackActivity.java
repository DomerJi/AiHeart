package com.thfw.mobileheart.activity.settings;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.models.FeedBackModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.adapter.FeedBackAdapter;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

public class MeHelpBackActivity extends BaseActivity<OtherPresenter> implements OtherPresenter.OtherUi<List<FeedBackModel>> {

    private TitleView mTitleView;
    private RecyclerView mRvHelpHints;
    private SmartRefreshLayout mRefreshLayout;
    private LoadingView mLoadingView;
    private FeedBackAdapter feedBackAdapter;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MeHelpBackActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_me_help_back;
    }

    @Override
    public OtherPresenter onCreatePresenter() {
        return new OtherPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRvHelpHints = (RecyclerView) findViewById(R.id.rv_help_hints);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvHelpHints.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

    }


    @Override
    public void initData() {
        feedBackAdapter = new FeedBackAdapter(null);
        mRvHelpHints.setAdapter(feedBackAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableOverScrollDrag(true);
        mPresenter.onGetFeedBackList();

    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<FeedBackModel> data) {
        if (EmptyUtil.isEmpty(data)) {
            mLoadingView.showEmpty();
        } else {
            mLoadingView.hide();
            feedBackAdapter.setDataListNotify(data);
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (feedBackAdapter.getItemCount() == 0) {
            mLoadingView.showFail(v -> {
                mPresenter.onGetFeedBackList();
            });
        }
    }
}