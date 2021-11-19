package com.thfw.mobileheart.activity.test;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.mobileheart.adapter.TestOneAdapter;
import com.thfw.mobileheart.model.TestModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

import java.util.List;

public class TestingActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvTest;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestingActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_testing;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvTest = (RecyclerView) findViewById(R.id.rv_test);
    }

    @Override
    public void initData() {
        mRvTest.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        TestOneAdapter testOneAdapter = new TestOneAdapter(null);
        testOneAdapter.setOnRvItemListener(new OnRvItemListener<TestModel>() {
            @Override
            public void onItemClick(List<TestModel> list, int position) {
                startActivity(new Intent(mContext, TestBeginActivity.class));
            }
        });
        mRvTest.setAdapter(testOneAdapter);
    }
}