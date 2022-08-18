package com.thfw.robotheart.port;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pl.sphelper.ConstantUtil;
import com.pl.sphelper.SPHelper;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.robot.RobotUtil;

public class ShakeNodActivity extends RobotBaseActivity {

    private android.widget.TextView mTvProvider;
    private android.widget.Button mBtShake;
    private android.widget.Button mBtNod;
    private android.widget.Button mBtRotate;

    private int shakeZero = ConstantUtil.DEFAULT_INT;
    private int nodZero = ConstantUtil.DEFAULT_INT;
    private int rotateZero = ConstantUtil.DEFAULT_INT;
    private Button mBtShutdown;
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private Button mBtHideBar;
    private Button mBtShowBar;
    private Button mBtUpElectricity;
    private Button mBtAllAction;
    private TextView mTvSensorMsg;
    private android.widget.LinearLayout mLlRotateSet;
    private Button mBtAngleAdd;
    private TextView mTvRotateAngle;
    private Button mBtAngleDel;
    private android.widget.LinearLayout mLlRotateTime;
    private Button mBtTimeAdd;
    private TextView mTvRotateTime;
    private Button mBtTimeDel;

    private LinearLayout mLlShakeSet;
    private Button mBtShakeAngleAdd;
    private TextView mTvShakeAngle;
    private Button mBtShakeDel;
    private LinearLayout mLlShakeTime;
    private Button mBtShakeTimeAdd;
    private TextView mTvShakeTime;
    private Button mBtShakeTimeDel;

    private LinearLayout mLlNodSet;
    private Button mBtNodAngleAdd;
    private TextView mTvNodAngle;
    private Button mBtNodAngleDel;
    private LinearLayout mLlNodDownSet;
    private Button mBtNodDownAngleAdd;
    private TextView mTvNodDownAngle;
    private Button mBtNodDownAngleDel;
    private LinearLayout mLlNodTime;
    private Button mBtNodTimeAdd;
    private TextView mTvNodTime;
    private Button mBtNodTimeDel;


    @Override
    public int getContentView() {
        return R.layout.activity_shake_nod;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        mTvProvider = (TextView) findViewById(R.id.tv_provider);
        mBtShake = (Button) findViewById(R.id.bt_shake);
        mBtNod = (Button) findViewById(R.id.bt_nod);
        mBtRotate = (Button) findViewById(R.id.bt_rotate);
        mBtShutdown = (Button) findViewById(R.id.bt_shutdown);
        mBtHideBar = (Button) findViewById(R.id.bt_hide_bar);
        mBtShowBar = (Button) findViewById(R.id.bt_show_bar);
        mBtUpElectricity = (Button) findViewById(R.id.bt_up_electricity);
        mBtAllAction = (Button) findViewById(R.id.bt_all_action);
        // 转身度数
        mLlRotateSet = (LinearLayout) findViewById(R.id.ll_rotate_set);
        mBtAngleAdd = (Button) findViewById(R.id.bt_angle_add);
        mTvRotateAngle = (TextView) findViewById(R.id.tv_rotate_angle);
        mBtAngleDel = (Button) findViewById(R.id.bt_angle_del);
        mLlRotateTime = (LinearLayout) findViewById(R.id.ll_rotate_time);
        mBtTimeAdd = (Button) findViewById(R.id.bt_time_add);
        mTvRotateTime = (TextView) findViewById(R.id.tv_rotate_time);
        mBtTimeDel = (Button) findViewById(R.id.bt_time_del);
        int angle = SharePreferenceUtil.getInt(ActionParams.KEY_ROTATE_ANGLE, ActionParams.ROTATE_ANGLE);
        int time = SharePreferenceUtil.getInt(ActionParams.KEY_ROTATE_TIME, ActionParams.ONE_ANGLE_TIME);
        mTvRotateAngle.setText(String.valueOf(angle));
        mTvRotateTime.setText(String.valueOf(time));
        mBtRotate.setOnLongClickListener(v -> {
            boolean oldVisible = mLlRotateSet.getVisibility() == View.VISIBLE;
            mLlRotateSet.setVisibility(oldVisible ? View.GONE : View.VISIBLE);
            mLlRotateTime.setVisibility(oldVisible ? View.GONE : View.VISIBLE);
            return false;
        });
        mBtAngleAdd.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_ROTATE_ANGLE, ActionParams.ROTATE_ANGLE);
            mTvRotateAngle.setText(String.valueOf(angleTemp + 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_ROTATE_ANGLE, angleTemp + 1);
        });
        mBtAngleDel.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_ROTATE_ANGLE, ActionParams.ROTATE_ANGLE);
            mTvRotateAngle.setText(String.valueOf(angleTemp - 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_ROTATE_ANGLE, angleTemp - 1);
        });

        mBtTimeAdd.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_ROTATE_TIME, ActionParams.ONE_ANGLE_TIME);
            mTvRotateTime.setText(String.valueOf(angleTemp + 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_ROTATE_TIME, angleTemp + 1);
        });
        mBtTimeDel.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_ROTATE_TIME, ActionParams.ONE_ANGLE_TIME);
            mTvRotateTime.setText(String.valueOf(angleTemp - 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_ROTATE_TIME, angleTemp - 1);
        });

        // 摇头度数
        mLlShakeSet = (LinearLayout) findViewById(R.id.ll_shake_set);
        mBtShakeAngleAdd = (Button) findViewById(R.id.bt_shake_angle_add);
        mTvShakeAngle = (TextView) findViewById(R.id.tv_shake_angle);
        mBtShakeDel = (Button) findViewById(R.id.bt_shake_del);
        mLlShakeTime = (LinearLayout) findViewById(R.id.ll_shake_time);
        mBtShakeTimeAdd = (Button) findViewById(R.id.bt_shake_time_add);
        mTvShakeTime = (TextView) findViewById(R.id.tv_shake_time);
        mBtShakeTimeDel = (Button) findViewById(R.id.bt_shake_time_del);

        int angleShake = SharePreferenceUtil.getInt(ActionParams.KEY_SHAKE_ANGLE, ActionParams.SHAKE_ANGLE);
        int timeShake = SharePreferenceUtil.getInt(ActionParams.KEY_SHAKE_TIME, ActionParams.ONE_ANGLE_TIME2);
        mTvShakeAngle.setText(String.valueOf(angleShake));
        mTvShakeTime.setText(String.valueOf(timeShake));
        mBtShake.setOnLongClickListener(v -> {
            boolean oldVisible = mLlShakeSet.getVisibility() == View.VISIBLE;
            mLlShakeSet.setVisibility(oldVisible ? View.GONE : View.VISIBLE);
            mLlShakeTime.setVisibility(oldVisible ? View.GONE : View.VISIBLE);
            return true;
        });
        mBtShakeAngleAdd.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_SHAKE_ANGLE, ActionParams.SHAKE_ANGLE);
            mTvShakeAngle.setText(String.valueOf(angleTemp + 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_SHAKE_ANGLE, angleTemp + 1);
        });
        mBtShakeDel.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_SHAKE_ANGLE, ActionParams.SHAKE_ANGLE);
            mTvShakeAngle.setText(String.valueOf(angleTemp - 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_SHAKE_ANGLE, angleTemp - 1);
        });

        mBtShakeTimeAdd.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_SHAKE_TIME, ActionParams.ONE_ANGLE_TIME2);
            mTvShakeTime.setText(String.valueOf(angleTemp + 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_SHAKE_TIME, angleTemp + 1);
        });
        mBtShakeTimeDel.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_SHAKE_TIME, ActionParams.ONE_ANGLE_TIME2);
            mTvShakeTime.setText(String.valueOf(angleTemp - 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_SHAKE_TIME, angleTemp - 1);
        });

        // 点头度数
        mLlNodSet = (LinearLayout) findViewById(R.id.ll_nod_set);
        mBtNodAngleAdd = (Button) findViewById(R.id.bt_nod_angle_add);
        mTvNodAngle = (TextView) findViewById(R.id.tv_nod_angle);
        mBtNodAngleDel = (Button) findViewById(R.id.bt_nod_angle_del);
        mLlNodDownSet = (LinearLayout) findViewById(R.id.ll_nod_down_set);
        mBtNodDownAngleAdd = (Button) findViewById(R.id.bt_nod_down_angle_add);
        mTvNodDownAngle = (TextView) findViewById(R.id.tv_nod_down_angle);
        mBtNodDownAngleDel = (Button) findViewById(R.id.bt_nod_down_angle_del);
        mLlNodTime = (LinearLayout) findViewById(R.id.ll_nod_time);
        mBtNodTimeAdd = (Button) findViewById(R.id.bt_nod_time_add);
        mTvNodTime = (TextView) findViewById(R.id.tv_nod_time);
        mBtNodTimeDel = (Button) findViewById(R.id.bt_nod_time_del);

        int angleNodUp = SharePreferenceUtil.getInt(ActionParams.KEY_NOD_UP_ANGLE, ActionParams.NOD_UP_ANGLE);
        int angleNodDown = SharePreferenceUtil.getInt(ActionParams.KEY_NOD_DOWN_ANGLE, ActionParams.NOD_DOWN_ANGLE);
        int timeNod = SharePreferenceUtil.getInt(ActionParams.KEY_NOD_TIME, ActionParams.ONE_ANGLE_TIME2);

        mTvNodTime.setText(String.valueOf(timeNod));
        mTvNodAngle.setText(String.valueOf(angleNodUp));
        mTvNodDownAngle.setText(String.valueOf(angleNodDown));

        mBtNod.setOnLongClickListener(v -> {
            boolean oldVisible = mLlNodSet.getVisibility() == View.VISIBLE;
            mLlNodSet.setVisibility(oldVisible ? View.GONE : View.VISIBLE);
            mLlNodDownSet.setVisibility(oldVisible ? View.GONE : View.VISIBLE);
            mLlNodTime.setVisibility(oldVisible ? View.GONE : View.VISIBLE);
            return true;
        });

        mBtNodAngleAdd.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_NOD_UP_ANGLE, ActionParams.NOD_UP_ANGLE);
            mTvNodAngle.setText(String.valueOf(angleTemp + 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_NOD_UP_ANGLE, angleTemp + 1);
        });
        mBtNodAngleDel.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_NOD_UP_ANGLE, ActionParams.NOD_UP_ANGLE);
            mTvNodAngle.setText(String.valueOf(angleTemp - 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_NOD_UP_ANGLE, angleTemp - 1);
        });

        mBtNodDownAngleAdd.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_NOD_DOWN_ANGLE, ActionParams.NOD_DOWN_ANGLE);
            mTvNodDownAngle.setText(String.valueOf(angleTemp + 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_NOD_DOWN_ANGLE, angleTemp + 1);
        });
        mBtNodDownAngleDel.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_NOD_DOWN_ANGLE, ActionParams.NOD_DOWN_ANGLE);
            mTvNodDownAngle.setText(String.valueOf(angleTemp - 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_NOD_DOWN_ANGLE, angleTemp - 1);
        });

        mBtNodTimeAdd.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_NOD_TIME, ActionParams.ONE_ANGLE_TIME2);
            mTvNodTime.setText(String.valueOf(angleTemp + 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_NOD_TIME, angleTemp + 1);
        });
        mBtNodTimeDel.setOnClickListener(v -> {
            int angleTemp = SharePreferenceUtil.getInt(ActionParams.KEY_NOD_TIME, ActionParams.ONE_ANGLE_TIME2);
            mTvNodTime.setText(String.valueOf(angleTemp - 1));
            SharePreferenceUtil.setInt(ActionParams.KEY_NOD_TIME, angleTemp - 1);
        });
    }

    @Override
    public void initData() {
        shakeZero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
        nodZero = SPHelper.getInt(ConstantUtil.Key.NOD_ZERO);
        rotateZero = SPHelper.getInt(ConstantUtil.Key.ROTATE_ZERO);
        mTvProvider.setText("共享信息：\n摇头零位：" + shakeZero + "  点头零位：" + nodZero + "  转身零位：" + rotateZero);
        mTvProvider.setText(mTvProvider.getText().toString()
                + "\nisSystemApp：" + RobotUtil.isSystemApp() + "  isBuildMsg：" + RobotUtil.isBuildMsg());
        mTvProvider.setText(mTvProvider.getText().toString()
                + "  \nproduct: " + Build.PRODUCT + " | device: " + Build.DEVICE + " | brand: " + Build.BRAND + " | model: " + Build.MODEL);
        mBtShake.setOnClickListener(v -> {
            SerialManager.getInstance().startAction(ActionParams.getNormalShake());
        });

        mBtNod.setOnClickListener(v -> {
            SerialManager.getInstance().startAction(ActionParams.getNormalNod());
        });


        mBtRotate.setOnClickListener(v -> {
            SerialManager.getInstance().startAction(ActionParams.getNormalRotate());
        });

        /**
         * 两种方法都可行。一种没有关机提示（shutdownShell） 一种有 RobotUtil.shutdownByActivity(mContext);
         */
        mBtShutdown.setOnClickListener(v -> {
//            RobotUtil.shutdownByActivity(mContext);
            RobotUtil.shutdownShell();
        });

        mBtHideBar.setOnClickListener(v -> {
            RobotUtil.closeBar(mContext);
        });

        mBtShowBar.setOnClickListener(v -> {
            RobotUtil.showBar(mContext);
        });

        mBtUpElectricity.setOnClickListener(v -> {
            SerialManager.getInstance().upElectricityNow();
        });

        mBtAllAction.setOnClickListener(v -> {
            SerialManager.getInstance().startAllAction();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LogUtil.isLogEnabled()) {
            SerialManager.getInstance().addEleListener(getEleListener());
        }
    }


    SerialManager.ElectricityListener electricityListener;

    private SerialManager.ElectricityListener getEleListener() {
        if (electricityListener == null) {
            electricityListener = new SerialManager.ElectricityListener() {


                @Override
                public void onCharge(int percent, int charge) {

                }

                @Override
                public void onSensor(int sensor, boolean showTip) {
                    if (ToastUtil.isMainThread()) {
                        printSensor(sensor);
                    } else {
                        runOnUiThread(() -> {
                            printSensor(sensor);
                        });
                    }
                }
            };
        }
        return electricityListener;
    }

    private void printSensor(int sensor) {
        if (mTvSensorMsg == null) {
            mTvSensorMsg = findViewById(R.id.tv_sensor_msg);
        }
        if (mTvSensorMsg != null) {
            float[] gravity = SerialManager.getInstance().getGravity();
            if (EmptyUtil.isEmpty(gravity)) {
                return;
            }
            String gravityStr = String.format("x = %.2f ; y = %.2f ; z = %.2f ;", gravity[0], gravity[1], gravity[2]);
            mTvSensorMsg.setText(
                    "sensor ：" + sensor
                            + "\ngravity ：" + gravityStr
                            + "\nisNoAction ：" + SerialManager.getInstance().isNoAction()
                            + "\ntime ：" + HourUtil.getYYMMDD_HHMMSS(System.currentTimeMillis()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (electricityListener != null) {
            SerialManager.getInstance().removeEleListener(electricityListener);
        }
    }
}