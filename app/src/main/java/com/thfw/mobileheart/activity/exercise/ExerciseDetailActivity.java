package com.thfw.mobileheart.activity.exercise;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.fragment.exercise.ExerciseHintFragment;
import com.thfw.mobileheart.fragment.exercise.ExerciseLogcataFragment;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.dialog.DialogFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnBindViewListener;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDetailActivity extends BaseActivity {

    private TitleView mTitleView;
    private ImageView mIvShare;
    private ImageView mIvTopBanner;
    private TextView mTvTitle;
    private TextView mTvClassHour;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<Fragment> tabFragmentList = new ArrayList<>();
    private String[] tabs = new String[]{"简介", "目录"};

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ExerciseDetailActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_exercise_detail;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mIvTopBanner = (ImageView) findViewById(R.id.iv_top_banner);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvClassHour = (TextView) findViewById(R.id.tv_class_hour);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIvShare.setOnClickListener(v -> {
            DialogFactory.createShare(ExerciseDetailActivity.this, new OnBindViewListener() {
                @Override
                public void bindView(BindViewHolder viewHolder) {

                }
            }, new OnViewClickListener() {
                @Override
                public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {

                }
            });
        });
    }

    @Override
    public void initData() {
        mTabLayout.addTab(mTabLayout.newTab().setText(tabs[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabs[1]));
        tabFragmentList.add(new ExerciseHintFragment());
        tabFragmentList.add(new ExerciseLogcataFragment());

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