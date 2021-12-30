package com.thfw.robotheart.activitys;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.SearchResultModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.SearchPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.SearchHistoryAdapter;
import com.thfw.robotheart.fragments.SearchResultFragment;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.MyRobotSearchView;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends RobotBaseActivity<SearchPresenter> implements SearchPresenter.SearchUi<SearchResultModel> {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private com.thfw.ui.widget.MyRobotSearchView mMySearch;
    private androidx.constraintlayout.widget.ConstraintLayout mClHistory;
    private android.widget.TextView mTvHistoryTitle;
    private androidx.recyclerview.widget.RecyclerView mRvHistory;
    private android.widget.LinearLayout mLlClearHistory;
    private androidx.constraintlayout.widget.ConstraintLayout mClResult;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private com.google.android.material.tabs.TabItem mTiVideo;
    private com.google.android.material.tabs.TabItem mTiExercise;
    private com.google.android.material.tabs.TabItem mTiBook;
    private com.google.android.material.tabs.TabItem mTiStudy;
    private ViewPager mViewPager;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private static String KEY_HISTORY = "search.history";
    private SearchHistoryAdapter searchHistoryAdapter;
    private List<String> mKeyHistoryList = new ArrayList<>();
    private MyResultAdapter myResultAdapter;

    private HashMap<Integer, SearchResultFragment> fragmentMaps = new HashMap<>();
    private List<Integer> fragments;

    @Override
    public int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    public SearchPresenter onCreatePresenter() {
        return new SearchPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mMySearch = (MyRobotSearchView) findViewById(R.id.my_search);
        mClHistory = (ConstraintLayout) findViewById(R.id.cl_history);
        mTvHistoryTitle = (TextView) findViewById(R.id.tv_history_title);
        mRvHistory = (RecyclerView) findViewById(R.id.rv_history);


        mLlClearHistory = (LinearLayout) findViewById(R.id.ll_clear_history);
        mLlClearHistory.setOnClickListener(v -> {
            LogUtil.d(TAG, "mLlClearHistory++++++++++++++++++++++++++++++++++++++++_________________");
            onDeleteHistory();
        });

        mClResult = (ConstraintLayout) findViewById(R.id.cl_result);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTiVideo = (TabItem) findViewById(R.id.ti_video);
        mTiExercise = (TabItem) findViewById(R.id.ti_exercise);
        mTiBook = (TabItem) findViewById(R.id.ti_book);
        mTiStudy = (TabItem) findViewById(R.id.ti_study);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLoadingView.hide();
        // 设置布局管理器
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
        // flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal。
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        // 主轴为水平方向，起点在左端。
        // flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列。
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        // 按正常方向换行
        // justifyContent 属性定义了项目在主轴上的对齐方式。
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);//交叉轴的起点对齐。
        mRvHistory.setLayoutManager(flexboxLayoutManager);
        searchHistoryAdapter = new SearchHistoryAdapter(mKeyHistoryList);
        searchHistoryAdapter.setOnRvItemListener(new OnRvItemListener<String>() {
            @Override
            public void onItemClick(List<String> list, int position) {
                mMySearch.getEditeText().setText(list.get(position));
                onGoSearch(list.get(position));
            }
        });
        searchHistoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mLlClearHistory.setVisibility(searchHistoryAdapter.getItemCount() > 0 ? View.VISIBLE : View.INVISIBLE);
                mLlClearHistory.setEnabled(searchHistoryAdapter.getItemCount() > 0 ? true : false);
            }
        });
        mRvHistory.setAdapter(searchHistoryAdapter);
        mMySearch.setOnSearchListener(new MyRobotSearchView.OnSearchListener() {
            @Override
            public void onSearch(String key, boolean clickSearch) {
                if (EmptyUtil.isEmpty(key)) {
                    mClResult.setVisibility(View.GONE);
                    mClHistory.setVisibility(View.VISIBLE);
                    mLoadingView.hide();
                }
                if (clickSearch) {
                    onGoSearch(key);
                }
            }

            @Override
            public void onClick(View view) {

            }
        });
    }

    private void onGoSearch(String key) {
        if (mKeyHistoryList.contains(key)) {
            mKeyHistoryList.remove(key);
        }
        mKeyHistoryList.add(0, key);
        SharePreferenceUtil.setString(KEY_HISTORY, GsonUtil.toJson(mKeyHistoryList));
        searchHistoryAdapter.notifyDataSetChanged();
        mClHistory.setVisibility(View.GONE);
        mClResult.setVisibility(View.GONE);
        mLoadingView.showLoading();
        mPresenter.onSearch(key);
        hideInput();
    }

    @Override
    public void initData() {
        KEY_HISTORY = "search.history." + UserManager.getInstance().getUID();

        Type type = new TypeToken<List<String>>() {
        }.getType();

        List<String> keyHistory = SharePreferenceUtil.getObject(KEY_HISTORY, type);
        if (!EmptyUtil.isEmpty(keyHistory)) {
            setHistoryList(keyHistory);
            mLoadingView.hide();
        } else {
            mLoadingView.showLoading();
        }

        new SearchPresenter(new SearchPresenter.SearchUi<List<String>>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return SearchActivity.this;
            }

            @Override
            public void onSuccess(List<String> list) {
                mLoadingView.hide();
                if (list == null) {
                    mRvHistory.removeAllViews();
                    return;
                }
                SharePreferenceUtil.setString(KEY_HISTORY, GsonUtil.toJson(list));
                setHistoryList(list);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                mLoadingView.hide();
            }
        }).getHistory();

    }

    private void setHistoryList(List<String> list) {
        mKeyHistoryList.clear();
        mKeyHistoryList.addAll(list);
        searchHistoryAdapter.setDataListNotify(mKeyHistoryList);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    private void onDeleteHistory() {
        LoadingDialog.show(this, "清除中...");
        new SearchPresenter(new SearchPresenter.SearchUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return SearchActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                LoadingDialog.hide();
                mKeyHistoryList.clear();
                searchHistoryAdapter.notifyDataSetChanged();
                ToastUtil.show("清除成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LoadingDialog.hide();
                ToastUtil.show("清除失败");
            }
        }).onDeleteHistory();
    }

    @Override
    public void onSuccess(SearchResultModel data) {
        initResultView(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            onGoSearch(mMySearch.getEditeText().getText().toString());
        });
    }


    /**
     * 加载搜索结果
     *
     * @param data
     */
    private void initResultView(SearchResultModel data) {

        if (data == null || EmptyUtil.isEmpty(data.getAllList())) {
            mLoadingView.showEmpty();
            return;
        }
        mTabLayout.removeAllTabs();
        mViewPager.removeAllViews();
        mLoadingView.hide();
        mClResult.setVisibility(View.VISIBLE);
        mClHistory.setVisibility(View.GONE);
        if (fragments == null) {
            fragments = new ArrayList<>();
        } else {
            fragments.clear();
        }

        if (!EmptyUtil.isEmpty(data.getCollection())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_AUDIO)) {
                fragmentMaps.get(SearchResultModel.TYPE_AUDIO).setResultBeans(data.getCollection());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_AUDIO,
                        new SearchResultFragment(SearchResultModel.TYPE_AUDIO, "正念冥想", data.getCollection()));

            }
            fragments.add(SearchResultModel.TYPE_AUDIO);
        }

        if (!EmptyUtil.isEmpty(data.getDialogList())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_DIALOG)) {
                fragmentMaps.get(SearchResultModel.TYPE_DIALOG).setResultBeans(data.getDialogList());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_DIALOG,
                        new SearchResultFragment(SearchResultModel.TYPE_DIALOG, "主题对话", data.getDialogList()));
            }
            fragments.add(SearchResultModel.TYPE_DIALOG);

        }

        if (!EmptyUtil.isEmpty(data.getPsychTest())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_TEST)) {
                fragmentMaps.get(SearchResultModel.TYPE_TEST).setResultBeans(data.getPsychTest());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_TEST,
                        new SearchResultFragment(SearchResultModel.TYPE_TEST, "心理测评", data.getPsychTest()));
            }
            fragments.add(SearchResultModel.TYPE_TEST);

        }

        if (!EmptyUtil.isEmpty(data.getVideoList())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_VIDEO)) {
                fragmentMaps.get(SearchResultModel.TYPE_VIDEO).setResultBeans(data.getVideoList());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_VIDEO,
                        new SearchResultFragment(SearchResultModel.TYPE_VIDEO, "科普视频", data.getVideoList()));
            }
            fragments.add(SearchResultModel.TYPE_VIDEO);

        }

        if (!EmptyUtil.isEmpty(data.getArticleList())) {
            if (fragmentMaps.containsKey(SearchResultModel.TYPE_TEXT)) {
                fragmentMaps.get(SearchResultModel.TYPE_TEXT).setResultBeans(data.getVideoList());
            } else {
                fragmentMaps.put(SearchResultModel.TYPE_TEXT,
                        new SearchResultFragment(SearchResultModel.TYPE_TEXT, "科普文章", data.getVideoList()));
            }
            fragments.add(SearchResultModel.TYPE_TEXT);

        }

        /**
         * setMaxLifecycle fragment 可见监听
         */
        if (myResultAdapter == null) {
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    getSupportFragmentManager().beginTransaction()
                            .setMaxLifecycle(fragmentMaps.get(fragments.get(position)), Lifecycle.State.RESUMED).commit();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        myResultAdapter = new MyResultAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(myResultAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        /**
         * tabView 左右边距动态设置
         */
        int count = mTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            TabLayout.TabView tabView = mTabLayout.getTabAt(i).view;
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) tabView.getLayoutParams();
            p.leftMargin = 50;
            p.rightMargin = 50;
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