package com.thfw.robotheart.activitys.test;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TestModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TestPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.TestListAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

public class TestActivity extends RobotBaseActivity<TestPresenter> implements TestPresenter.TestUi<List<TestModel>> {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mSrlRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvTest;
    private TestListAdapter mTestListAdapter;
    private com.thfw.ui.widget.LoadingView mLoadingView;

    @Override
    public int getContentView() {
        return R.layout.activity_test;
    }

    @Override
    public TestPresenter onCreatePresenter() {
        return new TestPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mSrlRefreshLayout = (SmartRefreshLayout) findViewById(R.id.srl_refreshLayout);
        mRvTest = (RecyclerView) findViewById(R.id.rv_test);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mRvTest.setLayoutManager(new GridLayoutManager(mContext, 4));
        mPresenter.onGetList();
        mTestListAdapter = new TestListAdapter(null);
        mTestListAdapter.setOnRvItemListener(new OnRvItemListener<TestModel>() {
            @Override
            public void onItemClick(List<TestModel> list, int position) {
                TestDetailActivity.startActivity(mContext, list.get(position).getId());
            }
        });
        mRvTest.setAdapter(mTestListAdapter);
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
        mTestListAdapter.setDataListNotify(data);
    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            mPresenter.onGetList();
        });
    }
}