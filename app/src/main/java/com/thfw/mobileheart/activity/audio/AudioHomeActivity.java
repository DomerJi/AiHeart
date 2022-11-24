package com.thfw.mobileheart.activity.audio;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.common.reflect.TypeToken;
import com.thfw.base.models.AudioLastEtcModel;
import com.thfw.base.models.AudioTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.AudioPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.fragment.list.AudioListFragment;
import com.thfw.mobileheart.fragment.list.VideoListFragment;
import com.thfw.mobileheart.view.LastTextView;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AudioHomeActivity extends BaseActivity<AudioPresenter> implements AudioPresenter.AudioUi<List<AudioTypeModel>> {

    private static final String KEY_TYPE_LIST = "audio.type.list";
    private com.thfw.ui.widget.TitleView mTitleView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;

    private List<Fragment> tabFragmentList = new ArrayList<>();
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private com.thfw.mobileheart.view.LastTextView mTvLastAudio;
    private List<AudioTypeModel> models;
    private ArrayList<Object> textList;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, AudioHomeActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_audio_home;
    }

    @Override
    public AudioPresenter onCreatePresenter() {
        return new AudioPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mTvLastAudio = (LastTextView) findViewById(R.id.tv_last_audio);
    }


    @Override
    public void initData() {
        Type type = new TypeToken<List<AudioTypeModel>>() {
        }.getType();
        List<AudioTypeModel> cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, type);
        if (!EmptyUtil.isEmpty(cacheModel)) {
            mLoadingView.hide();
            setViewPager(cacheModel);
        }
        mPresenter.getAudioType();

    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<AudioTypeModel> data) {
        mLoadingView.hide();
        if (data != null) {
            AudioTypeModel.sort(data);
            data.add(0, new AudioTypeModel("全部", 0));
            SharePreferenceUtil.setString(KEY_TYPE_LIST, GsonUtil.toJson(data));
            if (mViewPager.getAdapter() == null) {
                setViewPager(data);
            }
        }
    }

    private void setViewPagerOld(List<AudioTypeModel> audioTypeModels) {
        int size = audioTypeModels.size();
        mViewPager.setOffscreenPageLimit(size);
        // 添加tab
        for (int i = 0; i < size; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(audioTypeModels.get(i).getName()));
            tabFragmentList.add(new AudioListFragment(audioTypeModels.get(i).getKey()));
        }

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
                return audioTypeModels.get(position).getName();
            }
        });

        // 设置TabLayout和ViewPager联动
        mTabLayout.setupWithViewPager(mViewPager, false);
    }


    private void setViewPager(List<AudioTypeModel> cacheModel) {
        models = cacheModel;
        int size = cacheModel.size();
        Log.i(TAG, "size = " + size);
        mViewPager.setOffscreenPageLimit(size);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("onTabSelected", "position -> " + tab.getPosition());
                refreshTabColor(tab.getPosition());
                if (mViewPager.getCurrentItem() != tab.getPosition()) {
                    mViewPager.setCurrentItem(tab.getPosition(), false);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                try {
                    ((VideoListFragment) tabFragmentList.get(tab.getPosition())).onReSelected();
                } catch (Exception e) {

                }
            }
        });

        /**
         * TypeEvaluator简介
         * Android提供了以下几个简单的Evalutor实现类：
         * IntEvaluator：属性的值类型为int
         * FloatEvaluator：属性的值类型为float
         * ArgbEvaluator：属性的值类型为十六进制颜色值
         */
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            ArgbEvaluator argbEvaluator = new ArgbEvaluator();

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (positionOffset <= 0 || positionOffsetPixels <= 0) {
                    return;
                }

                Object startColor = cacheModel.get(position).getSelectedColor();
                Object endColor = cacheModel.get(position + 1).getSelectedColor();
                int color = (int) argbEvaluator.evaluate(positionOffset, startColor, endColor);
                mTabLayout.setSelectedTabIndicatorColor(color);
                Log.i("onPageScrolled", "position -> " + position
                        + " ; positionOffset -> " + positionOffset
                        + " ; positionOffsetPixels -> " + positionOffsetPixels
                        + " ; color -> " + color + " ; toPosition -> " + (position + 1));
                mTabLayout.setScrollPosition(position, positionOffset, true);
            }

            @Override
            public void onPageSelected(int position) {
                getSupportFragmentManager().beginTransaction()
                        .setMaxLifecycle(tabFragmentList.get(position), Lifecycle.State.RESUMED).commit();
                if (mTabLayout.getSelectedTabPosition() != position) {
                    mTabLayout.selectTab(mTabLayout.getTabAt(position));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
        //添加tab
        for (int i = 0; i < size; i++) {
            tabFragmentList.add(new AudioListFragment(cacheModel.get(i).getKey()));
        }

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return tabFragmentList.get(position);
            }

            @NonNull
            @NotNull
            @Override
            public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
                return super.instantiateItem(container, position);
            }

            @Override
            public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
                super.destroyItem(container, position, object);
            }

            @Override
            public int getCount() {
                return tabFragmentList.size();
            }

            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return cacheModel.get(position).getName();
            }
        });

        //添加tab
        for (int i = 0; i < size; i++) {
            // todo 自定义布局
            TabLayout.Tab tabAt = mTabLayout.newTab().setText(cacheModel.get(i).getName());
            mTabLayout.addTab(tabAt);
            tabAt.setCustomView(R.layout.tab_custeom_item_study);
            textList = new ArrayList<>();
            if (tabAt.getCustomView() != null) {
                if (cacheModel.get(i).isUnSelectedChange()) {
                    TextView mText1 = tabAt.getCustomView().findViewById(android.R.id.text1);
                    textList.add(mText1);
                    mText1.setTextColor(models.get(i).getUnSelectedColor());
                }
                ImageView mIvFire = tabAt.getCustomView().findViewById(R.id.iv_fire);
                if (cacheModel.get(i).fire == 0) {
                    mIvFire.setVisibility(View.GONE);
                } else {
                    mIvFire.setVisibility(View.VISIBLE);
                    mIvFire.setImageLevel(cacheModel.get(i).fire);
                }
            }

        }

        // 设置TabLayout和ViewPager联动
//        mTabLayout.setupWithViewPager(mViewPager, false);
    }

    private void refreshTabColor(int position) {
        int count = mTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            AudioTypeModel model = models.get(i);
            TabLayout.Tab tabAt = mTabLayout.getTabAt(i);


            if (tabAt != null && tabAt.getCustomView() != null) {
                TextView mText1 = tabAt.getCustomView().findViewById(android.R.id.text1);
                ImageView mIvFire = tabAt.getCustomView().findViewById(R.id.iv_fire);
                if (model.fire == 0) {
                    mIvFire.setVisibility(View.GONE);
                } else {
                    mIvFire.setVisibility(View.VISIBLE);
                    mIvFire.setImageLevel(model.fire);
                }
                if (position == i) {
                    mText1.setTextColor(model.getSelectedColor());
                    // mText1.setTypeface 加粗最可靠！！！
                    mText1.setTypeface(model.getSelectedColor() == model.getUnSelectedColor() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
                    mTabLayout.setSelectedTabIndicatorColor(model.getSelectedColor());
                } else {
                    mText1.setTextColor(model.getUnSelectedColor());
                    mText1.setTypeface(Typeface.DEFAULT);
                }
            }
        }
    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mViewPager.getAdapter() == null) {
            mLoadingView.showFail(v -> {
                mPresenter.getAudioType();
            });
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    /**
     * 获得最后一次播放记录
     */
    private void getLastAudio() {
        new AudioPresenter(new AudioPresenter.AudioUi<AudioLastEtcModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return AudioHomeActivity.this;
            }

            @Override
            public void onSuccess(AudioLastEtcModel data) {
                notifyLastAudioData(data);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {

            }
        }).getAudioLastHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLastAudio();
    }

    private void notifyLastAudioData(AudioLastEtcModel data) {
        if (data != null) {
            mTvLastAudio.setVisibility(View.VISIBLE);
            mTvLastAudio.setText("上次播放：" + data.getTitle() + "     " + data.getAddTime());
            mTvLastAudio.setOnClickListener(v -> {
                AudioPlayerActivity.startActivity(mContext, data.toAudioEtcModel());
            });
        }
    }


    public void setRecyclerView(RecyclerView recyclerView) {
        if (mTvLastAudio != null) {
            mTvLastAudio.onAttached(recyclerView);
        }
    }


}