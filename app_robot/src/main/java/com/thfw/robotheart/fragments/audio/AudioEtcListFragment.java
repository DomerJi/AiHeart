package com.thfw.robotheart.fragments.audio;

import android.content.Intent;
import android.os.Handler;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.audio.AudioPlayerActivity;
import com.thfw.robotheart.adapter.AudioEtcListAdapter;
import com.thfw.ui.base.RobotBaseFragment;
import com.thfw.ui.widget.LoadingView;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:15
 * Describe:Todo
 */
public class AudioEtcListFragment extends RobotBaseFragment {

    private String type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvEtcList;
    private LoadingView mLoadingView;
    private AudioEtcListAdapter mAudioEtcListAdapter;

    public AudioEtcListFragment(String type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_audio_etc_list;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvEtcList = (RecyclerView) findViewById(R.id.rvEtcList);
        mRvEtcList.setLayoutManager(new GridLayoutManager(mContext, 4));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingView.hide();
                mAudioEtcListAdapter = new AudioEtcListAdapter(null);
                mAudioEtcListAdapter.setOnRvItemListener(new OnRvItemListener<AudioEtcModel>() {
                    @Override
                    public void onItemClick(List<AudioEtcModel> list, int position) {
                        startActivity(new Intent(mContext, AudioPlayerActivity.class));
                    }
                });
                mRvEtcList.setAdapter(mAudioEtcListAdapter);
            }
        }, 500);
    }
}
