package com.thfw.robotheart.activitys.me;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.thfw.base.models.HotCallModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.adapter.AzQuickAdapter;
import com.thfw.robotheart.adapter.HotPhoneAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.MyRobotSearchView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HotPhoneActivity extends RobotBaseActivity<OtherPresenter> implements OtherPresenter.OtherUi<List<HotCallModel>> {

    private static final String KEY_CACHE_HOT = "key.cache.hot";
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private com.thfw.ui.widget.MyRobotSearchView mRsvSearch;
    private RecyclerView mRvAcQuick;
    private List<String> mAzList;
    private android.widget.TextView mTvCenterAz;
    private List<HotCallModel> hotCallModels;
    private Handler mMainHandler = new Handler();
    private com.thfw.ui.widget.LoadingView mLoadingView;

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

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRsvSearch = (MyRobotSearchView) findViewById(R.id.rsv_search);
        mRvAcQuick = (RecyclerView) findViewById(R.id.rv_ac_quick);

        mRvAcQuick.setLayoutManager(new LinearLayoutManager(mContext));
        mTvCenterAz = (TextView) findViewById(R.id.tv_center_az);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {

        Type type = new TypeToken<List<HotCallModel>>() {
        }.getType();
        List<HotCallModel> cacheModel = SharePreferenceUtil.getObject(KEY_CACHE_HOT, type);
        if (!EmptyUtil.isEmpty(cacheModel)) {
            mLoadingView.hide();
            onSuccess(cacheModel);
        }
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
            if (mRvList == null || mRvList.getAdapter() == null
                    || mRvList.getAdapter().getItemCount() == 0) {
                mLoadingView.showEmpty();
            }
            return;
        }
        SharePreferenceUtil.setString(KEY_CACHE_HOT, GsonUtil.toJson(data));
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
        if (mRvList == null || mRvList.getAdapter() == null || mRvList.getAdapter().getItemCount() == 0) {
            mLoadingView.showFail(v -> {
                mPresenter.onGetHotPhoneList();
            });
        }
    }
}