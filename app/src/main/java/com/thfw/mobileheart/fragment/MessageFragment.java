package com.thfw.mobileheart.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.fragment.message.MsgFragment;
import com.thfw.mobileheart.fragment.message.MsgTaskFragment;
import com.thfw.mobileheart.util.FragmentLoader;
import com.thfw.mobileheart.util.MsgCountManager;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.TitleView;

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
    private ViewPager2 mViewPager;
    private TitleView mTitleView;
    private int currentItem;

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

        FragmentLoader fragmentLoader = new FragmentLoader(getParentFragmentManager(), R.id.fl_content);
        fragmentLoader.add(R.id.rl_tab01, new MsgTaskFragment());
        fragmentLoader.add(R.id.rl_tab02, new MsgFragment());

        mRlTab01.setOnClickListener(v -> {
            fragmentLoader.load(v.getId());
            tabChange(0);
        });
        mRlTab02.setOnClickListener(v -> {
            fragmentLoader.load(v.getId());
            tabChange(1);
        });

        mRlTab01.performClick();

        /**
         * 【】 viewpager 实现
         */

//        mViewPager = (ViewPager2) findViewById(R.id.viewPager);
//        mRlTab01.setOnClickListener(v -> {
//            mViewPager.setCurrentItem(0);
//        });
//        mRlTab02.setOnClickListener(v -> {
//            mViewPager.setCurrentItem(1);
//        });
//        Fragment[] fragments = new Fragment[]{new MsgTaskFragment(), new MsgFragment()};
//        mViewPager.setAdapter(new FragmentStateAdapter(getActivity()) {
//            @Override
//            public int getItemCount() {
//                return fragments.length;
//            }
//
//            @NonNull
//            @Override
//            public Fragment createFragment(int position) {
//                return fragments[position];
//            }
//        });
//        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                tabChange(position);
//            }
//        });
//        tabChange(0);
    }

    private void tabChange(int position) {
        selectTab(position);
        currentItem = position;
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
            MsgCountManager.getInstance().readMsg(currentItem == 0 ? MsgCountManager.TYPE_TASK : MsgCountManager.TYPE_SYSTEM);
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
        if (mTitleView != null) {
            if (currentItem == 0) {
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