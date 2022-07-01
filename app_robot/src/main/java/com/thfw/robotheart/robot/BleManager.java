package com.thfw.robotheart.robot;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleManager {

    private static final String TAG = BleManager.class.getSimpleName();
    private Application context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BroadcastReceiver mBleBroadcastReceiver;
    private BleScanCallback scanCallback;
    private BluetoothDevice currentBluetoothDevice;

    private BluetoothA2dp mBluetoothA2dp;
    private boolean connected;
    int num = 0;


    public static BleManager getInstance() {
        return BleManagerHolder.sBleManager;
    }

    private static class BleManagerHolder {
        private static final BleManager sBleManager = new BleManager();
    }

    public void init(Application app) {
        if (context == null && app != null) {
            context = app;
            if (isSupportBle()) {
                bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            }
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    /**
     * Get the Context
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * Get the BluetoothManager
     *
     * @return
     */
    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    /**
     * Get the BluetoothAdapter
     *
     * @return
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }


    /**
     * is support ble?
     *
     * @return
     */
    public boolean isSupportBle() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * Open bluetooth
     */
    public void enableBluetooth() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.enable();
        }
    }

    /**
     * Disable bluetooth
     */
    public void disableBluetooth() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled())
                bluetoothAdapter.disable();
        }
    }

    /**
     * judge Bluetooth is enable
     *
     * @return
     */
    public boolean isBlueEnable() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    public BleDevice convertBleDevice(BluetoothDevice bluetoothDevice) {
        return new BleDevice(bluetoothDevice);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BleDevice convertBleDevice(ScanResult scanResult) {
        if (scanResult == null) {
            throw new IllegalArgumentException("scanResult can not be Null!");
        }
        BluetoothDevice bluetoothDevice = scanResult.getDevice();
        int rssi = scanResult.getRssi();
        ScanRecord scanRecord = scanResult.getScanRecord();
        byte[] bytes = null;
        if (scanRecord != null)
            bytes = scanRecord.getBytes();
        long timestampNanos = scanResult.getTimestampNanos();
        return new BleDevice(bluetoothDevice, rssi, bytes, timestampNanos);
    }


    /**
     * @return State of the profile connection. One of
     * {@link BluetoothProfile#STATE_CONNECTED},
     * {@link BluetoothProfile#STATE_CONNECTING},
     * {@link BluetoothProfile#STATE_DISCONNECTED},
     * {@link BluetoothProfile#STATE_DISCONNECTING}
     */
    public int getConnectState(BluetoothDevice device) {
        if (device != null) {
            return bluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
        } else {
            return BluetoothProfile.STATE_DISCONNECTED;
        }
    }

    public boolean isConnected2(BluetoothDevice device) {
        return getConnectState(device) == BluetoothProfile.STATE_CONNECTED;
    }


    public void newDestroy() {
        newCancelScan();
        if (mBleBroadcastReceiver != null && context != null) {
            context.unregisterReceiver(mBleBroadcastReceiver);
        }
        mBleBroadcastReceiver = null;
    }


    public void newScan(BleScanCallback scanCallback) {
        this.scanCallback = scanCallback;
        if (mBleBroadcastReceiver == null) {
            mBleBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent != null) {
                        Log.d(TAG, "jsp -> action：" + intent.getAction());
                        switch (intent.getAction()) {
                            case BluetoothDevice.ACTION_FOUND:
                                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                if (device != null) {
                                    BleDevice bleDevice = new BleDevice(device);
                                    if (scanCallback != null) {
                                        scanCallback.onScanning(bleDevice);
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                        Log.d(TAG, "-----> " + device.getAlias() + "_" + device.getName());
                                    } else {
                                        Log.d(TAG, "-----> " + device.getName());
                                    }
                                }
                                break;
                            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                                if (scanCallback != null) {
                                    scanCallback.onScanStarted(true);
                                }
                                break;
                            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                                if (scanCallback != null) {
                                    scanCallback.onScanFinished();
                                }
                                break;
                            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:

                                int currentBondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -99);
                                int oldState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -99);

                                Log.d(TAG, "currentBondState = " + currentBondState + " ; oldBondState = " + oldState);
                                BluetoothDevice bondDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                if (bondDevice != null) {
                                    if (scanCallback != null) {
                                        scanCallback.onBondStateChanged(bondDevice);
                                    }
                                    if (oldState == BluetoothDevice.BOND_BONDING) {
                                        if (currentBondState == BluetoothDevice.BOND_NONE) {
                                            String deviceName = TextUtils.isEmpty(bondDevice.getName()) ? bondDevice.getAddress() : bondDevice.getName();
                                            ToastUtil.showLong("与 “" + deviceName + "” 配对失败");
                                        }
                                    }
                                    Log.d(TAG, "currentBondState name = " + bondDevice.getName() + " ; state = " + bondDevice.getBondState());
                                }
                                break;
                            case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                                int currentConState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -99);
                                int oldConState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -99);
                                Log.d(TAG, "currentConState = " + currentConState + " ; oldConState = " + oldConState);
                                BluetoothDevice conDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                if (conDevice != null) {
                                    if (scanCallback != null) {
                                        scanCallback.onBondStateChanged(conDevice);
                                    }
                                    Log.d(TAG, "currentConState name = " + conDevice.getName());
                                }
                                break;
                            case BluetoothDevice.ACTION_ACL_CONNECTED:
                            case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                                BluetoothDevice aclDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                if (aclDevice != null) {
                                    if (scanCallback != null) {
                                        scanCallback.onBondStateChanged(aclDevice);
                                    }
                                    Log.d(TAG, "aclDevice name = " + aclDevice.getName());
                                }
                                break;

                        }
                        if (!BleManager.getInstance().getBluetoothAdapter().isDiscovering()) {
                            if (scanCallback != null) {
                                scanCallback.onScanFinished();
                            }
                        }
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_ALIAS_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            context.registerReceiver(mBleBroadcastReceiver, filter);
        }

        if (bluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "startDiscovery -----> ing ");
            return;
        }
        boolean success = bluetoothAdapter.startDiscovery();
        Log.d(TAG, "startDiscovery -----> " + success);
        scanCallback.onScanStarted(true);
    }

    public void newCancelScan() {
        if (bluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "startDiscovery -----> ing ");
            bluetoothAdapter.cancelDiscovery();
            if (scanCallback != null) {
                scanCallback.onScanFinished();
            }
            return;
        }
    }

    public boolean isAudioBlue(BluetoothDevice device) {
        if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
            Log.d(TAG, "isAudioBlue true");
            return true;
        }
        return false;
    }

    public static boolean isConnected() {
        return null != getPairBLEAndConnectBLE();
    }

    public boolean isConnected(BluetoothDevice bluetoothDevice) {
        BluetoothDevice bindDevice = getPairBLEAndConnectBLE();
        boolean pair = bindDevice != null && bluetoothDevice != null && bindDevice.getAddress().equals(bluetoothDevice.getAddress());
        return pair;
    }

    public static BluetoothDevice getPairBLEAndConnectBLE() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if (defaultAdapter != null) {
            //得到已配对的设备列表
            Set<BluetoothDevice> devices = defaultAdapter.getBondedDevices();

            for (BluetoothDevice bluetoothDevice : devices) {
                boolean isConnect = false;
                try {
                    //获取当前连接的蓝牙信息
                    isConnect = (boolean) bluetoothDevice.getClass().getMethod("isConnected").invoke(bluetoothDevice);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }

                if (isConnect) {
                    return bluetoothDevice;
                }
            }
        }
        return null;
    }

    public List<BleDevice> getAllConnectedDevice() {
        if (bluetoothAdapter != null) {
            //得到已配对的设备列表
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            List<BleDevice> bondedDevices = new ArrayList<>();
            for (BluetoothDevice bluetoothDevice : devices) {
                bondedDevices.add(new BleDevice(bluetoothDevice));
            }
            return bondedDevices;
        }
        return new ArrayList<>();
    }


    public boolean unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            if (scanCallback != null) {
                scanCallback.onBondStateChanged(device);
            }
            return false;
        }
        if (scanCallback != null) {
            scanCallback.onBondStateChanged(device);
        }
        return true;
    }


    public void pairDevice(BluetoothDevice mBluetoothDevice) {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        try {
            // 连接建立之前的先配对
            if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                Method creMethod = BluetoothDevice.class
                        .getMethod("createBond");
                Log.e(TAG, "开始配对");
                creMethod.invoke(mBluetoothDevice);
            }
        } catch (Exception e) {
            Log.e(TAG, "无法配对 e  = " + e.getMessage());
            if (scanCallback != null) {
                scanCallback.onBondStateChanged(mBluetoothDevice);
            }
        }
    }

    /**
     * 蓝牙蓝牙设备，蓝牙与设备进行配对
     */
    public void connectBluetoothA2DP(Context context, BluetoothDevice btDev, IBtConnectCallBack callBack) {
        if (bluetoothAdapter == null) {
            callBack.onFail();
            return;
        }
        bluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    //Service连接成功，获得BluetoothA2DP
                    mBluetoothA2dp = (BluetoothA2dp) proxy;
                    connectBlueToothAudio(btDev, callBack);
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                mBluetoothA2dp = null;
                callBack.onFail();
            }
        }, BluetoothProfile.A2DP);
    }

    /**
     * desc: 连接蓝牙音响
     */
    public void connectBlueToothAudio(BluetoothDevice btDev, IBtConnectCallBack callBack) {
        if (mBluetoothA2dp == null) {
            callBack.onFail();
            return;
        }
        LogUtil.w("A2DP sacn app 333333333333333333--》》》");
        if (bluetoothAdapter == null) {
            callBack.onFail();
            return;
        }
        if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
            LogUtil.i("A2DP base_ac没有配对");
            try {
                //btDevice.createBond();
                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                Boolean returnValue = (Boolean) createBondMethod.invoke(btDev);
            } catch (Exception e) {
                e.printStackTrace();
                callBack.onFail();
            }
        }
        num = 0;
        HandlerUtil.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (btDev.getBondState() != BluetoothDevice.BOND_BONDED) {
                    num++;
                    if (num > 10) {
                        callBack.onFail();
                        return;
                    } else {
                        HandlerUtil.getMainHandler().postDelayed(this, 1000);
                    }
                } else {
                    try {
                        @SuppressLint("PrivateApi")
                        Method connect = mBluetoothA2dp.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
                        connect.setAccessible(true);
                        connect.invoke(mBluetoothA2dp, btDev);
                        //连接标志
                        connected = true;
                        callBack.onSuccess();
                        //getSocket(btDev);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callBack.onFail();
                    }

                }
            }
        }, 1000);
    }


    /**
     * 断开蓝牙设备连接
     *
     * @param bluetoothDevice BluetoothDevice
     */
    public void disconnect(Context context, BluetoothDevice bluetoothDevice) {
        currentBluetoothDevice = bluetoothDevice;
        //获取A2DP代理对象
        bluetoothAdapter.getProfileProxy(context, disconnectProfileServiceListener, BluetoothProfile.HEADSET);
        //获取HEADSET代理对象
        bluetoothAdapter.getProfileProxy(context, disconnectProfileServiceListener, BluetoothProfile.A2DP);
    }

    private final BluetoothProfile.ServiceListener disconnectProfileServiceListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceDisconnected(int profile) {

        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            try {
                if (profile == BluetoothProfile.HEADSET) {
                    // 使用HEADSET的协议断开蓝牙设备（使用了反射技术调用断开的方法）
                    BluetoothHeadset bluetoothHeadset = (BluetoothHeadset) proxy;
                    boolean isDisConnect = false;
                    try {
                        Method connect = bluetoothHeadset.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
                        connect.setAccessible(true);
                        isDisConnect = (boolean) connect.invoke(bluetoothHeadset, currentBluetoothDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!isDisConnect && currentBluetoothDevice != null) {
                        unpairDevice(currentBluetoothDevice);
                    }
                    Log.d(TAG, "isDisConnect:" + (isDisConnect ? "断开通话成功" : "断开通话失败") + currentBluetoothDevice.getName());
                } else if (profile == BluetoothProfile.A2DP) {
                    // 使用A2DP的协议断开蓝牙设备（使用了反射技术调用断开的方法）
                    BluetoothA2dp bluetoothA2dp = (BluetoothA2dp) proxy;
                    boolean isDisConnect = false;
                    try {
                        Method connect = bluetoothA2dp.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
                        connect.setAccessible(true);
                        isDisConnect = (boolean) connect.invoke(bluetoothA2dp, currentBluetoothDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "isDisConnect:" + (isDisConnect ? "断开音频成功" : "断开音频失败") + currentBluetoothDevice.getName());

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

}
