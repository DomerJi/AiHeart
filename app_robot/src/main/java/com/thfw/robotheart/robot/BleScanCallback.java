package com.thfw.robotheart.robot;


import android.bluetooth.BluetoothDevice;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public abstract void onScanFinished();
    public abstract void onBondStateChanged(BluetoothDevice device);

}
