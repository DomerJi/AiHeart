package com.thfw.mobileheart.fragment.message;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.thfw.base.base.MsgType;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TaskItemModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TaskPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.task.MeTaskActivity;
import com.thfw.mobileheart.adapter.MsgTaskAdapter;
import com.thfw.mobileheart.util.MsgCountManager;
import com.thfw.mobileheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.thfw.user.models.User;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MsgTaskFragment extends BaseFragment<TaskPresenter> implements TaskPresenter.TaskUi<List<TaskItemModel>>, MsgCountManager.OnCountChangeListener {
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private MsgTaskAdapter mMsgAdapter;
    private PageHelper<TaskItemModel> mPageHelper;
    private int numTask;
    private boolean isLogin;
    private boolean needRefresh;


    @Override
    public int getContentView() {
        return R.layout.fragment_msg;
    }

    @Override
    public TaskPresenter onCreatePresenter() {
        return new TaskPresenter(this);
    }

    @Override
    public void initView() {
        isLogin = UserManager.getInstance().isTrueLogin();
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {
        mMsgAdapter = new MsgTaskAdapter(null);
        mRvList.setAdapter(mMsgAdapter);
        mMsgAdapter.setOnRvItemListener(new OnRvItemListener<TaskItemModel>() {
            @Override
            public void onItemClick(List<TaskItemModel> list, int position) {
                if (list.get(position).getStatus() == 0) {
                    MsgCountManager.getInstance().readMsg(MsgType.TASK, list.get(position).getId());
                }
                startActivity(new Intent(mContext, MeTaskActivity.class));
            }
        });
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.onGetMsgList(1, mPageHelper.getPage());
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPageHelper.onRefresh();
                mPresenter.onGetMsgList(1, mPageHelper.getPage());
            }
        });
        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, mMsgAdapter);
        mPresenter.onGetMsgList(1, mPageHelper.getPage());
        numTask = MsgCountManager.getInstance().getNumTask();
        MsgCountManager.getInstance().addOnCountChangeListener(this);

    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        LogUtil.d(TAG, "isVisible = " + isVisible);
        if (isVisible) {
            numTask = MsgCountManager.getInstance().getNumTask();
        }
        if (needRefresh) {
            needRefresh = false;
            if (mLoadingView != null) {
                mLoadingView.showLoading();
            }
            if (mMsgAdapter != null) {
                mMsgAdapter.setDataListNotify(null);
            }
            if (mPresenter != null && mPageHelper != null) {
                mPageHelper.onRefresh();
                mPresenter.onGetMsgList(1, mPageHelper.getPage());
            }
        }
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return MsgTaskFragment.this;
    }

    @Override
    public void onSuccess(List<TaskItemModel> data) {
        mPageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.onGetMsgList(1, mPageHelper.getPage());
        });
    }

    @Override
    public void onCount(int numTask, int numSystem) {
        if (mPresenter == null) {
            return;
        }
        if (this.numTask < numTask) {
            if (mRefreshLayout != null) {
                mRefreshLayout.autoRefresh();
            }
        }
    }

    @Override
    public void onItemState(int id, boolean read) {
        List<TaskItemModel> itemModels = mMsgAdapter.getDataList();
        if (!EmptyUtil.isEmpty(itemModels)) {
            for (TaskItemModel itemModel : itemModels) {
                if (id == itemModel.getId()) {
                    itemModel.setStatus(1);
                    mMsgAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void onReadAll(int type) {
        if (type != MsgCountManager.TYPE_SYSTEM && mMsgAdapter.getItemCount() != 0) {
            mMsgAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgCountManager.getInstance().removeOnCountChangeListener(this);
    }


    @Override
    public UserObserver addObserver() {
        return new UserObserver() {
            @Override
            public void onChanged(UserManager accountManager, User user) {
                if (isLogin != accountManager.isTrueLogin()) {
                    isLogin = accountManager.isTrueLogin();
                    if (isLogin) {
                        needRefresh = true;
                    } else {
                        if (mLoadingView != null) {
                            mLoadingView.showLoading();
                        }
                        if (mMsgAdapter != null) {
                            mMsgAdapter.setDataListNotify(null);
                        }
                    }
                }
            }
        };
    }
}