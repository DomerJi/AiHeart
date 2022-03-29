package com.thfw.mobileheart.activity.me;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.adapter.AssessReportAdapter;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

/**
 * 心理健康报告
 */
public class AssessReportActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvList;

    @Override
    public int getContentView() {
        return R.layout.activity_assess_report;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
    }

    @Override
    public void initData() {
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRvList.setAdapter(new AssessReportAdapter(null));
    }
}