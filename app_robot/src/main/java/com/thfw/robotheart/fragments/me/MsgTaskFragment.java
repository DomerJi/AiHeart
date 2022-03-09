package com.thfw.robotheart.fragments.me;

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
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.task.TaskActivity;
import com.thfw.robotheart.adapter.MsgTaskAdapter;
import com.thfw.robotheart.util.MsgCountManager;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MsgTaskFragment extends RobotBaseFragment<TaskPresenter> implements TaskPresenter.TaskUi<List<TaskItemModel>>, MsgCountManager.OnCountChangeListener {
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private MsgTaskAdapter mMsgAdapter;
    private PageHelper<TaskItemModel> mPageHelper;
    private int numTask;


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
                MsgCountManager.getInstance().readMsg(MsgType.TASK, list.get(position).getId());
                startActivity(new Intent(mContext, TaskActivity.class));
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
        if (type != MsgCountManager.TYPE_SYSTEM) {
            mMsgAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgCountManager.getInstance().removeOnCountChangeListener(this);
    }
}