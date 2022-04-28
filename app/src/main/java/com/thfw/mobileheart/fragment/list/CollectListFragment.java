package com.thfw.mobileheart.fragment.list;

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
import com.thfw.base.utils.DataChangeHelper;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseDetailActivity;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.activity.read.BookIdeoDetailActivity;
import com.thfw.mobileheart.activity.test.TestBeginActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.mobileheart.adapter.CollectAdapter;
import com.thfw.mobileheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;


public class CollectListFragment extends BaseFragment<HistoryPresenter>
        implements HistoryPresenter.HistoryUi<List<CollectModel>>, DataChangeHelper.DataChangeListener {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private PageHelper mPageHelper;
    private CollectAdapter mCollectAdapter;

    public CollectListFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_collect_list;
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
        DataChangeHelper.getInstance().add(this);
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
                        TestBeginActivity.startActivity(mContext, collectModel.id);
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
                        ExerciseDetailActivity.startActivity(mContext,collectModel.id);
                        break;
                    case HistoryApi.TYPE_COLLECT_VIDEO:
                        VideoPlayActivity.startActivity(mContext, collectModel.id, false);
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
        return this;
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

    @Override
    public void onChanged(HashMap<String, Object> map) {
        if (!TYPE_COLLECT.equals(map.get(KEY_TYPE))) {
            return;
        }
        if (mCollectAdapter == null || EmptyUtil.isEmpty(mCollectAdapter.getDataList())) {
            return;
        }
        List<CollectModel> collectModels = mCollectAdapter.getDataList();
        int len = collectModels.size();


        int id = (int) map.get(KEY_ID);
        for (int i = 0; i < len; i++) {
            if (id == collectModels.get(i).id) {
                collectModels.remove(i);
                mCollectAdapter.notifyItemRemoved(i);
                break;
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataChangeHelper.getInstance().remove(this);
    }
}