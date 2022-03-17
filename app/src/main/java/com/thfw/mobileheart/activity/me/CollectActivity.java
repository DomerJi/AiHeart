package com.thfw.mobileheart.activity.me;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.fragment.list.CollectListFragment;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

public class CollectActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;

    private List<Fragment> tabFragmentList = new ArrayList<>();
    private String[] tabs = new String[]{"测一测", "看一看", "听一听", "读一读", "练一练", "学一学"};

    // 1-测评  2-文章 3-音频 4-视频 5-话术 6-思政文章 7-工具包
    private int[] types = new int[]{HistoryApi.TYPE_TEST, HistoryApi.TYPE_VIDEO,
            HistoryApi.TYPE_AUDIO, HistoryApi.TYPE_BOOK,
            HistoryApi.TYPE_EXERCISE, HistoryApi.TYPE_STUDY};

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CollectActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_collect;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }


    @Override
    public void initData() {
        //添加tab
        for (int i = 0; i < tabs.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabs[i]));
            tabFragmentList.add(new CollectListFragment(types[i]));
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
                return tabs[position];
            }
        });

        //设置TabLayout和ViewPager联动
        mTabLayout.setupWithViewPager(mViewPager, false);
    }
}