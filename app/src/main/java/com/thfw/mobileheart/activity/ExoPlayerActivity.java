package com.thfw.mobileheart.activity;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.adapter.VideoListAdapter;
import com.thfw.base.models.VideoModel;
import com.thfw.mobileheart.util.ExoPlayerFactory;
import com.thfw.mobileheart.view.CustomDividerItemDecoration;
import com.thfw.ui.base.BaseActivity;

import java.util.List;

public class ExoPlayerActivity extends BaseActivity {


    private androidx.recyclerview.widget.RecyclerView mRvVideo;
    private boolean isPlaying = true;

    @Override
    public int getContentView() {
        return R.layout.activity_exo_player;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRvVideo = findViewById(R.id.rv_video);
        mRvVideo.addItemDecoration(new CustomDividerItemDecoration(mContext,
                R.drawable.list_divider_height_10px));
        mRvVideo.setLayoutManager(new LinearLayoutManager(mContext));
        VideoListAdapter videoListAdapter = new VideoListAdapter(VideoModel.getVideoUrl());
        videoListAdapter.setOnRvItemListener(new OnRvItemListener<VideoModel>() {
            @Override
            public void onItemClick(List<VideoModel> list, int position) {
                VideoPlayActivity.startActivity(mContext, list.get(position));
                finish();
            }
        });
        mRvVideo.setAdapter(videoListAdapter);
    }

    @Override
    public void initData() {
        ExoPlayerFactory.with(mContext).builder(ExoPlayerFactory.EXO_VIDEO);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ExoPlayerFactory.getExoPlayer().pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ExoPlayerFactory.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying) {
            ExoPlayerFactory.getExoPlayer().play();
        }
    }
}