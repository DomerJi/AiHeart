package com.thfw.robotheart.fragments.me;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.PushModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TaskPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.adapter.MsgAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;


public class MsgFragment extends RobotBaseFragment<TaskPresenter> implements TaskPresenter.TaskUi<List<PushModel>> {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private MsgAdapter mMsgAdapter;
    private PageHelper<PushModel> mPageHelper;

    public MsgFragment(int type) {
        super();
        this.type = type;
    }

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

            }
        });
        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, mMsgAdapter);
        mPresenter.onGetMsgList(type, mPageHelper.getPage());

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
            mPresenter.onGetList(type, mPageHelper.getPage());
        });
    }
}