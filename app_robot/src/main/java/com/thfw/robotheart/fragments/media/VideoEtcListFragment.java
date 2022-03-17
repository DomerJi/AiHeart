package com.thfw.robotheart.fragments.media;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.VideoPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.video.VideoPlayerActivity;
import com.thfw.robotheart.adapter.VideoEtcListAdapter;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:15
 * Describe:Todo
 */
public class VideoEtcListFragment extends RobotBaseFragment<VideoPresenter> implements VideoPresenter.VideoUi<List<VideoEtcModel>> {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvEtcList;
    private LoadingView mLoadingView;
    private VideoEtcListAdapter mVideoEtcListAdapter;

    private int page = 1;

    public VideoEtcListFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_video_etc_list;
    }

    @Override
    public VideoPresenter onCreatePresenter() {
        return new VideoPresenter(this);
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvEtcList = (RecyclerView) findViewById(R.id.rvEtcList);
        mRvEtcList.setLayoutManager(new GridLayoutManager(mContext, 4));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);

        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                initData();
            }
        });
    }

    @Override
    public void initData() {
        mPresenter.getVideoList(type, page);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<VideoEtcModel> data) {
        if (page == 1) {
            if (EmptyUtil.isEmpty(data)) {
                mLoadingView.showEmpty();
                return;
            }
            mLoadingView.hide();
            mVideoEtcListAdapter = new VideoEtcListAdapter(data);
            mVideoEtcListAdapter.setOnRvItemListener(new OnRvItemListener<VideoEtcModel>() {
                @Override
                public void onItemClick(List<VideoEtcModel> list, int position) {
                    VideoPlayerActivity.startActivity(mContext, list.get(position).getId(), false);
                }
            });
            mRefreshLayout.setEnableLoadMore(true);
            mRvEtcList.setAdapter(mVideoEtcListAdapter);
        } else {
            mRefreshLayout.finishLoadMore(true);
            mVideoEtcListAdapter.addDataListNotify(data);
            mRefreshLayout.setNoMoreData(EmptyUtil.isEmpty(data));
        }
        page++;
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (page == 1) {
            mLoadingView.showFail(v -> {
                initData();
            });
        } else {
            mRefreshLayout.finishLoadMore(false);
        }
    }
}
