package com.thfw.robotheart.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.port.SerialManager;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.util.Dormant;

import org.jetbrains.annotations.NotNull;

import static android.os.BatteryManager.BATTERY_STATUS_CHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_DISCHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_FULL;
import static android.os.BatteryManager.BATTERY_STATUS_NOT_CHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_UNKNOWN;
import static android.os.BatteryManager.EXTRA_STATUS;

/**
 * Author:pengs
 * Date: 2021/11/17 9:25
 * Describe:Todo
 */
public class TitleBarView extends LinearLayout {


    private static final String TAG = TitleBarView.class.getSimpleName();
    private static volatile int level;
    private ImageView mIvTitleBarWifi;
    private TextView mTvTitleBarTime;
    private RelativeLayout mFlBattery;
    private ProgressBar mPbBatteryProgress;
    private TextView mTvProgress;
    private Context mContext;
    private BroadcastReceiver mBatInfoReceiver;
    private BroadcastReceiver mWifiStateReceiver;
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver mBluecastReceiver;
    private boolean colorFg = false;
    private View mVBatteryHead;
    private boolean colorFgWhite;
    private ImageView mIvTitleBarBlue;
    private RelativeLayout mRlBattery;
    private ImageView mIvBatteryIng;


    private SerialManager.ElectricityListener mElectricityListener;
    private TimingHelper.WorkListener mWorkListener;

    public TitleBarView(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public TitleBarView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TitleBarView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, com.thfw.ui.R.styleable.TitleBarView);
            final int colorBg = ta.getColor(com.thfw.ui.R.styleable.TitleBarView_tbv_background, getResources().getColor(R.color.titlebar_background));
            colorFg = ta.getBoolean(com.thfw.ui.R.styleable.TitleBarView_tbv_black, false);
            colorFgWhite = ta.getBoolean(com.thfw.ui.R.styleable.TitleBarView_tbv_white, false);
            if (colorFg) {
                mIvTitleBarWifi.setColorFilter(Color.BLACK);
                mIvTitleBarBlue.setColorFilter(Color.BLACK);
                mTvTitleBarTime.setTextColor(Color.BLACK);
                mPbBatteryProgress.setProgressTintList(ColorStateList.valueOf(Color.BLACK));
                mPbBatteryProgress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                mVBatteryHead.setBackgroundColor(Color.BLACK);
                mTvProgress.setTextColor(Color.parseColor("#BBBBBB"));
                mIvBatteryIng.setColorFilter(Color.BLACK);
            } else if (colorFgWhite) {
                mIvTitleBarWifi.setColorFilter(Color.WHITE);
                mIvTitleBarBlue.setColorFilter(Color.WHITE);
                mTvTitleBarTime.setTextColor(Color.WHITE);
                mPbBatteryProgress.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
                mPbBatteryProgress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                mVBatteryHead.setBackgroundColor(Color.WHITE);
                mTvProgress.setTextColor(Color.BLACK);
                mIvBatteryIng.setColorFilter(Color.WHITE);
            }

            setBackgroundColor(colorBg);
        }

    }


    private void init() {

        mContext = getContext();
        if (getVisibility() == GONE) {
            return;
        }
        setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        inflate(mContext, R.layout.layout_titlebar_view, this);
        initView();

        // todo 正式策略应该是机器人
        if (RobotUtil.isInstallRobot()) {
            initBatAndTimeReceiverRobot();
        } else {
            initBatReceiver();
            initTimeReceiver();
        }
        initWifiReceiver();
        initBlueReceiver();
    }

    // 机器人电量和 充电状态
    private void initBatAndTimeReceiverRobot() {
        if (mElectricityListener == null) {
            mElectricityListener = (percent, charge) -> {
                level = percent;
                updateBattery(level);
                // 充电中
                mIvBatteryIng.setVisibility(charge == 1 ? VISIBLE : GONE);
            };
        }
        SerialManager.getInstance().addEleListener(mElectricityListener);
        // todo 机器人时间广播无效，故用此方法
        if (mWorkListener == null) {
            mWorkListener = new TimingHelper.WorkListener() {
                @Override
                public void onArrive() {
                    LogUtil.i(TAG, "SLEEP time +++ ");
                    mTvTitleBarTime.setText(HourUtil.getHHMM(System.currentTimeMillis()));
                    Dormant.addMinute(mContext);
                    SerialManager.getInstance().queryCharge();
                }

                @Override
                public WorkInt workInt() {
                    return WorkInt.SLEEP;
                }
            };
        }
        TimingHelper.getInstance().addWorkArriveListener(mWorkListener);
    }

    private void initTimeReceiver() {
        createTimeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        getContext().registerReceiver(broadcastReceiver, filter);
        //广播的注册，其中Intent.ACTION_TIME_CHANGED代表时间设置变化的时候会发出该广播
    }

    private void initBatReceiver() {
        createBatReceiver();
        getContext().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void initWifiReceiver() {
        createWifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(mWifiStateReceiver, filter);
    }

    private void initBlueReceiver() {
        createBlueReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
        filter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
        getContext().registerReceiver(mBluecastReceiver, filter);
    }

    private void initView() {
        mIvTitleBarWifi = (ImageView) findViewById(R.id.iv_titleBar_wifi);
        mTvTitleBarTime = (TextView) findViewById(R.id.tv_titleBar_time);
        mFlBattery = (RelativeLayout) findViewById(R.id.rl_battery);
        mPbBatteryProgress = (ProgressBar) findViewById(R.id.pb_battery_progress);
        mIvBatteryIng = (ImageView) findViewById(R.id.iv_battery_ing);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);
        mVBatteryHead = findViewById(R.id.v_battery_head);
        if (level > 0) {
            updateBattery(level);
        }
        mTvTitleBarTime.setText(HourUtil.getHHMM(System.currentTimeMillis()));
        mIvTitleBarWifi.setVisibility(NetworkUtil.isWifiConnected(mContext) ? VISIBLE : GONE);
        mIvTitleBarBlue = (ImageView) findViewById(R.id.iv_titleBar_blue);
        mRlBattery = (RelativeLayout) findViewById(R.id.rl_battery);

        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        boolean blueOpen = false;
        //支持蓝牙模块
        if (blueadapter != null) {
            blueOpen = blueadapter.isEnabled();
        }
        mIvTitleBarBlue.setVisibility(blueOpen ? VISIBLE : GONE);
    }


    private void updateBattery(int level) {
        if (level > 100) {
            level = 100;
        }
        if (level < 0) {
            level = 0;
        }
        mPbBatteryProgress.setProgress(level);
        mTvProgress.setText(level + "%");
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mWifiStateReceiver != null) {
            mContext.unregisterReceiver(this.mWifiStateReceiver);
        }
        if (mBatInfoReceiver != null) {
            mContext.unregisterReceiver(this.mBatInfoReceiver);
        }
        if (broadcastReceiver != null) {
            mContext.unregisterReceiver(this.broadcastReceiver);
        }
        if (mBluecastReceiver != null) {
            mContext.unregisterReceiver(this.mBluecastReceiver);
        }
        if (mElectricityListener != null) {
            SerialManager.getInstance().removeEleListener(mElectricityListener);
        }
        if (mWorkListener != null) {
            TimingHelper.getInstance().removeWorkArriveListener(mWorkListener);
        }
    }

    private void createBatReceiver() {

        if (mBatInfoReceiver == null) {
            mBatInfoReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    LogUtil.d(TAG, "level = " + level);
                    updateBattery(level);
                    int status = intent.getIntExtra(EXTRA_STATUS, BATTERY_STATUS_UNKNOWN);

                    switch (status) {
                        case BATTERY_STATUS_CHARGING:
                            // 充电中
                            mIvBatteryIng.setVisibility(VISIBLE);
                            break;
                        case BATTERY_STATUS_UNKNOWN:// 未知
                        case BATTERY_STATUS_FULL:
                            // 充满电
                        case BATTERY_STATUS_NOT_CHARGING:
                            // 未充电
                        case BATTERY_STATUS_DISCHARGING:
                            // 放电中
                            mIvBatteryIng.setVisibility(GONE);
                            break;
                    }
                }
            };
        }
    }

    private void createWifiReceiver() {


        if (mWifiStateReceiver == null) {
            mWifiStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mIvTitleBarWifi.setVisibility(NetworkUtil.isWifiConnected(mContext) ? VISIBLE : GONE);
                }
            };
        }
    }

    private void createTimeReceiver() {

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                        mTvTitleBarTime.setText(HourUtil.getHHMM(System.currentTimeMillis()));
                        Dormant.addMinute(mContext);
                    } else if (intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
                        mTvTitleBarTime.setText(HourUtil.getHHMM(System.currentTimeMillis()));
                    }
                }
            };
        }
    }

    private void createBlueReceiver() {

        if (mBluecastReceiver == null) {
            mBluecastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (action) {
                        case BluetoothDevice.ACTION_ACL_CONNECTED:
//                            Toast.makeText(context , "蓝牙设备:" + device.getName() + "已链接", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothDevice.ACTION_ACL_DISCONNECTED:
//                            Toast.makeText(context , "蓝牙设备:" + device.getName() + "已断开", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                            switch (blueState) {
                                case BluetoothAdapter.STATE_OFF:
                                    mIvTitleBarBlue.setVisibility(GONE);
                                    break;
                                case BluetoothAdapter.STATE_ON:
                                    mIvTitleBarBlue.setVisibility(VISIBLE);
                                    break;
                            }
                            break;
                    }
                }
            };
        }
    }


}
