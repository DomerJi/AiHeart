package com.thfw.mobileheart.activity.test;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TestModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TestPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.adapter.TestOneAdapter;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

public class TestingActivity extends BaseActivity<TestPresenter> implements TestPresenter.TestUi<List<TestModel>> {

    private com.thfw.ui.widget.TitleView mTitleView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvTest;
    private com.thfw.ui.widget.LoadingView mLoadingView;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestingActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_testing;
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
            }
        });
        mRvTest.setAdapter(testOneAdapter);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            initData();
        });
    }
}