package com.thfw.robotheart.fragments.help;

import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.TaskAdapter;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.ui.widget.LoadingView;

public class CommonProblemFragment extends RobotBaseFragment {


    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;

    public CommonProblemFragment() {
        super();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_common_problem;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
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
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingView.hide();
                mRvList.setAdapter(new TaskAdapter(null));
            }
        }, 1500);

    }
}