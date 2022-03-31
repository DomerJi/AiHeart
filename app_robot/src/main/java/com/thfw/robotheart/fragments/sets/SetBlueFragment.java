package com.thfw.robotheart.fragments.sets;

import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.adapter.BleAdapter;
import com.thfw.robotheart.view.CustomRefreshLayout;
import com.thfw.ui.widget.LoadingView;

import org.jetbrains.annotations.NotNull;

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
    private CustomRefreshLayout mCustomRefreshLayout;
    private RecyclerView mRvBlue;
    private boolean initScanRule;
    private BleAdapter mBleAdapter;
    private RelativeLayout mRlMeDevice;
    private TextView mTvMeBleName;
    private LoadingView mLoadingView;
    public BleScanCallback mBleScanCallback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            mLoadingView.hide();
            if (!mSwitchBlue.isChecked()) {
                return;
            }
            List<BleDevice> bleDevices = new ArrayList<>();
            for (BleDevice bleDevice : scanResultList) {
                if (bleDevice.getName() != null) {
                    bleDevices.add(bleDevice);
                }
            }
            LogUtil.d(TAG, "bleDevices.size() -> " + bleDevices.size());
            mBleAdapter.setDataListNotify(bleDevices);
            mCustomRefreshLayout.finishRefresh();
        }

        @Override
        public void onScanStarted(boolean success) {
            if (!mSwitchBlue.isChecked()) {
                return;
            }
            if (success) {
                mLoadingView.showLoadingNoText();
            } else {
                mLoadingView.hide();
                mCustomRefreshLayout.finishRefresh();
            }
        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            if (!mSwitchBlue.isChecked()) {
                return;
            }
            if (mBleAdapter.getDataList() != null) {
                for (BleDevice device : mBleAdapter.getDataList()) {
                    if (device.getMac().equals(bleDevice.getMac())) {
                        return;
                    }
                }
            }
            mBleAdapter.addDataNotify(bleDevice);
        }

        @Override
        public void onLeScan(BleDevice bleDevice) {
            super.onLeScan(bleDevice);
            if (!mSwitchBlue.isChecked()) {
                return;
            }
            if (mBleAdapter.getDataList() != null) {
                for (BleDevice device : mBleAdapter.getDataList()) {
                    if (device.getMac().equals(bleDevice.getMac())) {
                        return;
                    }
                }
            }
            mBleAdapter.addDataNotify(bleDevice);
        }
    };

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BleManager.getInstance().init(getActivity().getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(5000);
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
        mCustomRefreshLayout = (CustomRefreshLayout) findViewById(R.id.customRefreshLayout);
        mRvBlue = (RecyclerView) findViewById(R.id.rvBlue);
        mRvBlue.setLayoutManager(new LinearLayoutManager(mContext));

        mRlMeDevice = (RelativeLayout) findViewById(R.id.rl_me_device);
        mTvMeBleName = (TextView) findViewById(R.id.tv_me_ble_name);

        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLoadingView.showLoadingNoText();
    }

    @Override
    public void initData() {

        initAdapter();

        mSwitchBlue.setChecked(BleManager.getInstance().isBlueEnable());
        initBlue();
        if (BleManager.getInstance().isBlueEnable()) {
            BleManager.getInstance().scan(mBleScanCallback);
        } else {
            mLoadingView.hide();
        }

        mTvMeBleName.setText(BleManager.getInstance().getBluetoothAdapter().getName());

        mSwitchBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BleManager.getInstance().enableBluetooth();
                    syncScan();
                } else {
                    BleManager.getInstance().disableBluetooth();
                    BleManager.getInstance().cancelScan();
                    mBleAdapter.setDataListNotify(null);
                    mLoadingView.hide();
                }
            }
        });
    }

    private void initAdapter() {
        mCustomRefreshLayout.setEnableRefresh(true);
        mCustomRefreshLayout.setEnableLoadMore(false);
        mCustomRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                syncScan();
            }
        });
        mBleAdapter = new BleAdapter(null);
        mRvBlue.setAdapter(mBleAdapter);
        mBleAdapter.setOnRvItemListener(new OnRvItemListener<BleDevice>() {
            @Override
            public void onItemClick(List<BleDevice> list, int position) {
                BleDevice bleDevice = list.get(position);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    ToastUtil.show("已连接");
                } else {
                    LogUtil.d(TAG, "connect ==========================");
                    BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                super.handleMessage(msg);

                                LogUtil.d(TAG, "handleMessage ====================== msg.what = " + msg.what);
                                mBleAdapter.notifyDataSetChanged();
                            }
                        };

                        @Override
                        public void onStartConnect() {
                            handler.sendEmptyMessageDelayed(1, 300);
                            LogUtil.d(TAG, "onStartConnect ==========================");
                        }

                        @Override
                        public void onConnectFail(BleDevice bleDevice, BleException exception) {
                            handler.sendEmptyMessageDelayed(1, 300);
                            LogUtil.d(TAG, "onConnectFail ==========================");
                        }

                        @Override
                        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                            handler.sendEmptyMessageDelayed(1, 300);
                            LogUtil.d(TAG, "onConnectSuccess ==========================");
                        }

                        @Override
                        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                            handler.sendEmptyMessageDelayed(1, 300);
                            LogUtil.d(TAG, "onDisConnected ==========================");
                        }
                    });
                }
            }
        });
    }

    private void syncScan() {
        if (!mSwitchBlue.isChecked()) {
            mLoadingView.hide();
            return;
        }

        if (BleManager.getInstance().isBlueEnable()) {
            BleManager.getInstance().scan(mBleScanCallback);
        } else {
            if (mBleAdapter.getItemCount() == 0) {
                mLoadingView.showLoadingNoText();
            }
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
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
//        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)         // 只扫描指定广播名的设备，可选
//                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
//                .setScanTimeOut(5000)              // 扫描超时时间，可选，默认10秒
//                .build();
//        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

}
