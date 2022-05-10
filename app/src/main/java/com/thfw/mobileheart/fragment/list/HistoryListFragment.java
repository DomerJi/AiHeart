package com.thfw.mobileheart.fragment.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.HistoryModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseIngActivity;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.activity.read.BookIdeoDetailActivity;
import com.thfw.mobileheart.activity.test.TestBeginActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.mobileheart.adapter.HistoryAdapter;
import com.thfw.mobileheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HistoryListFragment extends BaseFragment<HistoryPresenter> implements HistoryPresenter.HistoryUi<List<HistoryModel>> {

    private SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private int type;
    private HistoryAdapter historyAdapter;
    private PageHelper<HistoryModel> pageHelper;

    public HistoryListFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_me_history_list;
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
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.getUserHistoryList(type, pageHelper.getPage());
            }
        });
    }

    @Override
    public void initData() {


        historyAdapter = new HistoryAdapter(null);
        historyAdapter.setOnRvItemListener(new OnRvItemListener<HistoryModel>() {
            @Override
            public void onItemClick(List<HistoryModel> list, int position) {

                HistoryModel historyModel = list.get(position);
                if (historyModel instanceof HistoryModel.HistoryTestModel) { // 测评
                    HistoryModel.HistoryTestModel testModel = (HistoryModel.HistoryTestModel) historyModel;
                    TestBeginActivity.startActivity(mContext, testModel.getId());
                } else if (historyModel instanceof HistoryModel.HistoryVideoModel) { // 视频
                    HistoryModel.HistoryVideoModel videoModel = (HistoryModel.HistoryVideoModel) historyModel;
                    VideoPlayActivity.startActivity(mContext, videoModel.getId(), false);
                } else if (historyModel instanceof HistoryModel.HistoryAudioModel) { // 音频
                    HistoryModel.HistoryAudioModel audioModel = (HistoryModel.HistoryAudioModel) historyModel;
                    AudioEtcModel audioEtcModel = new AudioEtcModel();
                    audioEtcModel.setTitle(audioModel.getCollectionTitle());
                    audioEtcModel.setId(audioModel.getCollectionId());
                    audioEtcModel.setLastMusicId(audioModel.getId());
                    AudioPlayerActivity.startActivity(mContext, audioEtcModel);
                } else if (historyModel instanceof HistoryModel.HistoryBookModel) { // 科普文章
                    HistoryModel.HistoryBookModel bookModel = (HistoryModel.HistoryBookModel) historyModel;
                    BookDetailActivity.startActivity(mContext, bookModel.getId());
                } else if (historyModel instanceof HistoryModel.HistoryStudyModel) { // 思政文章
                    HistoryModel.HistoryStudyModel bookModel = (HistoryModel.HistoryStudyModel) historyModel;
                    BookIdeoDetailActivity.startActivity(mContext, bookModel.getId());
                } else if (historyModel instanceof HistoryModel.HistoryExerciseModel) { // 工具包
                    HistoryModel.HistoryExerciseModel exerciseModel = (HistoryModel.HistoryExerciseModel) historyModel;
                    ExerciseIngActivity.startActivity(mContext, exerciseModel.getId(), exerciseModel.isUsed());
                }
            }
        });

        mRvList.setAdapter(historyAdapter);
        pageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, historyAdapter);
        pageHelper.setRefreshEnable(false);
        mPresenter.getUserHistoryList(type, pageHelper.getPage());
    }


    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<HistoryModel> data) {
        pageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        pageHelper.onFail(v -> {
            mPresenter.getUserHistoryList(type, pageHelper.getPage());
        });
    }
}