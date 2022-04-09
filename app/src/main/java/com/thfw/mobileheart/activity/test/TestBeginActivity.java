package com.thfw.mobileheart.activity.test;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.api.HistoryApi;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.MTestDetailAdapterModel;
import com.thfw.base.models.TestDetailModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.base.presenter.TestPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.adapter.TestDetailAdapter;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

public class TestBeginActivity extends BaseActivity<TestPresenter> implements TestPresenter.TestUi<TestDetailModel> {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.Button mBtnBegin;
    private androidx.recyclerview.widget.RecyclerView mRvTest;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private androidx.constraintlayout.widget.ConstraintLayout mClBottom;
    private android.widget.LinearLayout mLlCollect;
    private android.widget.ImageView mIvCollect;
    private TestDetailModel mDetailModel;
    private boolean requestIng;

    public static void startActivity(Context context, int id) {
        context.startActivity(new Intent(context, TestBeginActivity.class).putExtra(KEY_DATA, id));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_test_begin;
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
                ToastUtil.show(mIvCollect.isSelected() ? UIConfig.COLLECTED : UIConfig.COLLECTED_UN);
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

    }


}