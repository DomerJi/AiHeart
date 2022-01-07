package com.thfw.robotheart.fragments.sets;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.me.SystemAppActivity;
import com.thfw.ui.base.RobotBaseFragment;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:Todo
 */
public class SetUpdateFragment extends RobotBaseFragment {

    private RelativeLayout mRlClickUpdate;
    private TextView mTvCheckTime;
    private TextView mTvNewVersionHint;
    private RelativeLayout mRlWifiUpdate;
    private Switch mSwitchWifiUpdate;

    @Override
    public int getContentView() {
        return R.layout.fragment_set_update;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRlClickUpdate = (RelativeLayout) findViewById(R.id.rl_click_update);
        mTvCheckTime = (TextView) findViewById(R.id.tv_check_time);
        mTvNewVersionHint = (TextView) findViewById(R.id.tv_new_version_hint);
        mRlWifiUpdate = (RelativeLayout) findViewById(R.id.rl_wifi_update);
        mSwitchWifiUpdate = (Switch) findViewById(R.id.switch_wifi_update);
    }

    @Override
    public void initData() {
        mRlClickUpdate.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SystemAppActivity.class));
        });
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);

        if (!isVisible) {
            return;
        }

        BuglyUtil.requestNewVersion(new SimpleUpgradeStateListener() {
            @Override
            public void onVersion(boolean hasNewVersion) {
                super.onVersion(hasNewVersion);
                Log.d("requestNewVersion", "hasNewVersion = " + hasNewVersion);
                if (EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                TextView view = (TextView) findViewById(R.id.tv_new_version_hint);
                if (view == null) {
                    return;
                }
                view.setText("新");
                view.setVisibility(hasNewVersion ? View.VISIBLE : View.GONE);
            }
        });
    }
}
