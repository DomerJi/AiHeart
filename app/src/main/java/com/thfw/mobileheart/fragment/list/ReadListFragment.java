package com.thfw.mobileheart.fragment.list;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.mobileheart.activity.read.ReadEtcActivity;
import com.thfw.mobileheart.adapter.ReadOneAdapter;
import com.thfw.base.models.ReadModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;

import java.util.List;


public class ReadListFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;

    public ReadListFragment(String data) {
        super();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_video_list;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
    }

    @Override
    public void initData() {
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        ReadOneAdapter readOneAdapter = new ReadOneAdapter(null);
        readOneAdapter.setOnRvItemListener(new OnRvItemListener<ReadModel>() {
            @Override
            public void onItemClick(List<ReadModel> list, int position) {
                startActivity(new Intent(mContext, ReadEtcActivity.class));
            }
        });
        mRvList.setAdapter(readOneAdapter);
    }

}