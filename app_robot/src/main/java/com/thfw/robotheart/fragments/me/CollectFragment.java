package com.thfw.robotheart.fragments.me;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.CollectModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.audio.AudioPlayerActivity;
import com.thfw.robotheart.activitys.exercise.ExerciseDetailsActivity;
import com.thfw.robotheart.activitys.test.TestDetailActivity;
import com.thfw.robotheart.activitys.text.BookDetailActivity;
import com.thfw.robotheart.activitys.text.BookIdeoDetailActivity;
import com.thfw.robotheart.activitys.video.VideoPlayerActivity;
import com.thfw.robotheart.adapter.CollectAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class CollectFragment extends RobotBaseFragment<HistoryPresenter>
        implements HistoryPresenter.HistoryUi<List<CollectModel>> {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private PageHelper mPageHelper;
    private CollectAdapter mCollectAdapter;

    public CollectFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_collect;
    }

    @Override
    public HistoryPresenter onCreatePresenter() {
        return new HistoryPresenter(this);
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {

        mCollectAdapter = new CollectAdapter(null);
        mRvList.setAdapter(mCollectAdapter);
        mCollectAdapter.setOnRvItemListener(new OnRvItemListener<CollectModel>() {
            @Override
            public void onItemClick(List<CollectModel> list, int position) {
                CollectModel collectModel = list.get(position);
                switch (type) {
                    case HistoryApi.TYPE_COLLECT_TEST:
                        TestDetailActivity.startActivity(mContext, collectModel.id);
                        break;
                    case HistoryApi.TYPE_COLLECT_AUDIO:
                        AudioEtcModel audioEtcModel = new AudioEtcModel();
                        audioEtcModel.setTitle(collectModel.title);
                        audioEtcModel.setId(collectModel.id);
                        AudioPlayerActivity.startActivity(mContext, audioEtcModel);
                        break;
                    case HistoryApi.TYPE_COLLECT_BOOK:
                        BookDetailActivity.startActivity(mContext, collectModel.id);
                        break;
                    case HistoryApi.TYPE_COLLECT_IDEO_BOOK:
                        BookIdeoDetailActivity.startActivity(mContext, collectModel.id);
                        break;
                    case HistoryApi.TYPE_COLLECT_TOOL:
                        ExerciseDetailsActivity.startActivity(mContext, collectModel.id);
                        break;
                    case HistoryApi.TYPE_COLLECT_VIDEO:
                        VideoPlayerActivity.startActivity(mContext, collectModel.id, false);
                        break;
                }
            }
        });

        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.getCollectList(type, mPageHelper.getPage());
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPageHelper.onRefresh();
                mPresenter.getCollectList(type, mPageHelper.getPage());
            }
        });

        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, mCollectAdapter);
        mPresenter.getCollectList(type, mPageHelper.getPage());
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return CollectFragment.this;
    }

    @Override
    public void onSuccess(List<CollectModel> data) {
        mPageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.getCollectList(type, mPageHelper.getPage());
        });
    }
}