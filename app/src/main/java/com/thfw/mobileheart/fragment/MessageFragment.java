package com.thfw.mobileheart.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.fragment.message.MsgFragment;
import com.thfw.mobileheart.fragment.message.MsgTaskFragment;
import com.thfw.mobileheart.util.MsgCountManager;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.TitleView;

import org.jetbrains.annotations.NotNull;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

/**
 * 消息
 */
public class MessageFragment extends BaseFragment implements MsgCountManager.OnCountChangeListener {

    private ConstraintLayout mRlTab01;
    private TextView mTvTab01;
    private View mVIndactor01;
    private TextView mTvTab01Massage;
    private ConstraintLayout mRlTab02;
    private TextView mTvTab02;
    private View mVIndactor02;
    private TextView mTvTab02Massage;
    private ViewPager mViewPager;
    private TitleView mTitleView;

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
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRlTab01 = (ConstraintLayout) findViewById(R.id.rl_tab01);
        mTvTab01 = (TextView) findViewById(R.id.tv_tab01);
        mVIndactor01 = (View) findViewById(R.id.v_indactor_01);
        mTvTab01Massage = (TextView) findViewById(R.id.tv_tab01_massage);
        mRlTab02 = (ConstraintLayout) findViewById(R.id.rl_tab02);
        mTvTab02 = (TextView) findViewById(R.id.tv_tab02);
        mVIndactor02 = (View) findViewById(R.id.v_indactor_02);
        mTvTab02Massage = (TextView) findViewById(R.id.tv_tab02_massage);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mRlTab01.setOnClickListener(v -> {
            mViewPager.setCurrentItem(0);
        });
        mRlTab02.setOnClickListener(v -> {
            mViewPager.setCurrentItem(1);
        });

        Fragment[] fragments = new Fragment[]{new MsgTaskFragment(), new MsgFragment()};
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
        selectTab(position);
        switch (position) {
            case 0:
                mTvTab01.setSelected(true);
                mTvTab02.setSelected(false);
                mVIndactor01.setVisibility(View.VISIBLE);
                mVIndactor02.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mTvTab01.setSelected(false);
                mTvTab02.setSelected(true);
                mVIndactor01.setVisibility(View.INVISIBLE);
                mVIndactor02.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void initData() {


        MsgCountManager.getInstance().addOnCountChangeListener(this);

        mTitleView.setRightOnClickListener(v -> {
            LoadingDialog.show(getActivity(), "处理中");
            MsgCountManager.getInstance().readMsg(mViewPager.getCurrentItem() == 0
                    ? MsgCountManager.TYPE_TASK : MsgCountManager.TYPE_SYSTEM);
        });
    }

    private void selectTab(int position) {
        if (position == 0) {
            mTitleView.getTvRight().setEnabled(MsgCountManager.getInstance().getNumTask() > 0);
        } else {
            mTitleView.getTvRight().setEnabled(MsgCountManager.getInstance().getNumSystem() > 0);
        }
    }

    @Override
    public void onCount(int numTask, int numSystem) {
        MsgCountManager.setTextView(mTvTab01Massage, numTask);
        MsgCountManager.setTextView(mTvTab02Massage, numSystem);
        if (mViewPager != null) {
            if (mViewPager.getCurrentItem() == 0) {
                mTitleView.getTvRight().setEnabled(MsgCountManager.getInstance().getNumTask() > 0);
            } else {
                mTitleView.getTvRight().setEnabled(MsgCountManager.getInstance().getNumSystem() > 0);
            }
        }
    }

    @Override
    public void onItemState(int id, boolean read) {

    }

    @Override
    public void onReadAll(int type) {
        LoadingDialog.hide();
    }

    @Override
    public void onDestroy() {
        MsgCountManager.getInstance().removeOnCountChangeListener(this);
        super.onDestroy();
    }
}