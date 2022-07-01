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

import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.port.SerialManager;
import com.thfw.robotheart.robot.BleManager;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.Dormant;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;

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
    private static volatile int level = 100;
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
    private static boolean mIvBatteryIngVisible = false;

    public static int getLevel() {
        return level;
    }

    private SerialManager.ElectricityListener mElectricityListener;
    private TimingHelper.WorkListener mWorkListener;
    private int oldCharge = -1;

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
        notifyBlueIconState();
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
            mElectricityListener = new SerialManager.ElectricityListener() {
                @Override
                public void onCharge(int percent, int charge) {
                    updateBattery(percent);
                    // 充电中
                    mIvBatteryIng.setVisibility(charge == 1 ? VISIBLE : GONE);

                    if (oldCharge == 1 && charge == 0) {
                        robotNoCharge(mContext);
                    }
                    oldCharge = charge;
                }

                @Override
                public void onSensor(int sensor) {
                    if (sensor == 1) {
                        robotNoCharge(mContext);
                    }
                }
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

    public static void robotNoCharge(Context mContext) {
        if (!Dormant.isCanDormant() && mContext instanceof RobotBaseActivity) {
            RobotBaseActivity baseActivity = (RobotBaseActivity) mContext;
            if (!EmptyUtil.isEmpty(baseActivity) && baseActivity.isMeResumed()) {
                DialogRobotFactory.createFullSvgaDialog(baseActivity, AnimFileName.EMOJI_XUANYUN, new DialogRobotFactory.OnSVGACallBack() {
                    @Override
                    public void callBack(SVGAImageView svgaImageView) {
                        LogUtil.i(TAG, "眩晕");
                    }
                });
                TtsHelper.getInstance().start(new TtsModel("请尽快把我放到固定位置哦"), null);
            }
        }
    }


    private void initTimeReceiver() {
        createTimeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        mContext.registerReceiver(broadcastReceiver, filter);
        //广播的注册，其中Intent.ACTION_TIME_CHANGED代表时间设置变化的时候会发出该广播
    }

    private void initBatReceiver() {
        createBatReceiver();
        mIvBatteryIng.setVisibility(mIvBatteryIngVisible ? VISIBLE : GONE);
        mContext.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void initWifiReceiver() {
        createWifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mWifiStateReceiver, filter);
    }

    private void initBlueReceiver() {
        createBlueReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
        filter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
        mContext.registerReceiver(mBluecastReceiver, filter);
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
        /**
         * 蓝牙
         */
        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        boolean blueOpen = blueadapter != null && blueadapter.isEnabled();
        mIvTitleBarBlue.setVisibility(blueOpen ? VISIBLE : GONE);
    }


    private void updateBattery(int level) {
        if (RobotUtil.isInstallRobot()) {
            if (SerialManager.getInstance().isNoCharging()) {
                if (level < 0) {
                    RobotUtil.shutdownByActivity(mContext);
                } else if (TitleBarView.level > 10 && level <= 10) {
                    lowBatteryHint();
                }
            }
        }
        if (level > 100) {
            level = 100;
        } else if (level < 1) {
            level = 1;
        }
        TitleBarView.level = level;
        // 值太小看不到进度
        mPbBatteryProgress.setProgress(level < 20 ? 20 : level);
        mTvProgress.setText(level + "%");
    }

    private void lowBatteryHint() {
        if (mContext instanceof RobotBaseActivity) {
            RobotBaseActivity baseActivity = (RobotBaseActivity) mContext;
            if (!EmptyUtil.isEmpty(baseActivity) && baseActivity.isMeResumed()) {
                DialogRobotFactory.createFullSvgaDialog(baseActivity, AnimFileName.EMOJI_XUANYUN, new DialogRobotFactory.OnSVGACallBack() {
                    @Override
                    public void callBack(SVGAImageView svgaImageView) {
                        LogUtil.i(TAG, "眩晕");
                    }
                });
                TtsHelper.getInstance().start(new TtsModel("电池电量底，为防止自动关机，请为我充电"), null);
            }
        }

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
                        case BATTERY_STATUS_FULL:
                            // 充满电
                        case BATTERY_STATUS_CHARGING:
                            // 充电中
                            mIvBatteryIng.setVisibility(VISIBLE);
                            mIvBatteryIngVisible = true;
                            break;
                        case BATTERY_STATUS_UNKNOWN:
                            // 未知
                        case BATTERY_STATUS_NOT_CHARGING:
                            // 未充电
                        case BATTERY_STATUS_DISCHARGING:
                            // 放电中
                            mIvBatteryIngVisible = false;
                            if (mIvBatteryIng.getVisibility() == VISIBLE) {
                                mIvBatteryIng.setVisibility(GONE);
                                robotNoCharge(mContext);
                            }
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
                    if (mIvTitleBarWifi.getVisibility() == VISIBLE) {
                        mTvTitleBarTime.setText(HourUtil.getHHMM(System.currentTimeMillis()));
                    }
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
                    switch (action) {
                        case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                        case BluetoothDevice.ACTION_ACL_CONNECTED:
                        case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                        case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                            notifyBlueIconState();
                            break;
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            notifyBlueIconState();
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

    private void notifyBlueIconState() {
        if (mIvTitleBarBlue != null) {
            if (BleManager.getInstance().isConnected()) {
                mIvTitleBarBlue.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
            } else {
                mIvTitleBarBlue.setImageResource(R.drawable.ic_baseline_bluetooth_24);
            }
        }
    }


}
