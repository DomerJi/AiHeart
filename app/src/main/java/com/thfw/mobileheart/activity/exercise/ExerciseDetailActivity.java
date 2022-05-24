package com.thfw.mobileheart.activity.exercise;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.ExerciseModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.base.presenter.UserToolPresenter;
import com.thfw.base.utils.DataChangeHelper;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.mobileheart.fragment.exercise.ExerciseHintFragment;
import com.thfw.mobileheart.fragment.exercise.ExerciseLogcataFragment;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.MyScrollView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDetailActivity extends BaseActivity<UserToolPresenter> implements UserToolPresenter.UserToolUi<ExerciseModel> {

    public static final String KEY_ID = "key.id";
    private TitleView mTitleView;
    private ImageView mIvTopBanner;
    private TextView mTvTitle;
    private TextView mTvClassHour;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<Fragment> tabFragmentList = new ArrayList<>();
    private String[] tabs = new String[]{"简介", "目录"};
    private ExerciseModel mSimpleModel;
    private int mId;
    private ExerciseHintFragment mHintFragment;
    private ExerciseLogcataFragment mLogcataFragment;
    private android.widget.LinearLayout mLlCollect;
    private ImageView mIvCollect;
    private boolean requestIng;
    private com.thfw.ui.widget.MyScrollView mScrollView;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private ExerciseModel mExerciseModel;

    public static void startActivity(Context context, ExerciseModel exerciseModel) {
        context.startActivity(new Intent(context, ExerciseDetailActivity.class).putExtra(KEY_DATA, exerciseModel));
    }

    public static void startActivity(Context context, int id) {
        context.startActivity(new Intent(context, ExerciseDetailActivity.class).putExtra(KEY_ID, id));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_exercise_detail;
    }

    @Override
    public UserToolPresenter onCreatePresenter() {
        return new UserToolPresenter(this);
    }

    @Override
    public void initView() {
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mIvTopBanner = (ImageView) findViewById(R.id.iv_top_banner);
        mTvTitle = (TextView) findViewById(R.id.tv_lianxi_title);
        mTvClassHour = (TextView) findViewById(R.id.tv_class_hour);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mLlCollect = (LinearLayout) findViewById(R.id.ll_collect);
        mIvCollect = (ImageView) findViewById(R.id.iv_collect);
        mScrollView = (MyScrollView) findViewById(R.id.scrollView);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mTabLayout.addTab(mTabLayout.newTab().setText(tabs[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabs[1]));


        if (getIntent().hasExtra(KEY_ID)) {
            mId = getIntent().getIntExtra(KEY_ID, -1);
        }

        if (getIntent().hasExtra(KEY_DATA)) {
            mSimpleModel = (ExerciseModel) getIntent().getSerializableExtra(KEY_DATA);
            mId = mSimpleModel.getId();
        }

        if (mId == -1) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }

    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return ExerciseDetailActivity.this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onGetInfo(mId);
    }

    @Override
    public void onSuccess(ExerciseModel data) {
        mExerciseModel = data;
        GlideUtil.load(mContext, data.getPic(), mIvTopBanner);
        mTvTitle.setText(data.getTitle());
        mTvClassHour.setText(data.getTitle() + data.getLinkList().size() + "课时");
        mIvCollect.setSelected(data.getCollected() == 1);
        mLlCollect.setOnClickListener(v -> {
            addCollect();
        });
        mLoadingView.hide();
        mScrollView.setVisibility(View.VISIBLE);
        initAdapter(data);

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
                return ExerciseDetailActivity.this;
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
        }).addCollect(HistoryApi.TYPE_COLLECT_TOOL, mId);
    }


    private void initAdapter(ExerciseModel data) {
        if (tabFragmentList.isEmpty()) {
            mLogcataFragment = new ExerciseLogcataFragment();
            mHintFragment = new ExerciseHintFragment();
            mHintFragment.setExerciseModel(data);
            mLogcataFragment.setExerciseModel(data);
            tabFragmentList.add(mHintFragment);
            tabFragmentList.add(mLogcataFragment);

            mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                @NonNull
                @Override
                public Fragment getItem(int position) {
                    return tabFragmentList.get(position);
                }

                @Override
                public int getCount() {
                    return tabFragmentList.size();
                }

                @Nullable
                @Override
                public CharSequence getPageTitle(int position) {
                    return tabs[position];
                }
            });

            // 设置TabLayout和ViewPager联动
            mTabLayout.setupWithViewPager(mViewPager, false);
        } else {
            if (mHintFragment != null) {
                mHintFragment.setExerciseModel(data);
            }
            if (mLogcataFragment != null) {
                mLogcataFragment.setExerciseModel(data);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mExerciseModel != null) {
            DataChangeHelper.collectChange(mIvCollect, mExerciseModel.getId());
        }
        super.onDestroy();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mExerciseModel == null) {
            mLoadingView.showFail(v -> {
                mPresenter.onGetInfo(mId);
            });
        }
    }
}