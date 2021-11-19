package com.thfw.mobileheart.fragment.list;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.mobileheart.activity.audio.AudioEtcActivity;
import com.thfw.mobileheart.adapter.AudioListAdapter;
import com.thfw.mobileheart.model.AudioModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;

import java.util.List;


public class AudioListFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;

    public AudioListFragment(String data) {
        super();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_audio_list;
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
        AudioListAdapter audioListAdapter = new AudioListAdapter(null);
        audioListAdapter.setOnRvItemListener(new OnRvItemListener<AudioModel>() {
            @Override
            public void onItemClick(List<AudioModel> list, int position) {
                startActivity(new Intent(mContext, AudioEtcActivity.class));
            }
        });
        mRvList.setAdapter(audioListAdapter);
    }

}