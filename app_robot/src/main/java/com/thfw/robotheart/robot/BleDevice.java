package com.thfw.robotheart.robot;


import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;


public class BleDevice implements Parcelable {

    private BluetoothDevice mDevice;
    private byte[] mScanRecord;
    private int mRssi;
    private long mTimestampNanos;

    private boolean anim = false;
    private boolean connecting = false;

    public BleDevice beginConnect() {
        this.connecting = true;
        return this;
    }

    public void endConnect() {
        this.connecting = false;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public boolean isAnim() {
        return anim;
    }

    public void setAnim(boolean anim) {
        this.anim = anim;
    }

    public BleDevice(BluetoothDevice device) {
        mDevice = device;
    }

    public BleDevice(BluetoothDevice device, int rssi, byte[] scanRecord, long timestampNanos) {
        mDevice = device;
        mScanRecord = scanRecord;
        mRssi = rssi;
        mTimestampNanos = timestampNanos;
    }

    protected BleDevice(Parcel in) {
        mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        mScanRecord = in.createByteArray();
        mRssi = in.readInt();
        mTimestampNanos = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mDevice, flags);
        dest.writeByteArray(mScanRecord);
        dest.writeInt(mRssi);
        dest.writeLong(mTimestampNanos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel in) {
            return new BleDevice(in);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };

    public String getName() {
        if (mDevice != null) {
            return mDevice.getName();
        }
        return null;
    }

    public String getVisibleName() {
        String name = getName();
        if (TextUtils.isEmpty(name)) {
            return getMac();
        } else {
            return name;
        }
    }

    public String getMac() {
        if (mDevice != null) {
            return mDevice.getAddress();
        }
        return null;
    }

    public String getKey() {
        if (mDevice != null) {
            return mDevice.getName() + mDevice.getAddress();
        }
        return "";
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BluetoothDevice device) {
        this.mDevice = device;
    }

    public byte[] getScanRecord() {
        return mScanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.mScanRecord = scanRecord;
    }

    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int rssi) {
        this.mRssi = rssi;
    }

    public long getTimestampNanos() {
        return mTimestampNanos;
    }

    public void setTimestampNanos(long timestampNanos) {
        this.mTimestampNanos = timestampNanos;
    }

}
