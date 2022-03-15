package com.thfw.mobileheart.fragment.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.adapter.AudioListAdapter;
import com.thfw.mobileheart.util.PageHelper;
import com.thfw.ui.base.BaseFragment;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class AudioListFragment extends BaseFragment<AudioPresenter>
        implements AudioPresenter.AudioUi<List<AudioEtcModel>>, HourChangeHelper.HourChangeListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private int key;
    private LoadingView mLoadingView;
    private AudioListAdapter audioListAdapter;
    private PageHelper<AudioEtcModel> pageHelper;

    public AudioListFragment(int key) {
        super();
        this.key = key;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_audio_list;
    }

    @Override
    public AudioPresenter onCreatePresenter() {
        return new AudioPresenter(this);
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        HourChangeHelper.getInstance().add(this);
    }

    @Override
    public void initData() {

        audioListAdapter = new AudioListAdapter(null);
        audioListAdapter.setOnRvItemListener(new OnRvItemListener<AudioEtcModel>() {
            @Override
            public void onItemClick(List<AudioEtcModel> list, int position) {
                AudioPlayerActivity.startActivity(mContext, list.get(position));
            }
        });
        mRvList.setAdapter(audioListAdapter);
        pageHelper = new PageHelper<AudioEtcModel>(mLoadingView, mRefreshLayout, audioListAdapter);
        pageHelper.setRefreshEnable(false);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.getAudioEtcList(key, pageHelper.getPage());
            }
        });
        mPresenter.getAudioEtcList(key, pageHelper.getPage());
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<AudioEtcModel> data) {
        pageHelper.onSuccess(data);
    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        pageHelper.onFail(v -> {
            mPresenter.getAudioEtcList(key, pageHelper.getPage());
        });
    }

    @Override
    public void hourChanged(int collectionId, int hour, int musicId) {
        if (audioListAdapter != null) {
            List<AudioEtcModel> dataList = audioListAdapter.getDataList();
            if (EmptyUtil.isEmpty(dataList)) {
                return;
            }
            for (AudioEtcModel bean : dataList) {
                if (bean.getId() == collectionId) {
                    bean.setLastMusicId(musicId);
                    bean.setListenHistorySize(hour);
                    audioListAdapter.notifyDataSetChanged();
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