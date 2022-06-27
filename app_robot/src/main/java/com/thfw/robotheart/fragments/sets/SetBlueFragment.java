package com.thfw.robotheart.fragments.sets;

import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.adapter.BleAdapter;
import com.thfw.ui.dialog.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:Todo
 */
public class SetBlueFragment extends RobotBaseFragment {

    private RelativeLayout mRlBlue;
    private Switch mSwitchBlue;
    private ImageView mIvNameArrowRight;
    private RecyclerView mRvBlue;
    private boolean initScanRule;
    private BleAdapter mBleAdapter;
    private RelativeLayout mRlMeDevice;
    private TextView mTvMeBleName;
    public BleScanCallback mBleScanCallback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            mIvRescan.clearAnimation();
            if (!mSwitchBlue.isChecked()) {
                return;
            }
            if (BleManager.getInstance().getAllConnectedDevice() != null) {
                int size = BleManager.getInstance().getAllConnectedDevice().size();
                ToastUtil.show("all connect size = " + size);
            }
            scanResultList.addAll(BleManager.getInstance().getAllConnectedDevice());
            List<BleDevice> bleDevices = new ArrayList<>();
            for (BleDevice bleDevice : scanResultList) {
//                if (TextUtils.isEmpty(bleDevice.getName())) {
//                    continue;
//                }
                bleDevices.add(bleDevice);
            }
            LogUtil.d(TAG, "bleDevices.size() -> " + bleDevices.size());
            mBleAdapter.setDataListNotify(bleDevices);
            if (mBleAdapter.getItemCount() == 0) {
                mTvScaningHint.setVisibility(View.VISIBLE);
                mTvScaningHint.setText("没有搜索到蓝牙设备");
            } else {
                mTvScaningHint.setVisibility(View.GONE);
            }
        }

        @Override
        public void onScanStarted(boolean success) {
            if (!mSwitchBlue.isChecked()) {
                return;
            }
            if (success) {
                reScanAnimStart();
            } else {
                mIvRescan.clearAnimation();
            }
            if (mBleAdapter.getItemCount() == 0) {
                mTvScaningHint.setVisibility(View.VISIBLE);
                mTvScaningHint.setText("正在搜索");
            } else {
                mTvScaningHint.setVisibility(View.GONE);
            }
        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            if (!mSwitchBlue.isChecked()) {
                return;
            }
//            if (TextUtils.isEmpty(bleDevice.getName())) {
//                return;
//            }
            if (mBleAdapter.getDataList() != null) {
                for (BleDevice device : mBleAdapter.getDataList()) {
                    if (device.getMac().equals(bleDevice.getMac())) {
                        return;
                    }
                }
            }
            mBleAdapter.addDataNotify(bleDevice);
            if (mBleAdapter.getItemCount() == 0) {
                mTvScaningHint.setVisibility(View.VISIBLE);
                mTvScaningHint.setText("正在搜索");
            } else {
                mTvScaningHint.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLeScan(BleDevice bleDevice) {
            super.onLeScan(bleDevice);
            if (!mSwitchBlue.isChecked()) {
                return;
            }
//            if (TextUtils.isEmpty(bleDevice.getName())) {
//                return;
//            }
            if (mBleAdapter.getDataList() != null) {
                for (BleDevice device : mBleAdapter.getDataList()) {
                    if (device.getMac().equals(bleDevice.getMac())) {
                        return;
                    }
                }
            }
            mBleAdapter.addDataNotify(bleDevice);
            if (mBleAdapter.getItemCount() == 0) {
                mTvScaningHint.setVisibility(View.VISIBLE);
                mTvScaningHint.setText("正在搜索");
            } else {
                mTvScaningHint.setVisibility(View.GONE);
            }
        }
    };
    private TextView mTvScaningHint;
    private ImageView mIvRescan;

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBlue();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_set_blue;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }


    @Override
    public void initView() {
        if (!BleManager.getInstance().isSupportBle()) {
            ToastUtil.show("您的设备不支持蓝牙操作");
        }
        mRlBlue = (RelativeLayout) findViewById(R.id.rl_blue);
        mSwitchBlue = (Switch) findViewById(R.id.switch_blue);
        mIvNameArrowRight = (ImageView) findViewById(R.id.iv_name_arrow_right);
        mRvBlue = (RecyclerView) findViewById(R.id.rvBlue);
        mRvBlue.setLayoutManager(new LinearLayoutManager(mContext));

        mRlMeDevice = (RelativeLayout) findViewById(R.id.rl_me_device);
        mTvMeBleName = (TextView) findViewById(R.id.tv_me_ble_name);
        mIvRescan = (ImageView) findViewById(R.id.iv_rescan);

        mTvScaningHint = (TextView) findViewById(R.id.tv_scaning_hint);
    }


    private void reScanAnimStart() {
        if (mIvRescan == null) {
            return;
        }
        mIvRescan.setVisibility(View.VISIBLE);
        mIvRescan.clearAnimation();
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(20);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        mIvRescan.startAnimation(rotateAnimation);
    }

    @Override
    public void initData() {

        initAdapter();
        mIvRescan.setOnClickListener(v -> {
            syncScan();
        });
        mSwitchBlue.setChecked(BleManager.getInstance().isBlueEnable());
        if (!mSwitchBlue.isChecked()) {
            mTvScaningHint.setText("蓝牙已关闭");
        }
        if (BleManager.getInstance().isBlueEnable()) {
            mIvRescan.setVisibility(View.VISIBLE);
            BleManager.getInstance().scan(mBleScanCallback);
        } else {
            mIvRescan.setVisibility(View.GONE);
            mIvRescan.clearAnimation();
        }

        mTvMeBleName.setText(BleManager.getInstance().getBluetoothAdapter().getName());

        mSwitchBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BleManager.getInstance().enableBluetooth();
                    mIvRescan.setVisibility(View.VISIBLE);
                    syncScan();
                    mTvScaningHint.setText("正在搜索");
                } else {
                    BleManager.getInstance().disableBluetooth();
                    try {
                        BleManager.getInstance().cancelScan();
                    } catch (Exception e) {
                        LogUtil.e(TAG, "BleManager.getInstance().cancelScan()2 e = " + e.getMessage());
                    }
                    mBleAdapter.setDataListNotify(null);
                    mIvRescan.setVisibility(View.GONE);
                    mIvRescan.clearAnimation();
                    mTvScaningHint.setText("蓝牙已关闭");
                    mTvScaningHint.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initAdapter() {
        mBleAdapter = new BleAdapter(null);
        mRvBlue.setAdapter(mBleAdapter);
        mBleAdapter.setOnRvItemListener(new OnRvItemListener<BleDevice>() {
            @Override
            public void onItemClick(List<BleDevice> list, int position) {
                BleDevice bleDevice = list.get(position);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    ToastUtil.show("已连接");
                    mBleAdapter.notifyDataSetChanged();
                } else {
                    LogUtil.d(TAG, "connect ==========================");
                    onBleConnect(bleDevice);
                }
            }
        });
    }

    private void onBleConnect(BleDevice bleDevice) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                LogUtil.d(TAG, "handleMessage ====================== msg.what = " + msg.what);
                mBleAdapter.notifyDataSetChanged();
            }
        };
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {

            @Override
            public void onStartConnect() {
                LoadingDialog.show(getActivity(), "正在连接\n"
                        + bleDevice.getName());
                handler.sendEmptyMessageDelayed(1, 100);
                LogUtil.d(TAG, "onStartConnect ==========================");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                LoadingDialog.hide();
                handler.sendEmptyMessageDelayed(1, 100);
                LogUtil.d(TAG, "onConnectFail ==========================");
                ToastUtil.showLong("连接失败");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LoadingDialog.hide();
                handler.sendEmptyMessageDelayed(1, 100);
                ToastUtil.showLong("连接成功");
                LogUtil.d(TAG, "onConnectSuccess ==========================");
                // 搜索指定 Characteristic UUID，并 Write
//                searchSpecifiedCharacteristicUuid(bleDevice, WRITE_CHARACTERISTIC_UUID);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                LoadingDialog.hide();
                handler.sendEmptyMessageDelayed(1, 100);
                LogUtil.d(TAG, "onDisConnected ==========================");
                ToastUtil.showLong("断开连接");
            }
        });

        handler.sendEmptyMessageDelayed(1, 200);
    }

    private void syncScan() {
        if (!mSwitchBlue.isChecked()) {
            mIvRescan.clearAnimation();
            return;
        }

        if (BleManager.getInstance().isBlueEnable()) {
            try {
                BleManager.getInstance().cancelScan();
            } catch (Exception e) {
                LogUtil.e(TAG, "BleManager.getInstance().cancelScan() e = " + e.getMessage());
            } finally {
                BleManager.getInstance().scan(mBleScanCallback);
            }
        } else {
            HandlerUtil.getMainHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    syncScan();
                }
            }, 500);
        }
    }

    private void initBlue() {
        if (initScanRule) {
            return;
        }
        initScanRule = true;

        BleManager.getInstance().init(getActivity().getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)         // 只扫描指定广播名的设备，可选
//                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(5000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);


    }

}
