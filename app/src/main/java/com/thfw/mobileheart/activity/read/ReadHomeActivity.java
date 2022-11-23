package com.thfw.mobileheart.activity.read;

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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;
import com.thfw.base.models.BookTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.BookPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.fragment.list.ReadListFragment;
import com.thfw.mobileheart.view.LastTextView;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReadHomeActivity extends BaseActivity<BookPresenter> implements BookPresenter.BookUi<BookTypeModel> {

    private static final String KEY_TYPE_LIST = "book.type.list";
    private com.thfw.ui.widget.TitleView mTitleView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;

    private List<Fragment> tabFragmentList = new ArrayList<>();
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private com.thfw.mobileheart.view.LastTextView mTvLastAudio;
    private ArrayList textList;

    private List<BookTypeModel.BookTypeImpModel> models;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ReadHomeActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_read_home;
    }

    @Override
    public BookPresenter onCreatePresenter() {
        return new BookPresenter(this);
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
        Type type = new TypeToken<BookTypeModel>() {
        }.getType();
        BookTypeModel cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, type);
        if (cacheModel != null && !EmptyUtil.isEmpty(cacheModel.getList())) {
            mLoadingView.hide();
            setViewPager(cacheModel.getList());
        }
        mPresenter.getArticleType();
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(BookTypeModel data) {
        mLoadingView.hide();


        mLoadingView.hide();
        if (data != null) {
            SharePreferenceUtil.setString(KEY_TYPE_LIST, GsonUtil.toJson(data));
            if (mViewPager.getAdapter() == null) {
                setViewPager(data.getList());
            }
        }
    }


    private void setViewPagerOld(List<BookTypeModel.BookTypeImpModel> bookTypeImpModels) {

        int size = bookTypeImpModels.size();
        mViewPager.setOffscreenPageLimit(size);
        // 添加tab
        for (int i = 0; i < size; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(bookTypeImpModels.get(i).value));
            tabFragmentList.add(new ReadListFragment(bookTypeImpModels.get(i).id));
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
                return bookTypeImpModels.get(position).value;
            }
        });

        // 设置TabLayout和ViewPager联动
        mTabLayout.setupWithViewPager(mViewPager, false);
    }


    private void setViewPager(List<BookTypeModel.BookTypeImpModel> cacheModel) {
        this.models = cacheModel;
        int size = cacheModel.size();
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
                    ((ReadListFragment) tabFragmentList.get(tab.getPosition())).onReSelected();
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

            tabFragmentList.add(new ReadListFragment(cacheModel.get(i).id));
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
                return cacheModel.get(position).value;
            }
        });

        //添加tab
        for (int i = 0; i < size; i++) {

            // todo 自定义布局
            TabLayout.Tab tabAt = mTabLayout.newTab().setText(cacheModel.get(i).value);
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
                mIvFire.setVisibility(cacheModel.get(i).fire == 1 ? View.VISIBLE : View.GONE);


            }

        }

        // 设置TabLayout和ViewPager联动
//        mTabLayout.setupWithViewPager(mViewPager, false);
    }

    private void refreshTabColor(int position) {
        int count = mTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            BookTypeModel.BookTypeImpModel model = models.get(i);
            TabLayout.Tab tabAt = mTabLayout.getTabAt(i);


            if (tabAt != null && tabAt.getCustomView() != null) {
                TextView mText1 = tabAt.getCustomView().findViewById(android.R.id.text1);
                ImageView mIvFire = tabAt.getCustomView().findViewById(R.id.iv_fire);
                mIvFire.setVisibility(model.fire == 1 ? View.VISIBLE : View.GONE);
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
                mPresenter.getArticleType();
            });
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}