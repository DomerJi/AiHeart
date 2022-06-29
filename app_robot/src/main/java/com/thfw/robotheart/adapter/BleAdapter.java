package com.thfw.robotheart.adapter;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.robotheart.R;
import com.thfw.robotheart.robot.BleDevice;
import com.thfw.robotheart.robot.BleManager;

import org.jetbrains.annotations.NotNull;

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
    public void onBindViewHolder(@NonNull @NotNull BleHolder holder, int position) {
        BleDevice bleDevice = mDataList.get(position);
        String name = bleDevice.getName();
        if (TextUtils.isEmpty(name)) {
            // 验证是否许可权限
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    name = bleDevice.getDevice().getAlias();
                }
            }
        }

        if (TextUtils.isEmpty(name)) {
            name = "unknown";
        }
        switch (bleDevice.getDevice().getBondState()) {
            case BluetoothDevice.BOND_BONDED:
                holder.mTvBind.setText("已保存");
                holder.mTvBind.setVisibility(View.VISIBLE);
                break;
            case BluetoothDevice.BOND_BONDING:
                holder.mTvBind.setText("配对中...");
                holder.mTvBind.setVisibility(View.VISIBLE);
                break;
            default:
                holder.mTvBind.setVisibility(View.GONE);
                break;
        }

        holder.mTvDeviceName.setText(name);
        holder.mTvDeviceMac.setText("地址：" + bleDevice.getMac());
        if (BleManager.getInstance().isConnected(bleDevice.getDevice())) {
            holder.mTvState.setText("已连接");
            holder.mTvState.setTextColor(themeColor);
        } else {
            holder.mTvState.setText("未连接");
            holder.mTvState.setTextColor(white80);
        }
        holder.mIvAudioDevice.setVisibility(BleManager.getInstance()
                .isAudioBlue(bleDevice.getDevice())
                ? View.VISIBLE : View.GONE);
    }


    public class BleHolder extends RecyclerView.ViewHolder {

        private TextView mTvState;
        private TextView mTvDeviceName;
        private TextView mTvDeviceMac;
        private ImageView mIvAudioDevice;
        private TextView mTvBind;

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
            mTvDeviceMac = (TextView) itemView.findViewById(R.id.tv_device_mac);
            mIvAudioDevice = (ImageView) itemView.findViewById(R.id.iv_audio_device);
            mTvBind = (TextView) itemView.findViewById(R.id.tv_bind);
        }
    }

}
