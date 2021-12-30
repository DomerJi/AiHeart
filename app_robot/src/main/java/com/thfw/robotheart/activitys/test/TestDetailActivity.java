package com.thfw.robotheart.activitys.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.api.HistoryApi;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.TestDetailModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.base.presenter.TestPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.me.HistoryActivity;
import com.thfw.robotheart.adapter.TestHintAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

public class TestDetailActivity extends RobotBaseActivity<TestPresenter> implements TestPresenter.TestUi<TestDetailModel> {

    private int mTestId;
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.LinearLayout mLlContent;
    private android.widget.ImageView mIvBg;
    private android.widget.TextView mTvTitle;
    private android.widget.TextView mTvHint;
    private android.widget.TextView mTvSeePort;
    private android.widget.TextView mTvCollect;
    private android.widget.Button mBtBeginTest;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private TestDetailModel mModel;
    private TextView mTvTestTitle;
    private RecyclerView mRvHint;
    private LinearLayout mLlSeePort;
    private LinearLayout mLlCollect;
    private ImageView mIvCollect;

    private boolean requestIng = false;

    public static void startActivity(Context context, int id) {
        ((Activity) context).startActivityForResult(new Intent(context, TestDetailActivity.class)
                .putExtra(KEY_DATA, id), ChatEntity.TYPE_RECOMMEND_TEXT);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_test_detail;
    }

    @Override
    public TestPresenter onCreatePresenter() {
        return new TestPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);
        mIvBg = (ImageView) findViewById(R.id.iv_bg);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mTvSeePort = (TextView) findViewById(R.id.tv_see_port);
        mTvCollect = (TextView) findViewById(R.id.tv_collect);
        mBtBeginTest = (Button) findViewById(R.id.bt_begin_test);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mTvTestTitle = (TextView) findViewById(R.id.tv_test_title);

        mRvHint = findViewById(R.id.rv_hint);
        mRvHint.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mLlSeePort = (LinearLayout) findViewById(R.id.ll_see_port);
        mLlCollect = (LinearLayout) findViewById(R.id.ll_collect);
        mIvCollect = (ImageView) findViewById(R.id.iv_collect);
    }

    @Override
    public void initData() {
        mTestId = getIntent().getIntExtra(KEY_DATA, -1);
        if (mTestId == -1) {
            ToastUtil.show("参数错误");
            return;
        }
        mPresenter.onGetInfo(mTestId);

    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(TestDetailModel data) {
        mModel = data;
        if (mModel == null) {
            mLoadingView.showEmpty();
            return;
        }
        mLlContent.setVisibility(View.VISIBLE);
        mLoadingView.hide();
        TestDetailModel.PsychtestInfoBean mInfoBean = mModel.getPsychtestInfo();
        if (mInfoBean == null) {
            return;
        }

        GlideUtil.load(mContext, mInfoBean.getPic(), mIvBg);
        mTvHint.setText("简介: " + mInfoBean.getIntr());
        mTvTestTitle.setText(mInfoBean.getTitle());
        mLlSeePort.setOnClickListener(v -> {
            HistoryActivity.startActivity(mContext, HistoryApi.TYPE_TEST, mInfoBean.getId());
        });

        mBtBeginTest.setOnClickListener(v -> {
            TestIngActivity.startActivity(mContext, mModel);
            finish();
        });

        mIvCollect.setSelected(mInfoBean.isCollected());
        mLlCollect.setOnClickListener(v -> {
            addCollect();
        });


        mRvHint.setAdapter(new TestHintAdapter(mModel.getPsychtestInfo().getHintBeans()));

    }

    public void addCollect() {
        if (requestIng) {
            return;
        }
        requestIng = true;
        mIvCollect.setSelected(!mIvCollect.isSelected());
        new HistoryPresenter(new HistoryPresenter.HistoryUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return TestDetailActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? "收藏成功" : "取消收藏成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? "收藏失败" : "取消收藏失败");
                mIvCollect.setSelected(!mIvCollect.isSelected());
            }
        }).addCollect(HistoryApi.TYPE_COLLECT_TEST, mTestId);
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            mPresenter.onGetInfo(mTestId);
        });
    }
}