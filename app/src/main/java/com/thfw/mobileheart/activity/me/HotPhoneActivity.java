package com.thfw.mobileheart.activity.me;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.HotCallModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.adapter.AzQuickAdapter;
import com.thfw.mobileheart.adapter.HotPhoneAdapter;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HotPhoneActivity extends BaseActivity<OtherPresenter> implements OtherPresenter.OtherUi<List<HotCallModel>> {

    private TitleView mTitleRobotView;
    private RecyclerView mRvList;
    private RecyclerView mRvAcQuick;
    private List<String> mAzList;
    private TextView mTvCenterAz;
    private List<HotCallModel> hotCallModels;

    private Handler mMainHandler = new Handler();
    private LoadingView mLoadingView;

    @Override
    public int getContentView() {
        return R.layout.activity_hot_phone;
    }

    @Override
    public OtherPresenter onCreatePresenter() {
        return new OtherPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleView) findViewById(R.id.titleRobotView);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRvAcQuick = (RecyclerView) findViewById(R.id.rv_ac_quick);

        mRvAcQuick.setLayoutManager(new LinearLayoutManager(mContext));
        mTvCenterAz = (TextView) findViewById(R.id.tv_center_az);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mPresenter.onGetHotPhoneList();
        mRvAcQuick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        hideCenter();
                        break;
                    default:
                        showCenterAz(event);
                        break;
                }
                return true;
            }
        });

    }

    private void showCenterAz(MotionEvent event) {

        View childView = mRvAcQuick.findChildViewUnder(event.getX(), event.getY());
        if (childView != null) {
            RecyclerView.ViewHolder viewHolder = mRvAcQuick.findContainingViewHolder(childView);
            if (viewHolder != null) {
                int position = viewHolder.getBindingAdapterPosition();
                mTvCenterAz.setText(mAzList.get(position));
                mTvCenterAz.setVisibility(View.VISIBLE);
                String mAz = mAzList.get(position);
                int size = hotCallModels.size();
                for (int i = 0; i < size; i++) {
                    if (mAz.equals(hotCallModels.get(i).getAzStr())) {
                        LogUtil.d(TAG, "Az = " + mAz + " ____ " + hotCallModels.get(i).getAzStr());
                        if (position != -1) {
                            mRvList.scrollToPosition(i);
                            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRvList.getLayoutManager();
                            mLayoutManager.scrollToPositionWithOffset(i, 0);
                        }
                        break;
                    }
                }
            } else {
                hideCenter();
            }
        } else {
            hideCenter();
        }
    }

    private void hideCenter() {
        mMainHandler.removeCallbacksAndMessages(null);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvCenterAz.setVisibility(View.GONE);
            }
        }, 350);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
        mMainHandler = null;
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<HotCallModel> data) {
        if (EmptyUtil.isEmpty(data)) {
            mLoadingView.showEmpty();
            return;
        }

        mLoadingView.hide();

        hotCallModels = new ArrayList<>();


        for (HotCallModel model : data) {
            model.initAz();
            hotCallModels.add(model);
        }

        Collections.sort(hotCallModels, new Comparator<HotCallModel>() {
            @Override
            public int compare(HotCallModel o1, HotCallModel o2) {
                if (o1.getAzCode() > o2.getAzCode()) {
                    return 1;
                } else if (o1.getAzCode() < o2.getAzCode()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        mAzList = new ArrayList<>();

        for (HotCallModel model : hotCallModels) {
            if (!mAzList.contains(model.getAzStr())) {
                mAzList.add(model.getAzStr());
            }
        }

        mRvAcQuick.setAdapter(new AzQuickAdapter(mAzList));
        mRvList.setAdapter(new HotPhoneAdapter(hotCallModels));
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mRvList == null || mRvList.getAdapter() == null) {
            mLoadingView.showFail(v -> {
                mPresenter.onGetHotPhoneList();
            });
        }
    }
}