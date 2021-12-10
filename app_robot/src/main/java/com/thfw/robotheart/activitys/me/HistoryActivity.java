package com.thfw.robotheart.activitys.me;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.models.HistoryModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.HistoryAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

public class HistoryActivity extends RobotBaseActivity<HistoryPresenter> implements HistoryPresenter.HistoryUi<List<HistoryModel>> {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private com.thfw.ui.widget.LoadingView mLoadingView;

    private static final String KEY_TYPE = "key.type";
    private static final String KEY_TITLE = "key.title";
    private static final String KEY_RID = "key.rid";
    private int type;
    private int rid;

    public static void startActivity(Context context, int type) {
        startActivity(context, type, -1);
    }

    public static void startActivity(Context context, int type, int rid) {
        Intent intent = new Intent(context, HistoryActivity.class)
                .putExtra(KEY_TYPE, type)
                .putExtra(KEY_RID, rid);


        switch (type) {
            case HistoryApi.TYPE_TEST:
                intent.putExtra(KEY_TITLE, "我测的");
                break;
            case HistoryApi.TYPE_BOOK:
                intent.putExtra(KEY_TITLE, "我读的");
                break;
            case HistoryApi.TYPE_AUDIO:
                intent.putExtra(KEY_TITLE, "我听的");
                break;
            case HistoryApi.TYPE_VIDEO:
                intent.putExtra(KEY_TITLE, "我看的");
                break;
            case HistoryApi.TYPE_STUDY:
                intent.putExtra(KEY_TITLE, "我学的");
                break;
            case HistoryApi.TYPE_EXERCISE:
                intent.putExtra(KEY_TITLE, "我练的");
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
    }

    @Override
    public void initData() {


        type = getIntent().getIntExtra(KEY_TYPE, -1);
        rid = getIntent().getIntExtra(KEY_RID, -1);
        String title = getIntent().getStringExtra(KEY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            mTitleRobotView.setCenterText(title);
        }
        if (type == -1) {
            ToastUtil.show("参数错误");
            return;
        }
        mPresenter.getUserHistoryList(type, rid);
    }


    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<HistoryModel> data) {

        if (EmptyUtil.isEmpty(data)) {
            mLoadingView.showEmpty();
            return;
        }
        mLoadingView.hide();
        mRvList.setAdapter(new HistoryAdapter(data));

    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            initData();
        });
    }
}