package com.thfw.robotheart.fragments.sets;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.me.EditInfoActivity;
import com.thfw.robotheart.adapter.BleAdapter;
import com.thfw.robotheart.robot.BleDevice;
import com.thfw.robotheart.robot.BleManager;
import com.thfw.robotheart.robot.BleScanCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
    private Handler mHandler;
    private TextView mTvScaningHint;
    private ImageView mIvRescan;
    private TextView mTvDeviceName;
    private CheckBox mCbShowNullName;

    private List<BleDevice> scanResultList = new ArrayList<>();

    private boolean showNullName = false;

    public BleScanCallback mBleScanCallback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            mIvRescan.clearAnimation();
            if (!mSwitchBlue.isChecked()) {
                return;
            }
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
            if (mBleAdapter != null) {
                mBleAdapter.notifyDataSetChanged();
            }
            if (success) {
                reScanAnimStart();
            } else {
                mIvRescan.clearAnimation();
            }

            if (!EmptyUtil.isEmpty(BleManager.getInstance().getAllConnectedDevice())) {
                notifyBlue(BleManager.getInstance().getAllConnectedDevice());
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
            if (!showNullName && TextUtils.isEmpty(bleDevice.getName())) {
                return;
            }
            if (mBleAdapter.getDataList() != null) {
                for (BleDevice device : mBleAdapter.getDataList()) {
                    if (device.getMac().equals(bleDevice.getMac())) {
                        return;
                    }
                }
            }
            notifyBlue(bleDevice);
            if (mBleAdapter.getItemCount() == 0) {
                mTvScaningHint.setVisibility(View.VISIBLE);
                mTvScaningHint.setText("正在搜索");
            } else {
                mTvScaningHint.setVisibility(View.GONE);
            }
        }

    };

    public void notifyBlue(List<BleDevice> bleDevices) {
        List<String> bleMacs = new ArrayList<>();
        for (BleDevice bleDevice : scanResultList) {
            bleMacs.add(bleDevice.getMac());
        }
        for (BleDevice ble : bleDevices) {
            if (!bleMacs.contains(ble.getMac())) {
                scanResultList.add(ble);
            }
        }
        sort();
        mBleAdapter.notifyDataSetChanged();
    }

    public void notifyBlue(BleDevice bleDevice) {
        for (BleDevice ble : scanResultList) {
            if (bleDevice.getMac().equals(ble.getMac())) {
                return;
            }
        }
        scanResultList.add(bleDevice);
        sort();
        mBleAdapter.notifyDataSetChanged();
    }

    private void sort() {
        BluetoothDevice bindDevice = BleManager.getInstance().getPairBLEAndConnectBLE();
        Collections.sort(scanResultList, new Comparator<BleDevice>() {
            @Override
            public int compare(BleDevice o1, BleDevice o2) {
                if (bindDevice != null) {
                    if (bindDevice.getAddress().contains(o1.getMac())) {
                        return -1;
                    }
                }
                if (BleManager.getInstance().isAudioBlue(o1.getDevice())) {
                    if (BleManager.getInstance().isAudioBlue(o2.getDevice())) {
                        return o1.getRssi() > o2.getRssi() ? -1 : o1.getRssi() == o2.getRssi() ? 0 : 1;
                    } else {
                        return -1;
                    }
                } else {

                    if (TextUtils.isEmpty(o1.getName())) {
                        if (TextUtils.isEmpty(o2.getName())) {
                            return o1.getRssi() > o2.getRssi() ? -1 : o1.getRssi() == o2.getRssi() ? 0 : 1;
                        } else {
                            return 1;
                        }
                    } else {
                        if (TextUtils.isEmpty(o2.getName())) {
                            return -1;
                        } else {
                            return o1.getRssi() > o2.getRssi() ? -1 : o1.getRssi() == o2.getRssi() ? 0 : 1;
                        }
                    }
                }


            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBlue();
        mHandler = new Handler(Looper.getMainLooper());
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

        mTvMeBleName.setOnClickListener(v -> {
            EditInfoActivity.startActivity(mContext, EditInfoActivity.EditType.BLUE_NAME, mTvMeBleName.getText().toString());
        });
        mIvNameArrowRight.setOnClickListener(v -> {
            EditInfoActivity.startActivity(mContext, EditInfoActivity.EditType.BLUE_NAME, mTvMeBleName.getText().toString());
        });
        mTvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        mCbShowNullName = (CheckBox) findViewById(R.id.cb_show_null_name);
        mCbShowNullName.setChecked(false);
        mCbShowNullName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showNullName = isChecked;
                if (!showNullName) {
                    Iterator<BleDevice> bleDeviceIterator = scanResultList.iterator();
                    while (bleDeviceIterator.hasNext()) {
                        if (TextUtils.isEmpty(bleDeviceIterator.next().getName())) {
                            bleDeviceIterator.remove();
                        }
                    }
                    if (mBleAdapter != null) {
                        mBleAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
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
            syncScan();
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
                        BleManager.getInstance().newCancelScan();
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
        mBleAdapter = new BleAdapter(scanResultList);
        mRvBlue.setAdapter(mBleAdapter);
        mBleAdapter.setOnRvItemListener(new OnRvItemListener<BleDevice>() {
            @Override
            public void onItemClick(List<BleDevice> list, int position) {
                if (position < 0 || position >= list.size()) {
                    return;
                }
                BleDevice bleDevice = list.get(position);
                if (BleManager.getInstance().isConnected(bleDevice.getDevice())) {
                    ToastUtil.show("取消配对");
                    BleManager.getInstance().unpairDevice(bleDevice.getDevice());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mBleAdapter != null) {
                                mBleAdapter.notifyDataSetChanged();
                            }
                            if (BleManager.getInstance().getPairBLEAndConnectBLE() != null) {
                                mHandler.postDelayed(this, 1500);
                            }
                        }
                    }, 1000);
                } else {
                    LogUtil.d(TAG, "connect ==========================");
                    BleManager.getInstance().newCancelScan();
                    onBleConnect(bleDevice);
                }
            }
        });
    }

    private void onBleConnect(BleDevice bleDevice) {
//        if (bleDevice.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
//            ToastUtil.show("BOND_BONDED");
//            bleDevice.getDevice().connectGatt(mContext, false, new BluetoothGattCallback() {
//                @Override
//                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//                    super.onConnectionStateChange(gatt, status, newState);
//                    LogUtil.d(TAG, "newState -> " + newState);
//                    if (mBleAdapter != null) {
//                        mBleAdapter.notifyDataSetChanged();
//                    }
//                }
//            });
//            return;
//        }
        ToastUtil.show("BOND_UN_BONDED");
        BleManager.getInstance().connectDevice(bleDevice.getDevice());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBleAdapter != null) {
                    mBleAdapter.notifyDataSetChanged();
                }
                if (BleManager.getInstance().getPairBLEAndConnectBLE() == null) {
                    mHandler.postDelayed(this, 1500);
                }
            }
        }, 1000);

    }

    private void syncScan() {
        if (!mSwitchBlue.isChecked()) {
            mIvRescan.clearAnimation();
            return;
        }

        if (BleManager.getInstance().isBlueEnable()) {
            try {
                BleManager.getInstance().newCancelScan();
            } catch (Exception e) {
                LogUtil.e(TAG, "BleManager.getInstance().cancelScan() e = " + e.getMessage());
            } finally {
                BleManager.getInstance().newScan(mBleScanCallback);
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

    BluetoothHeadset bluetoothHeadset;

    private void initBlue() {
        if (initScanRule) {
            return;
        }
        initScanRule = true;

        BleManager.getInstance().init(getActivity().getApplication());


        // Get the default adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothProfile.ServiceListener profileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    LogUtil.d(TAG, "onServiceConnected profile = " + profile);
                    bluetoothHeadset = (BluetoothHeadset) proxy;
                }
            }

            public void onServiceDisconnected(int profile) {
                LogUtil.d(TAG, "onServiceDisconnected profile = " + profile);
                if (profile == BluetoothProfile.HEADSET) {
                    bluetoothHeadset = null;
                }
            }
        };

        // Establish connection to the proxy.
        bluetoothAdapter.getProfileProxy(mContext, profileListener, BluetoothProfile.HEADSET);

        // ... call functions on bluetoothHeadset

        // Close proxy connection after use.
//        bluetoothAdapter.closeProfileProxy(bluetoothHeadset);
    }

    @Override
    public void onDestroyView() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        BleManager.getInstance().newDestroy();
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == EditInfoActivity.EditType.BLUE_NAME.getType()) {
            String blueName = data.getStringExtra(EditInfoActivity.KEY_RESULT);
            if (!TextUtils.isEmpty(blueName)) {
                blueName = blueName.replaceAll(" ", "");
                if (!TextUtils.isEmpty(blueName)) {
                    BleManager.getInstance().getBluetoothAdapter().setName(blueName);
                    mTvMeBleName.setText(blueName);
                    ToastUtil.showLong("成功修改为：" + blueName);
                } else {
                    ToastUtil.showLong("名称不合法");
                }
            } else {
                ToastUtil.showLong("名称不合法");
            }

        }
    }
}
