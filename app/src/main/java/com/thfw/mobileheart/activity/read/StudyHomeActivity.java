package com.thfw.mobileheart.activity.read;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.thfw.base.models.BookStudyTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.BookPresenter;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.fragment.list.StudyListFragment;
import com.thfw.mobileheart.view.LastTextView;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StudyHomeActivity extends BaseActivity<BookPresenter> implements BookPresenter.BookUi<List<BookStudyTypeModel>> {

    private static final String KEY_TYPE_LIST = "key_booy_study_type_list";
    private static final String KEY_HAS_VIDEO = "key.video.has";
    private List<Fragment> tabFragmentList = new ArrayList<>();
    private com.thfw.ui.widget.TitleView mTitleView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private com.thfw.mobileheart.view.LastTextView mTvLastAudio;
    private List<BookStudyTypeModel> models;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, StudyHomeActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_study_home;
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


//        Type type = new TypeToken<List<BookStudyTypeModel>>() {
//        }.getType();
//        List<BookStudyTypeModel> cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, type);
//        if (!EmptyUtil.isEmpty(cacheModel)) {
//            mLoadingView.hide();
//            setAdapter(cacheModel);
//        }
        mPresenter.getIdeologyArticleType();


    }

    private void setAdapter(List<BookStudyTypeModel> cacheModel) {
        this.models = cacheModel;
        int size = cacheModel.size();
        mViewPager.setOffscreenPageLimit(size);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                refreshTabColor();
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    TextView textView = tab.getCustomView().findViewById(android.R.id.text1);
                    TextPaint paint = textView.getPaint();
                    paint.setFakeBoldText(false);
                    textView.setTextColor(cacheModel.get(tab.getPosition()).getUnSelectedColor());
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                try {
                    ((StudyListFragment) tabFragmentList.get(tab.getPosition())).onReSelected();
                } catch (Exception e) {

                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTabLayout.setScrollPosition(position, positionOffset, true);
            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.selectTab(mTabLayout.getTabAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //添加tab
        for (int i = 0; i < size; i++) {
            int contentType = cacheModel.get(i).id;
            StudyListFragment studyListFragment = new StudyListFragment(contentType);
            Bundle bundle = new Bundle();
            bundle.putSerializable(StudyListFragment.KEY_CHILD_TYPE, (ArrayList) cacheModel.get(i).getList());
            studyListFragment.setArguments(bundle);
            tabFragmentList.add(studyListFragment);

        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportFragmentManager().beginTransaction()
                        .setMaxLifecycle(tabFragmentList.get(position), Lifecycle.State.RESUMED).commit();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                return cacheModel.get(position).name;
            }
        });

        //添加tab
        for (int i = 0; i < size; i++) {

            // todo 自定义布局
            TabLayout.Tab tabAt = mTabLayout.newTab().setText(cacheModel.get(i).name);
            mTabLayout.addTab(tabAt);
            tabAt.setCustomView(R.layout.tab_custeom_item_study);
            if (cacheModel.get(i).isUnSelectedChange()) {
                if (tabAt.getCustomView() != null) {
                    TextView mText1 = tabAt.getCustomView().findViewById(android.R.id.text1);
                    mText1.setTextColor(models.get(i).getUnSelectedColor());
                }

            }

        }

        // 设置TabLayout和ViewPager联动
//        mTabLayout.setupWithViewPager(mViewPager, false);
    }

    private void refreshTabColor() {
        int count = mTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            BookStudyTypeModel model = models.get(i);
            TabLayout.Tab tabAt = mTabLayout.getTabAt(i);
            if (tabAt != null && tabAt.getCustomView() != null) {
                if (mTabLayout.getSelectedTabPosition() == i) {

                    TextView mText1 = tabAt.getCustomView().findViewById(android.R.id.text1);
                    mText1.setTextColor(model.getSelectedColor());
                    mTabLayout.setSelectedTabIndicatorColor(model.getSelectedColor());
                    TextPaint paint = mText1.getPaint();
                    paint.setFakeBoldText(model.getSelectedColor() == model.getUnSelectedColor());
                } else {
                    TextView mText1 = tabAt.getCustomView().findViewById(android.R.id.text1);
                    mText1.setTextColor(model.getUnSelectedColor());
                    TextPaint paint = mText1.getPaint();
                    paint.setFakeBoldText(false);

                }
            }
        }
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<BookStudyTypeModel> data) {
        BookStudyTypeModel.sort(data);
        if (data != null) {
            data.add(0, new BookStudyTypeModel("全部", 0));
        }
        SharePreferenceUtil.setString(KEY_TYPE_LIST, GsonUtil.toJson(data));
        mLoadingView.hide();
        if (mViewPager.getAdapter() == null) {
            setAdapter(data);
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mViewPager.getAdapter() == null) {
            mLoadingView.showFail(v -> {
                mPresenter.getIdeologyArticleType();
            });
        }
    }
}