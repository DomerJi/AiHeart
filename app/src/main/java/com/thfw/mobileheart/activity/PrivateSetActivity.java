package com.thfw.mobileheart.activity;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.robot.RobotActivity;
import com.thfw.ui.common.HostActivity;
import com.thfw.ui.widget.DeviceUtil;
import com.thfw.ui.widget.InputBoxSquareView;
import com.thfw.ui.widget.TitleView;

import java.util.Calendar;

public class PrivateSetActivity extends RobotActivity {

    private TitleView mTitleView;
    private LinearLayout mLlHostSet;
    private CheckBox mCbDebug;
    private TitleView mTitleRobotView;
    private LinearLayout mLlFaceFocus;
    private android.widget.Switch mSwitchFaceFocus;
    private ConstraintLayout mClInputPassword;
    private InputBoxSquareView mIbvCode;

    public static final String KEY_FACE_FOCUS = "key.face.focus";


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

        mTitleView = (TitleView) findViewById(R.id.titleRobotView);
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
        mCbDebug = findViewById(R.id.cb_debug);
        mCbDebug.setChecked(LogUtil.isLogSpEnable());
        mCbDebug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtil.switchLogEnable(isChecked);
            }
        });
        if (DeviceUtil.isLhXk_CM_GB03D()) {
            mLlFaceFocus = (LinearLayout) findViewById(R.id.ll_face_focus);
            mSwitchFaceFocus = (Switch) findViewById(R.id.switch_face_focus);
            mSwitchFaceFocus.setChecked(SharePreferenceUtil.getInt(KEY_FACE_FOCUS, 1) == 1);
            mSwitchFaceFocus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharePreferenceUtil.setInt(KEY_FACE_FOCUS, isChecked ? 1 : 0);
                }
            });
        } else {
            mLlFaceFocus.setVisibility(View.GONE);
        }

    }

    @Override
    public void initData() {

    }
}