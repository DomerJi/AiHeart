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
import com.thfw.base.base.SpeechToAction;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.audio.AudioHomeActivity;
import com.thfw.mobileheart.fragment.list.HistoryListFragment;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.ui.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

public class MeHistoryActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;

    private List<Fragment> tabFragmentList = new ArrayList<>();
    private String[] tabs = new String[]{"我测的", "我练的", "我听的", "我看的", "我读的", "我学的"};

    // 1-测评  2-文章 3-音频 4-视频 5-话术 6-思政文章 7-工具包
    private int[] types = new int[]{HistoryApi.TYPE_TEST, HistoryApi.TYPE_EXERCISE,
            HistoryApi.TYPE_AUDIO, HistoryApi.TYPE_VIDEO, HistoryApi.TYPE_BOOK, HistoryApi.TYPE_STUDY};

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

        mViewPager.setOffscreenPageLimit(tabs.length);
        //添加tab
        for (int i = 0; i < tabs.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabs[i]));
            tabFragmentList.add(new HistoryListFragment(types[i]));
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

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        int count = mTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            final TabLayout.Tab fTab = mTabLayout.getTabAt(i);

            LhXkHelper.putAction(MeHistoryActivity.class, new SpeechToAction(fTab.getText().toString(), () -> {
                mTabLayout.selectTab(fTab);
            }));
        }
    }
}