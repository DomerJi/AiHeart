package com.thfw.robotheart.fragments;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.models.SearchResultModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/3/16 17:16
 * Describe:Todo
 */
public class SearchFragment extends RobotBaseFragment {
    SearchResultModel data;
    private androidx.constraintlayout.widget.ConstraintLayout mClResult;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;
    private MyResultAdapter myResultAdapter;
    private HashMap<Integer, SearchResultFragment> fragmentMaps = new HashMap<>();
    private List<Integer> fragments;

    public SearchFragment(SearchResultModel data) {
        super();
        this.data = data;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_search;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mClResult = (ConstraintLayout) findViewById(R.id.cl_result);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    @Override
    public void initData() {
        initResultView(data);
    }


    /**
     * 加载搜索结果
     *
     * @param data
     */
    private void initResultView(SearchResultModel data) {

        if (fragments == null) {
            fragments = new ArrayList<>();
        } else {
            fragments.clear();
        }
        // 正念冥想
        if (!EmptyUtil.isEmpty(data.getCollection())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_AUDIO)) {
                fragmentMaps.get(SearchResultModel.TYPE_AUDIO).setResultBeans(data.getCollection());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_AUDIO,
                        new SearchResultFragment(SearchResultModel.TYPE_AUDIO, "正念冥想", data.getCollection()));

            }
            fragments.add(SearchResultModel.TYPE_AUDIO);
        }
        // 主题对话
        if (!EmptyUtil.isEmpty(data.getDialogList())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_DIALOG)) {
                fragmentMaps.get(SearchResultModel.TYPE_DIALOG).setResultBeans(data.getDialogList());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_DIALOG,
                        new SearchResultFragment(SearchResultModel.TYPE_DIALOG, "主题对话", data.getDialogList()));
            }
            fragments.add(SearchResultModel.TYPE_DIALOG);

        }
        // 测评问卷
        if (!EmptyUtil.isEmpty(data.getPsychTest())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_TEST)) {
                fragmentMaps.get(SearchResultModel.TYPE_TEST).setResultBeans(data.getPsychTest());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_TEST,
                        new SearchResultFragment(SearchResultModel.TYPE_TEST, "测评问卷", data.getPsychTest()));
            }
            fragments.add(SearchResultModel.TYPE_TEST);

        }
        // 视频集锦
        if (!EmptyUtil.isEmpty(data.getVideoList())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_VIDEO)) {
                fragmentMaps.get(SearchResultModel.TYPE_VIDEO).setResultBeans(data.getVideoList());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_VIDEO,
                        new SearchResultFragment(SearchResultModel.TYPE_VIDEO, "视频集锦", data.getVideoList()));
            }
            fragments.add(SearchResultModel.TYPE_VIDEO);

        }
        // 思政文章
        if (!EmptyUtil.isEmpty(data.getIdeologyList())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_IDEO_TEXT)) {
                fragmentMaps.get(SearchResultModel.TYPE_IDEO_TEXT).setResultBeans(data.getIdeologyList());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_IDEO_TEXT,
                        new SearchResultFragment(SearchResultModel.TYPE_IDEO_TEXT, "思政文库", data.getIdeologyList()));
            }
            fragments.add(SearchResultModel.TYPE_IDEO_TEXT);

        }
        // 心理文章
        if (!EmptyUtil.isEmpty(data.getArticleList())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_TEXT)) {
                fragmentMaps.get(SearchResultModel.TYPE_TEXT).setResultBeans(data.getArticleList());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_TEXT,
                        new SearchResultFragment(SearchResultModel.TYPE_TEXT, "心理文库", data.getArticleList()));
            }
            fragments.add(SearchResultModel.TYPE_TEXT);

        }

        // 成长训练
        if (!EmptyUtil.isEmpty(data.getToolPackageList())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_TOOL)) {
                fragmentMaps.get(SearchResultModel.TYPE_TOOL).setResultBeans(data.getToolPackageList());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_TOOL,
                        new SearchResultFragment(SearchResultModel.TYPE_TOOL, "成长训练", data.getToolPackageList()));
            }
            fragments.add(SearchResultModel.TYPE_TOOL);

        }


        /**
         * setMaxLifecycle fragment 可见监听
         * fragment 刷新
         */
        if (myResultAdapter == null) {
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    getChildFragmentManager().beginTransaction()
                            .setMaxLifecycle(fragmentMaps.get(fragments.get(position)), Lifecycle.State.RESUMED).commit();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        myResultAdapter = new MyResultAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(myResultAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        /**
         * tabView 左右边距动态设置
         */
        int count = mTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            TabLayout.TabView tabView = mTabLayout.getTabAt(i).view;
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) tabView.getLayoutParams();
            if (i != 0) {
                p.leftMargin = 25;
            }
            if (i != count - 1) {
                p.rightMargin = 25;
            }
            tabView.setLayoutParams(p);
        }

        /**
         * 添加tablayout监听
         */
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(mTabLayout.getSelectedTabPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 切换到下一个页面
     */
    public void onViewPagerNext() {
        if (mViewPager != null && myResultAdapter != null && myResultAdapter.getCount() > 1) {
            mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1) % myResultAdapter.getCount(), false);
        }
    }

    /**
     * 搜索结果FragmentPagerAdapter
     */
    public class MyResultAdapter extends FragmentPagerAdapter {

        private List<Integer> fragments;

        public MyResultAdapter(@NonNull @NotNull FragmentManager fm, List<Integer> fragments) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = fragments;
        }

        public void setFragments(List<Integer> fragments) {
            this.fragments = fragments;
        }

        @NonNull
        @NotNull
        @Override
        public Fragment getItem(int position) {
            return fragmentMaps.get(fragments.get(position));
        }

        @Override
        public long getItemId(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return ((SearchResultFragment) fragmentMaps.get(fragments.get(position))).getTitle();
        }
    }
}
