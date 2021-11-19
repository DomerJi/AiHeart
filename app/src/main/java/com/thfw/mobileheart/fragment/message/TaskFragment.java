package com.thfw.mobileheart.fragment.message;

import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;

public class TaskFragment extends BaseFragment {


    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;

    public TaskFragment() {
    }

    @Override
    public int getContentView() {
        return R.layout.custom_refresh_recyclerview_layout;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void initData() {

    }
}