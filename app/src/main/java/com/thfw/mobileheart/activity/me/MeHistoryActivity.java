package com.thfw.mobileheart.activity.me;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.fragment.list.HistoryListFragment;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

public class MeHistoryActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;

    private List<Fragment> tabFragmentList = new ArrayList<>();
    private String[] tabs = new String[]{"我练的", "我听的", "我看的", "我读的", "我学的"};

    private int mCurrentItem = 0;

    public static void startActivity(Context context, int currentItem) {
        context.startActivity(new Intent(context, MeHistoryActivity.class)
                .putExtra(KEY_DATA, currentItem));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_me_history;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        mCurrentItem = getIntent().getIntExtra(KEY_DATA, mCurrentItem);
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }


    @Override
    public void initData() {
        //添加tab
        for (int i = 0; i < tabs.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabs[i]));
            tabFragmentList.add(new HistoryListFragment(tabs[i]));
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

        mViewPager.setCurrentItem(mCurrentItem, false);
    }
}