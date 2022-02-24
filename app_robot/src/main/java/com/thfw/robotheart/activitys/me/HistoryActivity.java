package com.thfw.robotheart.activitys.me;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.HistoryModel;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.audio.AudioPlayerActivity;
import com.thfw.robotheart.activitys.exercise.ExerciseIngActivity;
import com.thfw.robotheart.activitys.test.TestDetailActivity;
import com.thfw.robotheart.activitys.text.BookDetailActivity;
import com.thfw.robotheart.activitys.video.VideoPlayerActivity;
import com.thfw.robotheart.adapter.HistoryAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends RobotBaseActivity<HistoryPresenter> implements HistoryPresenter.HistoryUi<List<HistoryModel>> {

    private static final String KEY_TYPE = "key.type";
    private static final String KEY_TITLE = "key.title";
    private static final String KEY_RID = "key.rid";
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private int type;
    private HistoryAdapter historyAdapter;
    private PageHelper<HistoryModel> pageHelper;


    public static void startActivity(Context context, int type) {
        Intent intent = new Intent(context, HistoryActivity.class)
                .putExtra(KEY_TYPE, type);


        switch (type) {
            case HistoryApi.TYPE_TEST:
                intent.putExtra(KEY_TITLE, MyApplication.getApp().getResources().getString(R.string.me_test));
                break;
            case HistoryApi.TYPE_BOOK:
                intent.putExtra(KEY_TITLE, MyApplication.getApp().getResources().getString(R.string.me_read));
                break;
            case HistoryApi.TYPE_AUDIO:
                intent.putExtra(KEY_TITLE, MyApplication.getApp().getResources().getString(R.string.me_listening));
                break;
            case HistoryApi.TYPE_VIDEO:
                intent.putExtra(KEY_TITLE, MyApplication.getApp().getResources().getString(R.string.me_see));
                break;
            case HistoryApi.TYPE_STUDY:
                intent.putExtra(KEY_TITLE, MyApplication.getApp().getResources().getString(R.string.me_study));
                break;
            case HistoryApi.TYPE_EXERCISE:
                intent.putExtra(KEY_TITLE, MyApplication.getApp().getResources().getString(R.string.me_exercise));
                break;
        }

        context.startActivity(intent);

    }

    @Override
    public int getContentView() {
        return R.layout.activity_history;
    }

    @Override
    public HistoryPresenter onCreatePresenter() {
        return new HistoryPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
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


        type = getIntent().getIntExtra(KEY_TYPE, -1);
        String title = getIntent().getStringExtra(KEY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            mTitleRobotView.setCenterText(title);
        }
        if (type == -1) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }

        historyAdapter = new HistoryAdapter(null);
        historyAdapter.setOnRvItemListener(new OnRvItemListener<HistoryModel>() {
            @Override
            public void onItemClick(List<HistoryModel> list, int position) {

                HistoryModel historyModel = list.get(position);
                if (historyModel instanceof HistoryModel.HistoryTestModel) { // 测评
                    HistoryModel.HistoryTestModel testModel = (HistoryModel.HistoryTestModel) historyModel;
                    TestDetailActivity.startActivity(mContext, testModel.getId());
//                    TestResultWebActivity.startActivity(mContext, new TestResultModel().setResultId(testModel.getId()));
                } else if (historyModel instanceof HistoryModel.HistoryVideoModel) { // 视频
                    HistoryModel.HistoryVideoModel videoModel = (HistoryModel.HistoryVideoModel) historyModel;
                    ArrayList<VideoEtcModel> videoList = new ArrayList<>();
                    VideoEtcModel videoEtcModel = new VideoEtcModel();
                    videoEtcModel.setId(videoModel.getId());
                    videoEtcModel.setTitle(videoModel.getTitle());
                    videoList.add(videoEtcModel);
                    VideoPlayerActivity.startActivity(mContext, videoList, 0);
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
                    BookDetailActivity.startActivity(mContext, bookModel.getId());
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