package com.thfw.robotheart.activitys.set;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.ClickCountUtils;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.fragments.sets.SetBlueFragment;
import com.thfw.robotheart.fragments.sets.SetDormantFragment;
import com.thfw.robotheart.fragments.sets.SetLightFragment;
import com.thfw.robotheart.fragments.sets.SetNetFragment;
import com.thfw.robotheart.fragments.sets.SetSpeechFragment;
import com.thfw.robotheart.fragments.sets.SetUpdateFragment;
import com.thfw.robotheart.fragments.sets.SetVolumeFragment;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;

public class SettingActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleBarView mTitleBarView;
    private TitleRobotView mTitleRobotView;
    private android.widget.FrameLayout mFlContent;
    private android.widget.LinearLayout mLlNavigation;
    private android.widget.TextView mTvSetNet;
    private android.widget.TextView mTvSetVolume;
    private android.widget.TextView mTvSetLight;
    private android.widget.TextView mTvSetBlue;
    private android.widget.TextView mTvSetUpdate;
    private android.widget.TextView mTvSetSpeech;
    private android.widget.TextView mTvSetDormant;
    private View[] mTabs;

    private Fragment mFragment;
    private RelativeLayout mRlSetUpdate;

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

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mTitleRobotView.setOnClickListener(v -> {
            if (ClickCountUtils.click(10)) {
                if (LogUtil.switchLogEnable()) {
                    ToastUtil.show("Log调试 -> 开启");
                } else {
                    ToastUtil.show("Log调试 -> 关闭");
                }
            }
        });
        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        mLlNavigation = (LinearLayout) findViewById(R.id.ll_navigation);
        mTvSetNet = (TextView) findViewById(R.id.tv_set_net);
        mTvSetVolume = (TextView) findViewById(R.id.tv_set_volume);
        mTvSetSpeech = (TextView) findViewById(R.id.tv_set_speech);
        mTvSetLight = (TextView) findViewById(R.id.tv_set_light);
        mTvSetBlue = (TextView) findViewById(R.id.tv_set_blue);
        mTvSetDormant = (TextView) findViewById(R.id.tv_set_dormant);
        mTvSetUpdate = (TextView) findViewById(R.id.tv_set_update);
        mRlSetUpdate = (RelativeLayout) findViewById(R.id.rl_set_update);
    }

    @Override
    public void initData() {

        FragmentLoader mLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        if (LogUtil.isLogEnabled() || RobotUtil.isSystemApp()) {
            mLoader.add(R.id.tv_set_net, new SetNetFragment());
        } else {
            mTvSetNet.setVisibility(View.GONE);
        }
        mLoader.add(R.id.tv_set_volume, new SetVolumeFragment());
        mLoader.add(R.id.tv_set_speech, new SetSpeechFragment());

        if (LogUtil.isLogEnabled() || RobotUtil.isSystemApp()) {
            mLoader.add(R.id.tv_set_light, new SetLightFragment());
        } else {
            mTvSetLight.setVisibility(View.GONE);
        }
        mLoader.add(R.id.tv_set_blue, new SetBlueFragment());
        mLoader.add(R.id.tv_set_dormant, new SetDormantFragment());
//        mLoader.add(R.id.tv_set_shutdown, new SetShutdownFragment());
        mLoader.add(R.id.rl_set_update, new SetUpdateFragment());
        if (LogUtil.isLogEnabled() || RobotUtil.isSystemApp()) {
            mTabs = new View[]{mTvSetNet, mTvSetVolume, mTvSetSpeech, mTvSetLight, mTvSetBlue, mTvSetDormant, mRlSetUpdate};
        } else {
            mTabs = new View[]{mTvSetVolume, mTvSetSpeech, mTvSetBlue, mTvSetDormant, mRlSetUpdate};
        }


        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mFragment = mLoader.load(v.getId());
                for (View tab : mTabs) {
                    tab.setSelected(tab.getId() == v.getId());
                    if (tab.getId() == R.id.rl_set_update) {
                        mTvSetUpdate.setTypeface(Typeface.defaultFromStyle(tab.getId() == v.getId() ? Typeface.BOLD : Typeface.NORMAL));
                    } else {
                        TextView mTvTab = (TextView) tab;
                        mTvTab.setTypeface(Typeface.defaultFromStyle(tab.getId() == v.getId() ? Typeface.BOLD : Typeface.NORMAL));
                    }
                }
            }
        };
        for (View tab : mTabs) {
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
        mLlNavigation.setOnClickListener(v -> {
            if (ClickCountUtils.click(10)) {
                Intent intent = new Intent("/");
                ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.Settings");
                intent.setComponent(cm);
                intent.setAction("android.intent.action.VIEW");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BuglyUtil.requestNewVersion(new SimpleUpgradeStateListener() {
            @Override
            public void onVersion(boolean hasNewVersion) {
                super.onVersion(hasNewVersion);
                Log.d("requestNewVersion", "hasNewVersion = " + hasNewVersion);
                if (EmptyUtil.isEmpty(SettingActivity.this)) {
                    return;
                }
                TextView view = findViewById(R.id.tv_new_version_hint);
                if (view == null) {
                    return;
                }
                view.setText("新");
                view.setVisibility(hasNewVersion ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BuglyUtil.requestNewVersion(null);
    }

}