package com.thfw.robotheart.activitys.me;

import android.graphics.Typeface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.fragments.sets.SetBlueFragment;
import com.thfw.robotheart.fragments.sets.SetLightFragment;
import com.thfw.robotheart.fragments.sets.SetNetFragment;
import com.thfw.robotheart.fragments.sets.SetShutdownFragment;
import com.thfw.robotheart.fragments.sets.SetUpdateFragment;
import com.thfw.robotheart.fragments.sets.SetVolumeFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleBarView;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;

public class SettingActivity extends RobotBaseActivity {


    private com.thfw.robotheart.view.TitleBarView mTitleBarView;
    private TitleRobotView mTitleRobotView;
    private android.widget.FrameLayout mFlContent;
    private android.widget.LinearLayout mLlNavigation;
    private android.widget.TextView mTvSetNet;
    private android.widget.TextView mTvSetVolume;
    private android.widget.TextView mTvSetLight;
    private android.widget.TextView mTvSetBlue;
    private android.widget.TextView mTvSetShutdown;
    private android.widget.TextView mTvSetUpdate;
    private TextView[] mTabs;

    private Fragment mFragment;

    @Override
    public int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleBarView = (TitleBarView) findViewById(R.id.titleBarView);
        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        mLlNavigation = (LinearLayout) findViewById(R.id.ll_navigation);
        mTvSetNet = (TextView) findViewById(R.id.tv_set_net);
        mTvSetVolume = (TextView) findViewById(R.id.tv_set_volume);
        mTvSetLight = (TextView) findViewById(R.id.tv_set_light);
        mTvSetBlue = (TextView) findViewById(R.id.tv_set_blue);
        mTvSetShutdown = (TextView) findViewById(R.id.tv_set_shutdown);
        mTvSetUpdate = (TextView) findViewById(R.id.tv_set_update);
        mTabs = new TextView[]{mTvSetNet, mTvSetVolume, mTvSetLight, mTvSetBlue, mTvSetShutdown, mTvSetUpdate};
    }

    @Override
    public void initData() {

        FragmentLoader mLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        mLoader.add(R.id.tv_set_net, new SetNetFragment());
        mLoader.add(R.id.tv_set_volume, new SetVolumeFragment());
        mLoader.add(R.id.tv_set_light, new SetLightFragment());
        mLoader.add(R.id.tv_set_blue, new SetBlueFragment());
        mLoader.add(R.id.tv_set_shutdown, new SetShutdownFragment());
        mLoader.add(R.id.tv_set_update, new SetUpdateFragment());

        View.OnClickListener onClickListener = new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                mFragment = mLoader.load(v.getId());
                for (TextView tab : mTabs) {
                    tab.setSelected(tab.getId() == v.getId());
                    tab.setTypeface(Typeface.defaultFromStyle(tab.getId() == v.getId() ? Typeface.BOLD : Typeface.NORMAL));
                }
            }
        };
        for (TextView tab : mTabs) {
            tab.setOnClickListener(onClickListener);
        }
        mTabs[0].performClick();

        mTitleRobotView.getIvBack().setOnClickListener(v -> {
            // 如果网络设置正在输入密码，先返回，不退出
            if (mFragment != null && mFragment instanceof SetNetFragment) {
                SetNetFragment setNetFragment = (SetNetFragment) mFragment;
                if (setNetFragment.wifiInputFragment()) {
                    setNetFragment.removeFragment();
                    return;
                }
            }
            finish();
        });
    }
}