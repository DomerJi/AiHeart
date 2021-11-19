package com.thfw.mobileheart.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.fragment.message.NoticeFragment;
import com.thfw.mobileheart.fragment.message.ReportFragment;
import com.thfw.mobileheart.fragment.message.TaskFragment;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;

import org.jetbrains.annotations.NotNull;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

/**
 * 消息
 */
public class MessageFragment extends BaseFragment {

    private ConstraintLayout mRlTab01;
    private TextView mTvTab01;
    private View mVIndactor01;
    private TextView mTvTab01Massage;
    private ConstraintLayout mRlTab02;
    private TextView mTvTab02;
    private View mVIndactor02;
    private TextView mTvTab02Massage;
    private ConstraintLayout mRlTab03;
    private TextView mTvTab03;
    private View mVIndactor03;
    private TextView mTvTab03Massage;
    private ViewPager mViewPager;

    @Override
    public int getContentView() {
        return R.layout.fragment_message;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRlTab01 = (ConstraintLayout) findViewById(R.id.rl_tab01);
        mTvTab01 = (TextView) findViewById(R.id.tv_tab01);
        mVIndactor01 = (View) findViewById(R.id.v_indactor_01);
        mTvTab01Massage = (TextView) findViewById(R.id.tv_tab01_massage);
        mRlTab02 = (ConstraintLayout) findViewById(R.id.rl_tab02);
        mTvTab02 = (TextView) findViewById(R.id.tv_tab02);
        mVIndactor02 = (View) findViewById(R.id.v_indactor_02);
        mTvTab02Massage = (TextView) findViewById(R.id.tv_tab02_massage);
        mRlTab03 = (ConstraintLayout) findViewById(R.id.rl_tab03);
        mTvTab03 = (TextView) findViewById(R.id.tv_tab03);
        mVIndactor03 = (View) findViewById(R.id.v_indactor_03);
        mTvTab03Massage = (TextView) findViewById(R.id.tv_tab03_massage);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mRlTab01.setOnClickListener(v -> {
            mViewPager.setCurrentItem(0);
        });
        mRlTab02.setOnClickListener(v -> {
            mViewPager.setCurrentItem(1);
        });
        mRlTab03.setOnClickListener(v -> {
            mViewPager.setCurrentItem(2);
        });

        Fragment[] fragments = new Fragment[]{new TaskFragment(), new ReportFragment(), new NoticeFragment()};
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @NotNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabChange(0);
    }

    private void tabChange(int position) {
        switch (position) {
            case 0:
                mTvTab01.setSelected(true);
                mTvTab02.setSelected(false);
                mTvTab03.setSelected(false);
                mVIndactor01.setVisibility(View.VISIBLE);
                mVIndactor02.setVisibility(View.INVISIBLE);
                mVIndactor03.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mTvTab01.setSelected(false);
                mTvTab02.setSelected(true);
                mTvTab03.setSelected(false);
                mVIndactor01.setVisibility(View.INVISIBLE);
                mVIndactor02.setVisibility(View.VISIBLE);
                mVIndactor03.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mTvTab01.setSelected(false);
                mTvTab02.setSelected(false);
                mTvTab03.setSelected(true);
                mVIndactor01.setVisibility(View.INVISIBLE);
                mVIndactor02.setVisibility(View.INVISIBLE);
                mVIndactor03.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void initData() {

    }
}