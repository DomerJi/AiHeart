package com.thfw.robotheart.activitys.me;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.view.TitleRobotView;

public class PrivateSetActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.EditText mEtOrganzitionId;
    private android.widget.LinearLayout mLlTranAnim;
    private android.widget.TextView mTvTranAnimFrequency;
    private LinearLayout mLlPushTest;

    public static final int getAnimFrequency() {
        return SharePreferenceUtil.getInt(AnimFileName.Frequency.KEY_FREQUENCY, AnimFileName.Frequency.EVERY_TIME);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_private_set;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);

        mEtOrganzitionId = (EditText) findViewById(R.id.et_organzition_id);
        mEtOrganzitionId.setText(CommonParameter.getOrganizationId());
        mLlTranAnim = (LinearLayout) findViewById(R.id.ll_tran_anim);
        mTvTranAnimFrequency = (TextView) findViewById(R.id.tv_tran_anim_frequency);
        mLlTranAnim.setOnClickListener(v -> {
            setAnimFrequency();
            mTvTranAnimFrequency.setText(getAnimFrequencyStr());
            LogUtil.d(TAG, "mLlTranAnim++++++++++++++++++++++++++++++++++++++++" + getAnimFrequencyStr());
        });
        mTvTranAnimFrequency.setText(getAnimFrequencyStr());
    }

    private void setAnimFrequency() {
        SharePreferenceUtil.setInt(AnimFileName.Frequency.KEY_FREQUENCY, (getAnimFrequency() + 1) % 3);
    }

    private String getAnimFrequencyStr() {
        int i = getAnimFrequency();
        switch (i) {
            case AnimFileName.Frequency.EVERY_TIME:
                return "每次";
            case AnimFileName.Frequency.DAY_TIME:
                return "每天一次";
            case AnimFileName.Frequency.WEEK_TIME:
                return "每星期一次";
        }
        return "";
    }

    @Override
    public void initData() {
        mEtOrganzitionId.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CommonParameter.setOrganizationId(s.toString());
            }
        });
    }
}