package com.thfw.robotheart.fragments.media;

import android.os.Handler;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.models.VideoModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.video.VideoPlayerActivity;
import com.thfw.robotheart.adapter.VideoEtcListAdapter;
import com.thfw.ui.base.RobotBaseFragment;
import com.thfw.ui.widget.LoadingView;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:15
 * Describe:Todo
 */
public class VideoEtcListFragment extends RobotBaseFragment {

    private String type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvEtcList;
    private LoadingView mLoadingView;
    private VideoEtcListAdapter mVideoEtcListAdapter;

    public VideoEtcListFragment(String type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_video_etc_list;
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
                mVideoEtcListAdapter = new VideoEtcListAdapter(null);
                mVideoEtcListAdapter.setOnRvItemListener(new OnRvItemListener<VideoEtcModel>() {
                    @Override
                    public void onItemClick(List<VideoEtcModel> list, int position) {
                        VideoPlayerActivity.startActivity(mContext, VideoModel.getVideoUrl().get(1));
                    }
                });
                mRvEtcList.setAdapter(mVideoEtcListAdapter);
            }
        }, 500);
    }
}
