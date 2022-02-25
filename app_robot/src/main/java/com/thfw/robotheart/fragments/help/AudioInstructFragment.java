package com.thfw.robotheart.fragments.help;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.models.VoiceInstructionModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.adapter.VoiceInstructionAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.robotheart.view.CustomRefreshLayout;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AudioInstructFragment extends RobotBaseFragment<OtherPresenter> implements OtherPresenter.OtherUi<List<VoiceInstructionModel>> {


    private CustomRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private PageHelper<VoiceInstructionModel> mPageHelper;

    public AudioInstructFragment() {
        super();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_audio_instruct;
    }

    @Override
    public OtherPresenter onCreatePresenter() {
        return new OtherPresenter(this);
    }

    @Override
    public void initView() {

        mRefreshLayout = (CustomRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {
        VoiceInstructionAdapter voiceInstructionAdapter = new VoiceInstructionAdapter(null);
        mRvList.setAdapter(voiceInstructionAdapter);
        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, voiceInstructionAdapter);
        mPageHelper.setRefreshEnable(false);
        mPresenter.onGetVoiceInstruction(mPageHelper.getPage());
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.onGetVoiceInstruction(mPageHelper.getPage());
            }
        });


    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<VoiceInstructionModel> data) {
        mPageHelper.onSuccess(data, false);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.onGetVoiceInstruction(mPageHelper.getPage());
        });
    }
}