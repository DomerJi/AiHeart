package com.thfw.robotheart.fragments.sets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
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
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.adapter.WifiAdapter;
import com.thfw.robotheart.util.WifiHelper;

import org.jetbrains.annotations.NotNull;

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

    private List<ScanResult> newResults = new ArrayList<>();
    private BroadcastReceiver mWifiStateReceiver;
    private ArrayList<NetworkInfo.DetailedState> mWifiStateList = new ArrayList<>();
    private ScanResult startScanResult;
    private String startSSID;

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
                }, 800);

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
        registerWifi();
        delayScanWifi();
    }

    private void registerWifi() {
        createWifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mWifiStateReceiver, filter);
    }

    private void createWifiReceiver() {


        if (mWifiStateReceiver == null) {
            mWifiStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent != null) {
                        LogUtil.d(TAG, "action = " + intent.getAction());
                    } else {
                        return;
                    }
                    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                        Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        if (null != parcelableExtra) {
                            NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                            NetworkInfo.State state = networkInfo.getState();
                            NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
                            String extra = networkInfo.getExtraInfo();
                            if (extra != null) {
                                extra = extra.replaceAll("\"", "");

                            }
                            LogUtil.d(TAG, "startSSID = " + startSSID + " ; extra = " + extra);
                            if (startSSID != null && startSSID.equals(extra)) {
                                mWifiStateList.add(detailedState);
                                if (state == NetworkInfo.State.CONNECTING) {
                                    wifiAdapter.setSsId(startSSID);
                                } else {
                                    wifiAdapter.setSsId(null);
                                }
                                if (detailedState == NetworkInfo.DetailedState.DISCONNECTED) {
                                    for (NetworkInfo.DetailedState detailedState1 : mWifiStateList) {
                                        if (detailedState1 == NetworkInfo.DetailedState.CONNECTING) {
                                            Util.removeWifiBySsid(mWifiManager, startScanResult.SSID);
                                            ToastUtil.show("????????????,???????????????");
                                            String capabilities = WifiAdapter.getEncrypt(mWifiManager, startScanResult);
                                            openInputPass(startScanResult, capabilities, 0);
                                            break;
                                        }
                                    }
                                    startSSID = null;
                                    wifiAdapter.setSsId(null);
                                } else if (detailedState == NetworkInfo.DetailedState.CONNECTED) {
                                    startSSID = null;
                                    wifiAdapter.setSsId(null);
                                }
                            } else {
                                wifiAdapter.notifySsId(extra);
                            }
                            LogUtil.d(TAG, "networkInfo = " + networkInfo.toString());
                            // ?????????????????????????????????????????????
                            boolean isConnected = (state == NetworkInfo.State.CONNECTED);
                            LogUtil.d(TAG, "isConnected" + isConnected);
                        }
                        if (NetworkUtil.isWifiConnected(mContext)) {
                            if (wifiAdapter != null) {
                                wifiAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            };
        }
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
                if (TextUtils.isEmpty(passType)) {
                    WifiHelper.get()
                            .connectWith(scanResult.SSID, scanResult.BSSID, "")
                            .setTimeout(10000).onConnectionResult(new ConnectionSuccessListener() {
                        @Override
                        public void success() {
                            wifiAdapter.notifyDataSetChanged();
                            ToastUtil.show("???????????????-" + scanResult.SSID);
                        }

                        @Override
                        public void failed(@NonNull ConnectionErrorCode errorCode) {
                            ToastUtil.show("????????????");
                        }
                    }).start();
                    return;
                    // ???????????????
                } else if (Util.isSavePassWord(mWifiManager, scanResult.SSID)) {

                    Log.i(TAG, "???????????????");
                    WifiConfiguration configuration = Util.getConfig(mWifiManager, scanResult.SSID);
                    if (configuration != null) {

                        if (WifiHelper.get().isWifiConnected()) {
                            startScanResult = scanResult;
                            startSSID = startScanResult.SSID;
                            wifiAdapter.setSsId(startSSID);
                            mWifiStateList.clear();
                            WifiHelper.get().disconnect(new DisconnectionSuccessListener() {
                                @Override
                                public void success() {
                                    savePassReConnect(scanResult, configuration);
                                }

                                @Override
                                public void failed(@NonNull @NotNull DisconnectionErrorCode errorCode) {
                                    savePassReConnect(scanResult, configuration);
                                }
                            });
                        } else {
                            savePassReConnect(scanResult, configuration);
                        }
                        return;
                    }
                }
                startScanResult = null;
                startSSID = null;
                openInputPass(scanResult, passType, position);

            }
        });
        mWifiList.setAdapter(wifiAdapter);
    }

    private void savePassReConnect(ScanResult scanResult, WifiConfiguration configuration) {
        Log.i(TAG, "??????????????? - ????????????");
        startScanResult = scanResult;
        startSSID = startScanResult.SSID;
        wifiAdapter.setSsId(startSSID);
        mWifiStateList.clear();
        mWifiManager.updateNetwork(configuration);
        mWifiManager.enableNetwork(configuration.networkId,
                true);
    }

    private void openInputPass(ScanResult scanResult, String passType, int position) {
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
     * ?????????????????????wifi
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
                mTvHint.setText("???????????????Wifi");
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
        hideInput();
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
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (!EmptyUtil.isEmpty(getActivity())) {
                        if (wifiAdapter != null && WifiHelper.get().isWifiConnected()) {
                            wifiAdapter.notifyDataSetChanged();
                        }
                        LogUtil.d(TAG, "handleMessage ====================== ");
                        handler.sendEmptyMessageDelayed(0, 2000);
                    }
                }
            };
        }
        handler.sendEmptyMessageDelayed(0, 1500);
    }

    @Override
    public void onDestroyView() {
        if (mWifiStateReceiver != null) {
            mContext.unregisterReceiver(mWifiStateReceiver);
        }
        super.onDestroyView();
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
