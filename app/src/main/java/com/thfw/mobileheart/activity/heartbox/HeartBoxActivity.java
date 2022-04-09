package com.thfw.mobileheart.activity.heartbox;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.HeartBoxEntity;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.adapter.HeartBoxAdapter;
import com.thfw.ui.widget.TitleView;

import java.util.List;

public class HeartBoxActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvHome;

    @Override
    public int getContentView() {
        return R.layout.activity_heart_box;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvHome = (RecyclerView) findViewById(R.id.rv_home);
    }

    @Override
    public void initData() {
        mRvHome.setLayoutManager(new LinearLayoutManager(mContext));
        HeartBoxAdapter heartBoxAdapter = new HeartBoxAdapter(null);
        heartBoxAdapter.setOnRvItemListener(new OnRvItemListener<HeartBoxEntity>() {
            @Override
            public void onItemClick(List<HeartBoxEntity> list, int position) {
                startActivity(new Intent(mContext, MeHeartBoxActivity.class));
            }
        });
        mRvHome.setAdapter(heartBoxAdapter);
    }
}