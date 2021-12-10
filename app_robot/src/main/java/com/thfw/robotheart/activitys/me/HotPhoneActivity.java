package com.thfw.robotheart.activitys.me;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.HotPhoneAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.MyRobotSearchView;

public class HotPhoneActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private com.thfw.ui.widget.MyRobotSearchView mRsvSearch;
    private androidx.recyclerview.widget.RecyclerView mRvList;

    @Override
    public int getContentView() {
        return R.layout.activity_hot_phone;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRsvSearch = (MyRobotSearchView) findViewById(R.id.rsv_search);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {
        mRvList.setAdapter(new HotPhoneAdapter(null));
    }
}