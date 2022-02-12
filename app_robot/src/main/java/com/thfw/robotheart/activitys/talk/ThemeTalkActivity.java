package com.thfw.robotheart.activitys.talk;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.ThemeTalkModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ThemeTalkAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

public class ThemeTalkActivity extends RobotBaseActivity<TalkPresenter> implements TalkPresenter.TalkUi<List<ThemeTalkModel>> {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.constraintlayout.widget.ConstraintLayout mClTheme;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private ThemeTalkAdapter talkAdapter;
    private com.thfw.ui.widget.LoadingView mLoadingView;

    @Override
    public int getContentView() {
        return R.layout.activity_theme_talk;
    }

    @Override
    public TalkPresenter onCreatePresenter() {
        return new TalkPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mClTheme = (ConstraintLayout) findViewById(R.id.cl_theme);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new GridLayoutManager(mContext, 3));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mPresenter.getDialogList();
        talkAdapter = new ThemeTalkAdapter(null);
        talkAdapter.setOnRvItemListener(new OnRvItemListener<ThemeTalkModel>() {
            @Override
            public void onItemClick(List<ThemeTalkModel> list, int position) {
                AiTalkActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_SPEECH_CRAFT)
                        .setId(list.get(position).getId())
                        .setCollected(list.get(position).getCollected() == 1)
                        .setTitle(list.get(position).getTitle()));
            }
        });
        mRvList.setAdapter(talkAdapter);
        mClTheme.setOnClickListener(v -> {
            AiTalkActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_THEME));
        });
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return ThemeTalkActivity.this;
    }

    @Override
    public void onSuccess(List<ThemeTalkModel> data) {
        if (EmptyUtil.isEmpty(data)) {
            mLoadingView.showEmpty();
        }
        mLoadingView.hide();
        talkAdapter.setDataListNotify(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            mPresenter.getDialogList();
        });
    }
}