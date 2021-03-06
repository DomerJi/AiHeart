package com.thfw.robotheart.port;

import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import com.pl.sphelper.ConstantUtil;
import com.pl.sphelper.SPHelper;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
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
    }

    @Override
    public void initData() {
        shakeZero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
        nodZero = SPHelper.getInt(ConstantUtil.Key.NOD_ZERO);
        rotateZero = SPHelper.getInt(ConstantUtil.Key.ROTATE_ZERO);
        mTvProvider.setText("???????????????\n???????????????" + shakeZero + "  ???????????????" + nodZero + "  ???????????????" + rotateZero);
        mTvProvider.setText(mTvProvider.getText().toString()
                + "\nisSystemApp???" + RobotUtil.isSystemApp() + "  isBuildMsg???" + RobotUtil.isBuildMsg());
        mTvProvider.setText(mTvProvider.getText().toString()
                + "  \nproduct: " + Build.PRODUCT + " | device: " + Build.DEVICE + " | brand: " + Build.BRAND + " | model: " + Build.MODEL);
        mBtShake.setOnClickListener(v -> {
            SerialManager.getInstance().startAction(ActionParams.getNormalShake());
        });

        mBtNod.setOnClickListener(v -> {
            SerialManager.getInstance().startAction(ActionParams.getNormalNod());
        });


        mBtRotate.setOnClickListener(v -> {
            SerialManager.getInstance().startAction(ActionParams.getNormalRotate(45, -90, 45));
        });

        /**
         * ???????????????????????????????????????????????????shutdownShell??? ????????? RobotUtil.shutdownByActivity(mContext);
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
            mBtShake.performClick();
            mBtNod.performClick();
            mBtRotate.performClick();
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
                    "sensor ???" + sensor
                            + "\ngravity ???" + gravityStr
                            + "\nisNoAction ???" + SerialManager.getInstance().isNoAction()
                            + "\ntime ???" + HourUtil.getYYMMDD_HHMMSS(System.currentTimeMillis()));
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