package com.thfw.mobileheart.activity.me;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.adapter.ServiceHotLineAdapter;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

public class ServiceHotLineActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private androidx.recyclerview.widget.RecyclerView mRvHotLine;

    @Override
    public int getContentView() {
        return R.layout.activity_service_hot_line;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRvHotLine = (RecyclerView) findViewById(R.id.rv_hot_line);
    }

    @Override
    public void initData() {
        mRvHotLine.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRvHotLine.setAdapter(new ServiceHotLineAdapter(null));
    }
}