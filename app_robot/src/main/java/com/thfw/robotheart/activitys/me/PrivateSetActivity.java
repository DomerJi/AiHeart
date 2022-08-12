package com.thfw.robotheart.activitys.me;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.port.ShakeNodActivity;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.HostActivity;
import com.thfw.ui.widget.InputBoxSquareView;

import java.util.Calendar;

public class PrivateSetActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.EditText mEtOrganzitionId;
    private android.widget.LinearLayout mLlTranAnim;
    private android.widget.LinearLayout mLlRobotAction;
    private android.widget.TextView mTvTranAnimFrequency;
    private LinearLayout mLlPushTest;
    private LinearLayout mLlHostSet;

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
        mLlRobotAction = (LinearLayout) findViewById(R.id.ll_robot_action);
        mTvTranAnimFrequency = (TextView) findViewById(R.id.tv_tran_anim_frequency);
        mLlHostSet = (LinearLayout) findViewById(R.id.ll_host_set);
        ConstraintLayout mClInputPassword = findViewById(R.id.cl_input_password);
        mClInputPassword.setOnClickListener(v -> {

        });
        InputBoxSquareView mInputBoxSquareView = findViewById(R.id.ibv_code);
        mInputBoxSquareView.setOnInputCompleteListener(new InputBoxSquareView.OnInputCompleteListener() {
            @Override
            public void onComplete(String text) {
                long time = System.currentTimeMillis();
                int week = Util.getWeekFormCalendar(time);
                Calendar calendar = Calendar.getInstance();
                // 获取当前年
                int year = calendar.get(Calendar.YEAR);
                // 获取当前月
                int month = calendar.get(Calendar.MONTH) + 1;
                // 获取当前日
                int day = calendar.get(Calendar.DATE);
                Util.getWeekFormCalendar(time);
                String password = null;
                if (day % 2 == 0) {
                    password = String.format("%02d", month + 2)
                            + String.format("%02d", day + 4)
                            + String.format("%02d", week + 6);

                } else {
                    password = String.format("%02d", month + 1)
                            + String.format("%02d", day + 3)
                            + String.format("%02d", week + 5);
                }

                if (password.equals(text)) {
                    mInputBoxSquareView.setVisibility(View.GONE);
                    mClInputPassword.setVisibility(View.GONE);
                    hideInput();
                } else {
                    hideInput();
                    ToastUtil.show("密码错误");
                }
            }

            @Override
            public void onChanged(String text) {

            }
        });
        mLlHostSet.setOnClickListener(v -> {
            startActivity(new Intent(mContext, HostActivity.class));
        });
        mLlTranAnim.setOnClickListener(v -> {
            setAnimFrequency();
            mTvTranAnimFrequency.setText(getAnimFrequencyStr());
            LogUtil.d(TAG, "mLlTranAnim++++++++++++++++++++++++++++++++++++++++" + getAnimFrequencyStr());
        });
        mTvTranAnimFrequency.setText(getAnimFrequencyStr());
        if (LogUtil.isLogEnabled() || RobotUtil.isInstallRobot()) {
            mLlRobotAction.setVisibility(View.VISIBLE);
            mLlRobotAction.setOnClickListener(v -> {
                startActivity(new Intent(mContext, ShakeNodActivity.class));
            });
        }
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