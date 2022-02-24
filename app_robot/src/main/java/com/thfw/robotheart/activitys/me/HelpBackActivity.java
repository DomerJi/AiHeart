package com.thfw.robotheart.activitys.me;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.fragments.help.AboutMeFragment;
import com.thfw.robotheart.fragments.help.AudioInstructFragment;
import com.thfw.robotheart.fragments.help.CommonProblemFragment;
import com.thfw.robotheart.fragments.help.IdeaBackFragment;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.robotheart.activitys.RobotBaseActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class HelpBackActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private com.google.android.material.tabs.TabItem mTiProblem;
    private com.google.android.material.tabs.TabItem mTiInstruct;
    private com.google.android.material.tabs.TabItem mTiBack;
    private com.google.android.material.tabs.TabItem mTiAbout;
    private androidx.viewpager2.widget.ViewPager2 mViewPager;

    @Override
    public int getContentView() {
        return R.layout.activity_help_back;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTiProblem = (TabItem) findViewById(R.id.ti_problem);
        mTiInstruct = (TabItem) findViewById(R.id.ti_instruct);
        mTiBack = (TabItem) findViewById(R.id.ti_back);
        mTiAbout = (TabItem) findViewById(R.id.ti_about);
        mViewPager = (ViewPager2) findViewById(R.id.viewPager);
    }

    @Override
    public void initData() {
        int count = mTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            TabLayout.TabView tabView = mTabLayout.getTabAt(i).view;
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) tabView.getLayoutParams();
            p.leftMargin = 50;
            p.rightMargin = 50;
            tabView.setLayoutParams(p);
        }
        HashMap<Integer, Fragment> collectMap = new HashMap<>();
        collectMap.put(0, new CommonProblemFragment());
        collectMap.put(1, new AudioInstructFragment());
        collectMap.put(2, new IdeaBackFragment());
        collectMap.put(3, new AboutMeFragment());
        mViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @NotNull
            @Override
            public Fragment createFragment(int position) {
                return collectMap.get(position);
            }

            @Override
            public int getItemCount() {
                return collectMap.size();
            }
        });

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    mTabLayout.selectTab(mTabLayout.getTabAt(mViewPager.getCurrentItem()));
                }
            }
        });

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
}