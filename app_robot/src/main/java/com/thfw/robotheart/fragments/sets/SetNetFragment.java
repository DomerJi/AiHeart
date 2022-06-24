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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.adapter.WifiAdapter;
import com.thfw.robotheart.util.WifiHelper;

import java.util.ArrayList;
import java.util.Iterator;
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
    private ImageView mIvRescan;

    List<ScanResult> newResults = new ArrayList<>();

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
        mIvRescan = (ImageView) findViewById(R.id.iv_rescan);
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
                    WifiHelper.get().enableWifi();
                    WifiHelper.get().enableWifi(new WifiStateListener() {
                        @Override
                        public void isSuccess(boolean isSuccess) {
                            mIvRescan.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
                            if (isSuccess) {
                                onScanWifi();
                            } else {
                                mSwitchWifi.setChecked(false);
                                mPbLoading.setVisibility(View.GONE);
                                mWifiList.setVisibility(View.GONE);
                                WifiHelper.get().disableWifi();
                            }
                        }
                    });
                } else {
                    mIvRescan.clearAnimation();
                    mPbLoading.setVisibility(View.GONE);
                    mWifiList.setVisibility(View.GONE);
                    WifiHelper.get().disableWifi();
                }
                HandlerUtil.getMainHandler().postDelayed(() -> {
                    boolean enabled = mWifiManager != null && mWifiManager.isWifiEnabled();
                    mSwitchWifi.setChecked(enabled);
                }, 300);

            }
        });
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        boolean enabled = mWifiManager != null && mWifiManager.isWifiEnabled();
        setWifiAdapter();
        Log.i(TAG, "isWifiEnabled =  " + enabled);
        mSwitchWifi.setChecked(enabled);
        mIvRescan.setVisibility(enabled ? View.VISIBLE : View.GONE);
        mIvRescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanWifi();
                Log.i(TAG, "mIvRescan click ++++++++++++");
            }
        });
    }

    private void reScanAnimStart() {
        if (mIvRescan == null) {
            return;
        }
        mIvRescan.clearAnimation();
        RotateAnimation rotateAnimation = new RotateAnimation(0, 1080,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(3000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        mIvRescan.startAnimation(rotateAnimation);
    }

    private void setWifiAdapter() {
        wifiAdapter = new WifiAdapter(newResults);
        wifiAdapter.setWifiManager(mWifiManager);
        wifiAdapter.setOnWifiItemListener(new WifiAdapter.OnWifiItemListener() {
            @Override
            public void onItemClick(ScanResult scanResult, String passType, int position) {

                wifiAdapter.setSsidIng(null);
                if (TextUtils.isEmpty(passType)) {
                    WifiHelper.get()
                            .connectWith(scanResult.SSID, "")
                            .setTimeout(10000).onConnectionResult(new ConnectionSuccessListener() {
                        @Override
                        public void success() {
                            wifiAdapter.notifyDataSetChanged();
                            ToastUtil.show("成功链接至-" + scanResult.SSID);
                        }

                        @Override
                        public void failed(@NonNull ConnectionErrorCode errorCode) {
                            ToastUtil.show("连接失败");
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
                        wifiAdapter.setSsidIng(scanResult);
                        wifiAdapter.notifyDataSetChanged();
                        mWifiManager.updateNetwork(configuration);
                        mWifiManager.enableNetwork(configuration.networkId,
                                true);
                        delayScanWifi();
                        wifiAdapter.setSsidIng(null);
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
    }

    @Override
    public void initData() {
        onScanWifi();
    }


    private void onScanWifi() {
        mWifiList.setVisibility(View.VISIBLE);
        if (mWifiList.getAdapter() == null || mWifiList.getAdapter().getItemCount() == 0) {
            mPbLoading.setVisibility(View.VISIBLE);
            mTvHint.setVisibility(View.GONE);
        } else {
            reScanAnimStart();
        }
        WifiHelper.get().scanWifi(this::getScanResults).start();
    }

    /**
     * 去除名称为空的wifi
     *
     * @param results
     */
    private void removeNullNameWifi(final List<ScanResult> results) {

        if (results != null) {
            Iterator<ScanResult> iterator = results.iterator();
            while (iterator.hasNext()) {
                ScanResult scanResult = iterator.next();
                String ssid = null;
                if (scanResult.SSID != null) {
                    ssid = scanResult.SSID.trim().replaceAll(" ", "");
                }
                if (TextUtils.isEmpty(ssid)) {
                    iterator.remove();
                }
            }
        }
    }


    private void getScanResults(@NonNull final List<ScanResult> results) {
//        removeNullNameWifi(results);
        mPbLoading.setVisibility(View.GONE);
        if (results.isEmpty()) {
            if (wifiAdapter != null && wifiAdapter.getItemCount() == 0) {
                mTvHint.setVisibility(View.VISIBLE);
                mTvHint.setText("没有扫描到Wifi");
            }
            return;
        }

        newResults.clear();
        int len = results.size();
        for (int i = 0; i < len; i++) {
            ScanResult scanResult = results.get(i);
            boolean connected = WifiHelper.get().isWifiConnected(scanResult.SSID);
            if (connected) {
                newResults.add(0, scanResult);
            } else {
                if (Util.isSavePassWord(mWifiManager, scanResult.SSID)) {
                    if (newResults.isEmpty()) {
                        newResults.add(0, scanResult);
                    } else {
                        newResults.add(1, scanResult);
                    }
                } else {
                    newResults.add(scanResult);
                }
            }
        }

        mTvHint.setVisibility(View.GONE);
        if (wifiAdapter != null) {
            wifiAdapter.notifyDataSetChanged();
        }
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
                handler.sendEmptyMessageDelayed(0, 1500);
            }
        };
        handler.sendEmptyMessageDelayed(0, 800);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WifiHelper.release();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }


}
