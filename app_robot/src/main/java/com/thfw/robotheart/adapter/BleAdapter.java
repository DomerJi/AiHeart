package com.thfw.robotheart.adapter;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.thfw.robotheart.R;
import com.thfw.robotheart.util.BleUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/1/10 10:24
 * Describe:Todo
 */
public class BleAdapter extends BaseAdapter<BleDevice, BleAdapter.BleHolder> {

    private int white80;
    private int themeColor;

    public BleAdapter(List<BleDevice> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public BleHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BleHolder(inflate(R.layout.item_ble_device, parent));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        themeColor = mContext.getResources().getColor(R.color.colorRobotFore);
        white80 = mContext.getResources().getColor(R.color.white_80);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BleAdapter.BleHolder holder, int position) {
        BleDevice bleDevice = mDataList.get(position);
        String name = bleDevice.getName();
        if (TextUtils.isEmpty(name)) {
            name = BleUtil.parseAdertisedData(bleDevice.getScanRecord()).getName();
        }

        if (TextUtils.isEmpty(name)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 验证是否许可权限
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                    name = bleDevice.getDevice().getAlias();
                }
            }
        }

        if (TextUtils.isEmpty(name)) {
            name = bleDevice.getMac();
        }
        holder.mTvDeviceName.setText(name + "：" + bleDevice.getMac());
        int connetState = BleManager.getInstance().getConnectState(bleDevice);
        switch (connetState) {
            case BluetoothProfile.STATE_DISCONNECTED:
                holder.mTvState.setText("未连接");
                holder.mTvState.setTextColor(white80);
                break;
            case BluetoothProfile.STATE_DISCONNECTING:
                holder.mTvState.setText("断开中");
                holder.mTvState.setTextColor(white80);
                break;
            case BluetoothProfile.STATE_CONNECTING:
                holder.mTvState.setText("连接中");
                holder.mTvState.setTextColor(themeColor);
                break;
            case BluetoothProfile.STATE_CONNECTED:
                holder.mTvState.setText("已连接");
                holder.mTvState.setTextColor(themeColor);
                break;
            default:
                holder.mTvState.setText("未连接");
                holder.mTvState.setTextColor(white80);
                break;
        }

    }

    private String getAliasName(BluetoothDevice device) {
        String deviceAlias = device.getName();

        try {
            Method method = device.getClass().getMethod("getAliasName");
            if (method != null) {
                deviceAlias = (String) method.invoke(device);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return deviceAlias;
    }

    public class BleHolder extends RecyclerView.ViewHolder {

        private TextView mTvState;
        private TextView mTvDeviceName;

        public BleHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }

        private void initView(View itemView) {
            mTvState = (TextView) itemView.findViewById(R.id.tv_state);
            mTvDeviceName = (TextView) itemView.findViewById(R.id.tv_device_name);
        }
    }
}
