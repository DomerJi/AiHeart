package com.thfw.robotheart.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

/**
 * Author:pengs
 * Date: 2021/11/17 9:25
 * Describe:Todo
 */
public class TitleBarView extends LinearLayout implements TimingHelper.WorkListener {

    private static final String TAG = TitleBarView.class.getSimpleName();

    private ImageView mIvTitleBarWifi;
    private TextView mTvTitleBarTime;
    private FrameLayout mFlBattery;
    private ImageView mIvBatteryProgress;
    private ProgressBar mPbBatteryProgress;
    private TextView mTvProgress;
    private Context mContext;
    private BroadcastReceiver mBatInfoReceiver;
    private BroadcastReceiver mWifiStateReceiver;
    private static volatile int level;


    public TitleBarView(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public TitleBarView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TitleBarView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        mContext = getContext();
        setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        inflate(mContext, R.layout.layout_titlebar_view, this);
        initView();
        initReceiver();
    }

    private void initView() {
        mIvTitleBarWifi = (ImageView) findViewById(R.id.iv_titleBar_wifi);
        mTvTitleBarTime = (TextView) findViewById(R.id.tv_titleBar_time);
        mFlBattery = (FrameLayout) findViewById(R.id.fl_battery);
        mIvBatteryProgress = (ImageView) findViewById(R.id.iv_battery_progress);
        mPbBatteryProgress = (ProgressBar) findViewById(R.id.pb_battery_progress);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);
        if (level > 0) {
            updateBattery(level);
        }
        mTvTitleBarTime.setText(HourUtil.getHHMM(System.currentTimeMillis()));
        mIvTitleBarWifi.setVisibility(NetworkUtil.isWifiConnected(mContext) ? VISIBLE : INVISIBLE);
    }

    private void updateBattery(int level) {
        mPbBatteryProgress.setProgress(level);
        mTvProgress.setText(level + "%");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mContext.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mWifiStateReceiver, filter);

        TimingHelper.getInstance().addWorkArriveListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(this.mWifiStateReceiver);
        mContext.unregisterReceiver(this.mBatInfoReceiver);
        TimingHelper.getInstance().removeWorkArriveListener(this);
    }

    @Override
    public void onArrive() {
        if (mTvTitleBarTime != null) {
            mTvTitleBarTime.setText(HourUtil.getHHMM(System.currentTimeMillis()));
        }
    }

    @Override
    public WorkInt workInt() {
        return WorkInt.TIME;
    }

    private void initReceiver() {

        if (mBatInfoReceiver == null) {
            mBatInfoReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    LogUtil.d(TAG, "level = " + level);
                    updateBattery(level);
                }
            };
        }

        if (mWifiStateReceiver == null) {
            mWifiStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mIvTitleBarWifi.setVisibility(NetworkUtil.isWifiConnected(mContext) ? VISIBLE : INVISIBLE);
                }
            };
        }
    }


}
