package com.thfw.robotheart.activitys.talk;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ChatAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TalkHistoryActivity extends RobotBaseActivity<TalkPresenter> implements TalkPresenter.TalkUi<List<DialogTalkModel>> {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvHistory;
    private int scene;
    private ChatAdapter chatAdapter;
    private PageHelper pageHelper;
    private com.thfw.ui.widget.LoadingView mLoadingView;

    @Override
    public int getContentView() {
        return R.layout.activity_talk_history;
    }

    public static void startActivity(Context context, int scene) {
        context.startActivity(new Intent(context, TalkHistoryActivity.class)
                .putExtra(KEY_DATA, scene));
    }

    @Override
    public TalkPresenter onCreatePresenter() {
        return new TalkPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvHistory = (RecyclerView) findViewById(R.id.rv_history);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.onDialogHistory(scene, pageHelper.getPage());
            }
        });
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        scene = getIntent().getIntExtra(KEY_DATA, 1);
        chatAdapter = new ChatAdapter(null);
        chatAdapter.setOnRvItemListener(new OnRvItemListener<ChatEntity>() {
            @Override
            public void onItemClick(List<ChatEntity> list, int position) {

            }
        });
        mRvHistory.setAdapter(chatAdapter);

        pageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, chatAdapter);
        pageHelper.setRefreshEnable(false);
        mPresenter.onDialogHistory(scene, pageHelper.getPage());
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<DialogTalkModel> data) {
        pageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        pageHelper.onFail(v -> {
            mPresenter.onDialogHistory(scene, pageHelper.getPage());
        });
    }
}