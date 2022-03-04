package com.thfw.robotheart.activitys.task;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.fragments.me.MsgFragment;
import com.thfw.robotheart.util.MsgCountManager;
import com.thfw.robotheart.view.TitleRobotView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MsgActivity extends RobotBaseActivity implements MsgCountManager.OnCountChangeListener {


    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.viewpager2.widget.ViewPager2 mViewPager;
    private LinearLayout mTabLayout;
    private TextView mTvDotCount01;
    private TextView mTvTab01;
    private TextView mTvDotCount02;
    private TextView mTvTab02;

    @Override
    public int getContentView() {
        return R.layout.activity_msg;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }


    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mViewPager = (ViewPager2) findViewById(R.id.viewPager);
        mTabLayout = (LinearLayout) findViewById(R.id.tabLayout);
        mTvDotCount01 = (TextView) findViewById(R.id.tv_dot_count_01);
        mTvTab01 = (TextView) findViewById(R.id.tv_tab_01);
        mTvDotCount02 = (TextView) findViewById(R.id.tv_dot_count_02);
        mTvTab02 = (TextView) findViewById(R.id.tv_tab_02);
    }

    @Override
    public void initData() {


        HashMap<Integer, Fragment> collectMap = new HashMap<>();
        collectMap.put(0, new MsgFragment(1));
        collectMap.put(1, new MsgFragment(2));
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
                    selectTab(mViewPager.getCurrentItem());
                }
            }
        });


        mTvTab01.setOnClickListener(v -> {
            mViewPager.setCurrentItem(0, false);
            selectTab(0);
        });

        mTvTab02.setOnClickListener(v -> {
            mViewPager.setCurrentItem(1, false);
            selectTab(1);
        });
        selectTab(0);

        MsgCountManager.getInstance().addOnCountChangeListener(this);

    }

    private void selectTab(int position) {
        mTvTab01.setSelected(position == 0);
        mTvTab02.setSelected(position == 1);
    }

    @Override
    public void onCount(int numTask, int numSystem) {
        MsgCountManager.setTextView(mTvDotCount01, numTask);
        MsgCountManager.setTextView(mTvDotCount02, numSystem);
    }

    @Override
    public void onItemState(int id, boolean read) {

    }

    @Override
    public void onDestroy() {
        MsgCountManager.getInstance().removeOnCountChangeListener(this);
        super.onDestroy();
    }
}