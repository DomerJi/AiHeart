package com.thfw.robotheart.fragments.sets;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.set.SystemAppActivity;
import com.thfw.robotheart.lhxk.LhXkHelper;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:Todo
 */
public class SetUpdateFragment extends RobotBaseFragment {

    public static final String SET_AUTO_UPDATE_BOOLEAN = "set.auto_update";
    private RelativeLayout mRlClickUpdate;
    private TextView mTvCheckTime;
    private TextView mTvNewVersionHint;
    private RelativeLayout mRlWifiUpdate;
    private Switch mSwitchWifiUpdate;
    private RelativeLayout mRlCurrentUpdate;
    private TextView mTvCurrentVersion;
    private RelativeLayout mRlOriginCode;
    private TextView mTvOriginCode;

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
        mRlCurrentUpdate = (RelativeLayout) findViewById(R.id.rl_current_update);
        mTvCurrentVersion = (TextView) findViewById(R.id.tv_current_version);
        mRlOriginCode = (RelativeLayout) findViewById(R.id.rl_origin_code);
        mTvOriginCode = (TextView) findViewById(R.id.tv_origin_code);
    }

    @Override
    public void initData() {
        mRlClickUpdate.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SystemAppActivity.class));
        });
        mTvCurrentVersion.setText(Util.getAppVersion(mContext));
        mTvOriginCode.setText(CommonParameter.getOrganizationId());
        mSwitchWifiUpdate.setChecked(SharePreferenceUtil.getBoolean(SET_AUTO_UPDATE_BOOLEAN, true));
        mSwitchWifiUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharePreferenceUtil.setBoolean(SET_AUTO_UPDATE_BOOLEAN, isChecked);
            }
        });
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        LhXkHelper.putAction(SetUpdateFragment.class, new SpeechToAction("自动更新", () -> {
            mSwitchWifiUpdate.setChecked(!mSwitchWifiUpdate.isChecked());
        }));
        LhXkHelper.putAction(SetUpdateFragment.class, new SpeechToAction("关闭自动更新", () -> {
            if (mSwitchWifiUpdate.isChecked()) {
                mSwitchWifiUpdate.setChecked(false);
            }
        }));
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);

        if (!isVisible) {
            BuglyUtil.requestNewVersion(null);
            return;
        }

        BuglyUtil.requestNewVersion(new SimpleUpgradeStateListener() {
            @Override
            public void onVersion(boolean hasNewVersion) {
                super.onVersion(hasNewVersion);
                Log.d("requestNewVersion", "hasNewVersion = " + hasNewVersion);
                if (EmptyUtil.isEmpty(getActivity()) || mTvNewVersionHint == null) {
                    return;
                }
                mTvNewVersionHint.setText("新");
                mTvNewVersionHint.setVisibility(hasNewVersion ? View.VISIBLE : View.GONE);
                if (mTvCheckTime != null) {
                    if (!hasNewVersion) {
                        mTvCheckTime.setText("您的版本是最新的");
                    } else {
                        mTvCheckTime.setVisibility(View.GONE);
                    }

                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BuglyUtil.requestNewVersion(null);
    }
}
