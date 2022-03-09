package com.thfw.robotheart.fragments.me;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.PushModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TaskPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.adapter.MsgAdapter;
import com.thfw.robotheart.push.helper.PushHandle;
import com.thfw.robotheart.util.MsgCountManager;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MsgFragment extends RobotBaseFragment<TaskPresenter> implements TaskPresenter.TaskUi<List<PushModel>>, MsgCountManager.OnCountChangeListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private MsgAdapter mMsgAdapter;
    private PageHelper<PushModel> mPageHelper;
    private int numSystem;


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
        mMsgAdapter = new MsgAdapter(null);
        mRvList.setAdapter(mMsgAdapter);
        mMsgAdapter.setOnRvItemListener(new OnRvItemListener<PushModel>() {
            @Override
            public void onItemClick(List<PushModel> list, int position) {
                PushModel pushModel = list.get(position);
                PushHandle.handleMessage(mContext, pushModel);
            }
        });
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.onGetMsgList(2, mPageHelper.getPage());
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPageHelper.onRefresh();
                mPresenter.onGetMsgList(2, mPageHelper.getPage());
            }
        });
        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, mMsgAdapter);
        mPresenter.onGetMsgList(2, mPageHelper.getPage());
        numSystem = MsgCountManager.getInstance().getNumSystem();
        MsgCountManager.getInstance().addOnCountChangeListener(this);

    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        LogUtil.d(TAG, "isVisible = " + isVisible);
        if (isVisible) {
            numSystem = MsgCountManager.getInstance().getNumSystem();
        }
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return MsgFragment.this;
    }

    @Override
    public void onSuccess(List<PushModel> data) {
        mPageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.onGetMsgList(2, mPageHelper.getPage());
        });
    }

    @Override
    public void onCount(int numTask, int numSystem) {
        if (mPresenter == null) {
            return;
        }
        if (this.numSystem < numSystem) {
            if (mRefreshLayout != null) {
                mRefreshLayout.autoRefresh();
            }
        }
    }

    @Override
    public void onItemState(int id, boolean read) {
        List<PushModel> itemModels = mMsgAdapter.getDataList();
        if (!EmptyUtil.isEmpty(itemModels)) {
            for (PushModel itemModel : itemModels) {
                if (id == itemModel.getId()) {
                    itemModel.setReadStatus(1);
                    mMsgAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void onReadAll(int type) {
        if (type != MsgCountManager.TYPE_TASK) {
            mMsgAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgCountManager.getInstance().removeOnCountChangeListener(this);
    }
}