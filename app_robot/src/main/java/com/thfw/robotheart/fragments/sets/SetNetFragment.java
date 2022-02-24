package com.thfw.robotheart.fragments.sets;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thanosfisherman.wifiutils.Logger;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener;
import com.thfw.base.ContextApp;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.WifiAdapter;
import com.thfw.robotheart.activitys.RobotBaseFragment;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:Todo
 */
public class SetNetFragment extends RobotBaseFragment {

    private RelativeLayout mRlWifi;
    private Switch mSwitchWifi;
    private RecyclerView mWifiList;
    private ProgressBar mPbLoading;
    private TextView mTvHint;
    private WifiInputFragment mWifiInputFragment;
    private WifiAdapter wifiAdapter;
    private WifiManager mWifiManager;
    private Handler handler;


    @Override
    public int getContentView() {
        return R.layout.fragment_set_net;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRlWifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        mSwitchWifi = (Switch) findViewById(R.id.switch_wifi);
        mWifiList = (RecyclerView) findViewById(R.id.wifi_list);
        mWifiList.setLayoutManager(new LinearLayoutManager(mContext));
        mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        WifiUtils.enableLog(true);
        WifiUtils.forwardLog(new Logger() {
            @Override
            public void log(int priority, String tag, String message) {
                Log.d(tag + "jsp", message);
            }
        });
        mSwitchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                delayScanWifi();
                if (isChecked) {
                    WifiUtils.withContext(ContextApp.get()).enableWifi();
                    WifiUtils.withContext(ContextApp.get()).enableWifi(new WifiStateListener() {
                        @Override
                        public void isSuccess(boolean isSuccess) {
                            if (isSuccess) {
                                onScanWifi();
                            } else {
                                mSwitchWifi.setChecked(false);
                                mPbLoading.setVisibility(View.GONE);
                                mWifiList.setVisibility(View.GONE);
                                WifiUtils.withContext(ContextApp.get()).disableWifi();
                            }
                        }
                    });
                } else {
                    mPbLoading.setVisibility(View.GONE);
                    mWifiList.setVisibility(View.GONE);
                    WifiUtils.withContext(ContextApp.get()).disableWifi();
                }
            }
        });
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        boolean enabled = mWifiManager != null && mWifiManager.isWifiEnabled();
        Log.i(TAG, "isWifiEnabled =  " + enabled);
        mSwitchWifi.setChecked(enabled);
    }

    @Override
    public void initData() {
        onScanWifi();
    }


    private void onScanWifi() {
        mPbLoading.setVisibility(View.VISIBLE);
        mWifiList.setVisibility(View.VISIBLE);
        mWifiList.setAdapter(new WifiAdapter(null));
        WifiUtils.withContext(mContext).scanWifi(this::getScanResults).start();
    }

    private void getScanResults(@NonNull final List<ScanResult> results) {
        mPbLoading.setVisibility(View.GONE);
        if (results.isEmpty()) {
            mTvHint.setVisibility(View.VISIBLE);
            if (Util.isSystemApp(mContext.getPackageName())) {
                mTvHint.setText("没有扫描到Wifi");
            } else {
                mTvHint.setText("App不是系统级的，没有办法扫描无线网络，请到本设备的设置中连接无线网络。");
            }
            return;
        }
        mTvHint.setVisibility(View.GONE);
        wifiAdapter = new WifiAdapter(results);
        wifiAdapter.setOnWifiItemListener(new WifiAdapter.OnWifiItemListener() {
            @Override
            public void onItemClick(ScanResult scanResult, String passType, int position) {
                if (TextUtils.isEmpty(passType)) {
                    WifiUtils.withContext(mContext)
                            .connectWith(scanResult.SSID, "")
                            .setTimeout(10000).onConnectionResult(new ConnectionSuccessListener() {
                        @Override
                        public void success() {
                            wifiAdapter.notifyDataSetChanged();
                            ToastUtil.show("成功链接至-" + scanResult.SSID);
                        }

                        @Override
                        public void failed(@NonNull ConnectionErrorCode errorCode) {
                            ToastUtil.show("链接失败");
                        }
                    }).start();
                    return;
                    // 已经有密码
                } else if (Util.isSavePassWord(mWifiManager, scanResult.SSID)) {
                    Log.i(TAG, "已经有密码");
                    WifiConfiguration configuration = Util.getConfig(mWifiManager, scanResult.SSID);
                    if (configuration != null) {
                        Log.i(TAG, "已经有密码 - 开始连接");
                        // 连接配置好的指定ID的网络
                        mWifiManager.enableNetwork(configuration.networkId,
                                true);
                        return;
                    }

                }
                if (mWifiInputFragment != null) {
                    return;
                }
                mWifiInputFragment = new WifiInputFragment(scanResult, passType, position);
                mWifiInputFragment.setCompleteInputListener(new WifiInputFragment.OnCompleteInputListener() {
                    @Override
                    public void onComplete() {
                        removeFragment();
                    }
                });
                getParentFragmentManager().beginTransaction()
                        .add(R.id.fl_input_pass, mWifiInputFragment).commit();
            }
        });
        mWifiList.setAdapter(wifiAdapter);
        Log.i(TAG, "GOT SCAN RESULTS " + results);
    }

    public void removeFragment() {
        if (mWifiInputFragment != null) {
            getParentFragmentManager().beginTransaction().remove(mWifiInputFragment).commit();
            mWifiInputFragment = null;
        }
        if (wifiAdapter != null) {
            wifiAdapter.notifyDataSetChanged();
        }
    }

    public boolean wifiInputFragment() {
        return mWifiInputFragment != null;
    }

    private void delayScanWifi() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (mSwitchWifi == null || !mSwitchWifi.isChecked()) {
            return;
        }
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (wifiAdapter != null) {
                    wifiAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessageDelayed(0, 3000);
            }
        };
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }


}
