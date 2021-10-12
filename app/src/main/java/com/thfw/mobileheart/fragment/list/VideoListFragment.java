package com.thfw.mobileheart.fragment.list;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.mobileheart.activity.audio.AudioEtcActivity;
import com.thfw.mobileheart.adapter.HomeVideoListAdapter;
import com.thfw.mobileheart.model.AudioModel;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseFragment;

import java.util.List;


public class VideoListFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;

    public VideoListFragment(String data) {
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
        HomeVideoListAdapter homeVideoListAdapter = new HomeVideoListAdapter(null);
        homeVideoListAdapter.setOnRvItemListener(new OnRvItemListener<AudioModel>() {
            @Override
            public void onItemClick(List<AudioModel> list, int position) {
                startActivity(new Intent(mContext, AudioEtcActivity.class));
            }
        });
        mRvList.setAdapter(homeVideoListAdapter);
    }

}