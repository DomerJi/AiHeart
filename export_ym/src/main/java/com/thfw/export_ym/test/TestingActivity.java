package com.thfw.export_ym.test;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.adapter.TestOneAdapter;
import com.thfw.base.OnRvItemListener;
import com.thfw.base.YmBaseActivity;
import com.thfw.export_ym.R;
import com.thfw.models.HttpResult;
import com.thfw.models.TestModel;
import com.thfw.net.ResponeThrowable;
import com.thfw.presenter.TestPresenter;
import com.thfw.util.EmptyUtil;
import com.thfw.view.LoadingView;
import com.thfw.view.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

public class TestingActivity extends YmBaseActivity<TestPresenter> implements TestPresenter.TestUi<List<TestModel>> {

    private TitleView mTitleView;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvTest;
    private LoadingView mLoadingView;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestingActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_testing_ym;
    }

    @Override
    public TestPresenter onCreatePresenter() {
        return new TestPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);
        mRvTest = (RecyclerView) findViewById(R.id.rv_test);
        mRvTest.setLayoutManager(new LinearLayoutManager(mContext));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mPresenter.onGetList();
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<TestModel> data) {
        if (EmptyUtil.isEmpty(data)) {
            mLoadingView.showEmpty();
            return;
        }
        mLoadingView.hide();
        TestOneAdapter testOneAdapter = new TestOneAdapter(data);
        testOneAdapter.setOnRvItemListener(new OnRvItemListener<TestModel>() {
            @Override
            public void onItemClick(List<TestModel> list, int position) {
                TestBeginActivity.startActivity(mContext, list.get(position).getId());
                finish();
            }
        });
        mRvTest.setAdapter(testOneAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mLoadingView != null && mLoadingView.isLoadFail()) {
            mLoadingView.reTry();
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (!HttpResult.noTokenFailShowMsg(throwable.code)) {
            mLoadingView.showFail(v -> {
                initData();
            });
        }
    }
}