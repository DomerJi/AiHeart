package com.thfw.robotheart.robot;

import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
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
import android.util.Log;

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
     * @param bleDevice
     * @return State of the profile connection. One of
     * {@link BluetoothProfile#STATE_CONNECTED},
     * {@link BluetoothProfile#STATE_CONNECTING},
     * {@link BluetoothProfile#STATE_DISCONNECTED},
     * {@link BluetoothProfile#STATE_DISCONNECTING}
     */
    public int getConnectState(BleDevice bleDevice) {
        if (bleDevice != null) {
            return bluetoothManager.getConnectionState(bleDevice.getDevice(), BluetoothProfile.GATT);
        } else {
            return BluetoothProfile.STATE_DISCONNECTED;
        }
    }

    public boolean isConnected(BleDevice bleDevice) {
        return getConnectState(bleDevice) == BluetoothProfile.STATE_CONNECTED;
    }


    public void newDestroy() {
        newCancelScan();
        if (mBleBroadcastReceiver != null && context != null) {
            context.unregisterReceiver(mBleBroadcastReceiver);
        }
        mBleBroadcastReceiver = null;
    }


    /**
     * ble 新 api
     */

    BroadcastReceiver mBleBroadcastReceiver;
    BleScanCallback scanCallback;

    public void newScan(BleScanCallback scanCallback) {
        this.scanCallback = scanCallback;
        if (mBleBroadcastReceiver == null) {
            mBleBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent != null) {
                        Log.d("Ble", intent.getAction());
                        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if (device != null) {
                                BleDevice bleDevice = new BleDevice(device);
                                if (scanCallback != null) {
                                    scanCallback.onScanning(bleDevice);
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    Log.d("Ble", "-----> " + device.getAlias() + "_" + device.getName());
                                } else {
                                    Log.d("Ble", "-----> " + device.getName());
                                }
                            }

                        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                            if (scanCallback != null) {
                                scanCallback.onScanStarted(true);
                            }
                        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                            if (scanCallback != null) {
                                scanCallback.onScanFinished(new ArrayList<>());
                            }
                        }

                        if (!BleManager.getInstance().getBluetoothAdapter().isDiscovering()) {
                            if (scanCallback != null) {
                                scanCallback.onScanFinished(new ArrayList<>());
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
            filter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            context.registerReceiver(mBleBroadcastReceiver, filter);
        }

        if (BleManager.getInstance().getBluetoothAdapter().isDiscovering()) {
            Log.d("Ble", "startDiscovery -----> ing ");
            return;
        }
        boolean success = BleManager.getInstance().getBluetoothAdapter().startDiscovery();
        Log.d("Ble", "startDiscovery -----> " + success);
        scanCallback.onScanStarted(true);
    }

    public void newCancelScan() {
        if (BleManager.getInstance().getBluetoothAdapter().isDiscovering()) {
            Log.d("Ble", "startDiscovery -----> ing ");
            BleManager.getInstance().getBluetoothAdapter().cancelDiscovery();
            if (scanCallback != null) {
                scanCallback.onScanFinished(new ArrayList<>());
            }
            return;
        }
    }

    public boolean isAudioBlue(BluetoothDevice device) {
        if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
            Log.d("Ble", "isAudioBlue true");
            return true;
        }
        return false;
    }

    public boolean isConnected(BluetoothDevice bluetoothDevice) {
        BluetoothDevice bindDevice = getPairBLEAndConnectBLE();
        return bindDevice != null && bluetoothDevice != null && bindDevice.getAddress().equals(bluetoothDevice.getAddress());
    }

    public BluetoothDevice getPairBLEAndConnectBLE() {
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
                    Log.d("markTest", "device2=" + bluetoothDevice.getAddress());
                    return bluetoothDevice;
                }
                Log.d("markTest", "device1=" + bluetoothDevice.getName());
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
            return false;
        }
        return true;
    }


    public boolean connectDevice(BluetoothDevice mBluetoothDevice) {
        try {
            // 连接建立之前的先配对
            if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                Method creMethod = BluetoothDevice.class
                        .getMethod("createBond");
                Log.e(TAG, "开始配对");
                creMethod.invoke(mBluetoothDevice);
                return true;
            } else {
                mBluetoothDevice.createBond();
                ToastUtil.show("配对成功!");
                return false;
            }

        } catch (Exception e) {
            ToastUtil.show("无法配对!");
            Log.e(TAG, "无法配对 e  = " + e.getMessage());
            return false;
        } finally {
            if (bluetoothAdapter != null) {
                bluetoothAdapter.cancelDiscovery();
            }
        }
    }


}
