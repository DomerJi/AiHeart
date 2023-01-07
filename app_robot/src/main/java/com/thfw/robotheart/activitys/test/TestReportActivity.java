package com.thfw.robotheart.activitys.test;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ReportTestModel;
import com.thfw.base.models.TestResultModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TestPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.adapter.ReportTestAdapter;
import com.thfw.robotheart.lhxk.InstructScrollHelper;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 测评报告列表页
 */
public class TestReportActivity extends RobotBaseActivity<TestPresenter> implements TestPresenter.TestUi<List<ReportTestModel>> {

    private static final String KEY_RID = "key.rid";
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private int rid;
    private PageHelper pageHelper;
    private ReportTestAdapter reportTestAdapter;

    public static void startActivity(Context context, int rid) {
        context.startActivity(new Intent(context, TestReportActivity.class).putExtra(KEY_RID, rid));
    }

    public static void startActivity(Context context) {
        startActivity(context, -1);
    }


    @Override
    public int getContentView() {
        return R.layout.activity_test_report;
    }

    @Override
    public TestPresenter onCreatePresenter() {
        return new TestPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.onResultHistory(rid, pageHelper.getPage());
            }
        });

        reportTestAdapter = new ReportTestAdapter(null);
        reportTestAdapter.setOnRvItemListener(new OnRvItemListener<ReportTestModel>() {
            @Override
            public void onItemClick(List<ReportTestModel> list, int position) {
                if (list.get(position).isHide()) {
                    DialogRobotFactory.createSimple(TestReportActivity.this, "本报告暂不支持查看");
                } else {
                    TestResultWebActivity.startActivity(mContext, new TestResultModel().setResultId(list.get(position).getId()));
                }
            }
        });
        mRvList.setAdapter(reportTestAdapter);

    }

    @Override
    public void initData() {
        rid = getIntent().getIntExtra(KEY_RID, -1);
        pageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, reportTestAdapter);
        pageHelper.setRefreshEnable(false);
        mPresenter.onResultHistory(rid, pageHelper.getPage());
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        new InstructScrollHelper(TestDetailActivity.class, mRvList);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return TestReportActivity.this;
    }

    @Override
    public void onSuccess(List<ReportTestModel> data) {
        pageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        pageHelper.onFail(v -> {
            mPresenter.onResultHistory(rid, pageHelper.getPage());
        });
    }
}