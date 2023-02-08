package com.thfw.export_ym.test;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.adapter.TestDetailAdapter;
import com.thfw.api.HistoryApi;
import com.thfw.base.YmBaseActivity;
import com.thfw.export_ym.R;
import com.thfw.models.CommonModel;
import com.thfw.models.HttpResult;
import com.thfw.models.MTestDetailAdapterModel;
import com.thfw.models.TestDetailModel;
import com.thfw.net.ResponeThrowable;
import com.thfw.presenter.HistoryPresenter;
import com.thfw.presenter.TestPresenter;
import com.thfw.util.ToastUtil;
import com.thfw.view.LoadingView;
import com.thfw.view.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

public class TestBeginActivity extends YmBaseActivity<TestPresenter> implements TestPresenter.TestUi<TestDetailModel> {

    private TitleView mTitleView;
    private Button mBtnBegin;
    private RecyclerView mRvTest;
    private LoadingView mLoadingView;
    private ConstraintLayout mClBottom;
    private LinearLayout mLlCollect;
    private ImageView mIvCollect;
    private TestDetailModel mDetailModel;
    private boolean requestIng;

    public static void startActivity(Context context, int id) {
        context.startActivity(new Intent(context, TestBeginActivity.class).putExtra(KEY_DATA, id));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_test_begin_ym;
    }

    @Override
    public TestPresenter onCreatePresenter() {
        return new TestPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mBtnBegin = (Button) findViewById(R.id.btn_begin);
        mRvTest = (RecyclerView) findViewById(R.id.rv_test);
        mRvTest.setLayoutManager(new LinearLayoutManager(mContext));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mClBottom = (ConstraintLayout) findViewById(R.id.cl_bottom);
        mLlCollect = (LinearLayout) findViewById(R.id.ll_collect);
        mIvCollect = (ImageView) findViewById(R.id.iv_collect);
    }

    @Override
    public void initData() {

        int mTestId = getIntent().getIntExtra(KEY_DATA, 0);
        if (mTestId <= 0) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }
        mPresenter.onGetInfo(mTestId);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    public void addCollect() {
        if (requestIng || mDetailModel == null) {
            return;
        }
        requestIng = true;
        mIvCollect.setSelected(!mIvCollect.isSelected());
        new HistoryPresenter(new HistoryPresenter.HistoryUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return TestBeginActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? "已收藏" : "已取消收藏");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? "收藏失败" : "取消收藏失败");
                mIvCollect.setSelected(!mIvCollect.isSelected());
            }
        }).addCollect(HistoryApi.TYPE_COLLECT_AUDIO, mDetailModel.getPsychtestInfo().getId());
    }


    @Override
    public void onSuccess(TestDetailModel data) {
        if (data == null) {
            mLoadingView.showEmpty();
            return;
        }
        mLoadingView.hide();
        mDetailModel = data;
        mBtnBegin.setOnClickListener(v -> {
            TestProgressIngActivity.startActivity(mContext, mDetailModel);
        });
        mIvCollect.setOnClickListener(v -> {
            addCollect();
        });
        List<MTestDetailAdapterModel> list = new ArrayList<>();
        list.add(new MTestDetailAdapterModel().setDetailModel(data).setType(MTestDetailAdapterModel.TYPE_TOP));

        if (data.getPsychtestInfo().isHas1Body()) {
            list.add(new MTestDetailAdapterModel().setHintBean(data.getPsychtestInfo().getBody1()).setType(MTestDetailAdapterModel.TYPE_BODY));
        }

        if (data.getPsychtestInfo().isHas2Body()) {
            list.add(new MTestDetailAdapterModel().setHintBean(data.getPsychtestInfo().getBody2()).setType(MTestDetailAdapterModel.TYPE_BODY));
        }

        if (data.getPsychtestInfo().isHas3Body()) {
            list.add(new MTestDetailAdapterModel().setHintBean(data.getPsychtestInfo().getBody3()).setType(MTestDetailAdapterModel.TYPE_HINT));
        }
        mRvTest.setAdapter(new TestDetailAdapter(list));

    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (!HttpResult.noTokenFailShowMsg(throwable.code)) {
            mLoadingView.showFail(v -> {
                initData();
            });
        }
    }


}