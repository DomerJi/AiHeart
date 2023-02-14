package com.thfw.mobileheart.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.CommonProblemModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.adapter.CommonProblemAdapter;
import com.thfw.mobileheart.lhxk.InstructScrollHelper;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.mobileheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HelpBackActivity extends BaseActivity<OtherPresenter> implements OtherPresenter.OtherUi<List<CommonProblemModel>> {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.TextView mTvMeBack;
    private androidx.recyclerview.widget.RecyclerView mRvHelpHints;
    private SmartRefreshLayout mRefreshLayout;
    private LoadingView mLoadingView;
    private PageHelper<CommonProblemModel> mPageHelper;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, HelpBackActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_help_back;
    }

    @Override
    public OtherPresenter onCreatePresenter() {
        return new OtherPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTvMeBack = (TextView) findViewById(R.id.tv_me_back);
        mRvHelpHints = (RecyclerView) findViewById(R.id.rv_help_hints);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvHelpHints.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mTvMeBack.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MeWillHelpBackActivity.class));
        });
        if (LogUtil.isLogEnabled()) {
            mTitleView.setRightText("历史反馈");
            mTitleView.setRightOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mContext, MeHelpBackActivity.class));
                }
            });
        } else {
            mTitleView.setRightText("");
        }
    }


    @Override
    public void initData() {
        CommonProblemAdapter commonProblemAdapter = new CommonProblemAdapter(null);
        commonProblemAdapter.setOnRvItemListener(new OnRvItemListener<CommonProblemModel>() {
            @Override
            public void onItemClick(List<CommonProblemModel> list, int position) {
                SimpleTextActivity.startActivity(mContext, list.get(position).getQuestion(), list.get(position).getAnswer());
            }
        });
        mRvHelpHints.setAdapter(commonProblemAdapter);
        mPageHelper = new PageHelper<CommonProblemModel>(mLoadingView, mRefreshLayout, commonProblemAdapter);
        mPageHelper.setRefreshEnable(false);
        mPresenter.onGetCommonProblem(mPageHelper.getPage());
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.onGetCommonProblem(mPageHelper.getPage());
            }
        });


    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<CommonProblemModel> data) {
        mPageHelper.onSuccess(data, false);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.onGetCommonProblem(mPageHelper.getPage());
        });
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        LhXkHelper.putAction(HelpBackActivity.class, new SpeechToAction("我要反馈", () -> {
            mTvMeBack.performClick();
        }));
        LhXkHelper.putAction(HelpBackActivity.class, new SpeechToAction("历史反馈", () -> {
            mTitleView.getTvRight().performClick();
        }));
        new InstructScrollHelper(HelpBackActivity.class, mRvHelpHints);
    }
}