package com.thfw.mobileheart.fragment.list;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.models.VideoTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.VideoPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.video.VideoHomeActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.mobileheart.adapter.HomeVideoListAdapter;
import com.thfw.mobileheart.adapter.VideoChildTypeAdapter;
import com.thfw.mobileheart.util.FragmentLoader;
import com.thfw.mobileheart.util.PageHelper;
import com.thfw.ui.base.BaseFragment;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class VideoListFragment extends BaseFragment<VideoPresenter> implements VideoPresenter.VideoUi<List<VideoEtcModel>> {

    public static final String KEY_CHILD_TYPE = "key.child.type";
    private static final String KEY_INDEX = "key.video.index";
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private int type;
    private PageHelper<VideoEtcModel> mPageHelper;
    private RecyclerView mRvChildren;
    private VideoChildTypeAdapter videoChildTypeAdapter;

    public VideoListFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        SharePreferenceUtil.setInt(KEY_INDEX + type, -1);
    }

    public void onReSelected() {
        if (videoChildTypeAdapter != null) {
            videoChildTypeAdapter.resetSelectedIndex();
        }
    }

    @Override
    protected void onReCreateView() {
        super.onReCreateView();
        if (mLoadingView != null) {
            mLoadingView.showLoading();
        }
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_video_list;
    }

    @Override
    public VideoPresenter onCreatePresenter() {
        return new VideoPresenter(this);
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        if (getActivity() instanceof VideoHomeActivity) {
            VideoHomeActivity videoHomeActivity = (VideoHomeActivity) getActivity();
            videoHomeActivity.setRecyclerView(mRvList);
        }
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mRvChildren = (RecyclerView) findViewById(R.id.rv_children);
        mRvChildren.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        if (getArguments() != null && !getArguments().isEmpty()) {
            ArrayList<VideoTypeModel> videoTypeModels = (ArrayList<VideoTypeModel>) getArguments().getSerializable(KEY_CHILD_TYPE);
            videoChildTypeAdapter = new VideoChildTypeAdapter(videoTypeModels);
            FragmentLoader mLoader = new FragmentLoader(getChildFragmentManager(), R.id.fl_content01);
            videoChildTypeAdapter.setOnRvItemListener(new OnRvItemListener<VideoTypeModel>() {

                private VideoListFragment mVideoListFragment;

                @Override
                public void onItemClick(List<VideoTypeModel> list, int position) {
                    LogUtil.d(TAG, "onItemClick -> " + position);
                    if (position == -1) {
                        mLoader.hide();
                        return;
                    }
                    int childType = list.get(position).id;
                    Fragment fragment = mLoader.load(childType);
                    if (fragment == null) {
                        mLoader.add(childType, new VideoListFragment(childType));
                    }
                    mVideoListFragment = (VideoListFragment) mLoader.load(childType);
                    LogUtil.d(TAG, "onItemClick -> " + position + mVideoListFragment);
                }
            });
            mRvChildren.setAdapter(videoChildTypeAdapter);
        }
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (!isVisible) {
            if (videoChildTypeAdapter != null) {
                int index = videoChildTypeAdapter.getmSelectedIndex();
                SharePreferenceUtil.setInt(KEY_INDEX + type, index);
                LogUtil.d("JSP_" + type, "index = " + index);
            }

        } else {
            if (videoChildTypeAdapter != null) {
                int cacheIndex = SharePreferenceUtil.getInt(KEY_INDEX + type, -1);
                videoChildTypeAdapter.setSelectedIndex(cacheIndex);
                videoChildTypeAdapter.notifyDataSetChanged();
                LogUtil.d("JSP_" + type, "index cache = " + cacheIndex);
            }
        }
    }

    @Override
    public void initData() {

        HomeVideoListAdapter homeVideoListAdapter = new HomeVideoListAdapter(null);
        homeVideoListAdapter.setOnRvItemListener(new OnRvItemListener<VideoEtcModel>() {
            @Override
            public void onItemClick(List<VideoEtcModel> list, int position) {
                VideoPlayActivity.startActivity(mContext, list.get(position).getId(), false);
            }
        });
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.getVideoList(type, mPageHelper.getPage());
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPageHelper.onRefresh();
                mPresenter.getVideoList(type, mPageHelper.getPage());
            }
        });

        mRvList.setAdapter(homeVideoListAdapter);

        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, homeVideoListAdapter);

        mPresenter.getVideoList(type, mPageHelper.getPage());
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<VideoEtcModel> data) {
        mPageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.getVideoList(type, mPageHelper.getPage());
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharePreferenceUtil.setInt(KEY_INDEX + type, -1);
    }
}