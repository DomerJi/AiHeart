package com.thfw.robotheart.fragments.media;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.AudioPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HourChangeHelper;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.audio.AudioPlayerActivity;
import com.thfw.robotheart.adapter.AudioEtcListAdapter;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:15
 * Describe:Todo
 */
public class AudioEtcListFragment extends RobotBaseFragment<AudioPresenter>
        implements AudioPresenter.AudioUi<List<AudioEtcModel>>, HourChangeHelper.HourChangeListener {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvEtcList;
    private LoadingView mLoadingView;
    private AudioEtcListAdapter mAudioEtcListAdapter;

    private int page = 1;

    public AudioEtcListFragment(int type) {
        super();
        this.type = type;
    }


    @Override
    public int getContentView() {
        return R.layout.fragment_audio_etc_list;
    }

    @Override
    public AudioPresenter onCreatePresenter() {
        return new AudioPresenter(this);
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
        HourChangeHelper.getInstance().add(this);
    }

    @Override
    public void initData() {
        mPresenter.getAudioEtcList(type, page);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<AudioEtcModel> data) {
        if (page == 1) {
            if (EmptyUtil.isEmpty(data)) {
                mLoadingView.showEmpty();
                return;
            }
            mLoadingView.hide();
            mAudioEtcListAdapter = new AudioEtcListAdapter(data);
            mAudioEtcListAdapter.setOnRvItemListener(new OnRvItemListener<AudioEtcModel>() {
                @Override
                public void onItemClick(List<AudioEtcModel> list, int position) {
                    AudioPlayerActivity.startActivity(mContext, list.get(position));
                }
            });
            mRefreshLayout.setEnableLoadMore(true);
            mRvEtcList.setAdapter(mAudioEtcListAdapter);
        } else {
            mRefreshLayout.finishLoadMore(true);
            mAudioEtcListAdapter.addDataListNotify(data);
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

    @Override
    public void hourChanged(int collectionId, int hour, int musicId) {
        if (mAudioEtcListAdapter != null) {
            List<AudioEtcModel> dataList = mAudioEtcListAdapter.getDataList();
            if (EmptyUtil.isEmpty(dataList)) {
                return;
            }
            for (AudioEtcModel bean : dataList) {
                if (bean.getId() == collectionId) {
                    bean.setLastMusicId(musicId);
                    bean.setListenHistorySize(hour);
                    mAudioEtcListAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HourChangeHelper.getInstance().remove(this);
    }
}
