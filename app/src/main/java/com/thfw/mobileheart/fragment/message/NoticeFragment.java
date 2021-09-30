package com.thfw.mobileheart.fragment.message;

import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseFragment;

public class NoticeFragment extends BaseFragment {


    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;

    public NoticeFragment() {
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
        mRvList.setBackgroundColor(getResources().getColor(R.color.dot_red));
    }

    @Override
    public void initData() {

    }
}