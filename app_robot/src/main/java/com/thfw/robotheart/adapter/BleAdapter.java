package com.thfw.robotheart.adapter;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
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

    private int themeColor;
    private TranslateAnimation translateAnimation;

    public BleAdapter(List<BleDevice> dataList) {
        super(dataList);
    }

    public void notifyItem(BluetoothDevice bleDevice) {
        if (mDataList != null) {
            int size = mDataList.size();
            for (int position = 0; position < size; position++) {
                if (bleDevice.getAddress().equals(mDataList.get(position).getMac())) {
                    notifyItemChanged(position);
                    break;
                }
            }
        }
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
        if (bleDevice.getDevice().getBondState() == BluetoothDevice.BOND_BONDING || bleDevice.isConnecting()) {
            startAnimBonding(holder, bleDevice);
        } else {
            stopAnimBonding(holder, bleDevice);
        }

        switch (bleDevice.getDevice().getBondState()) {
            case BluetoothDevice.BOND_BONDED:
                holder.mTvBind.setText("已保存");
                holder.mTvBind.setVisibility(View.VISIBLE);
                holder.itemView.setSelected(false);
                break;
            case BluetoothDevice.BOND_BONDING:
                holder.mTvBind.setText("配对中...");
                holder.mTvBind.setVisibility(View.VISIBLE);
                holder.itemView.setSelected(true);
                break;
            default:
                holder.itemView.setSelected(false);
                holder.mTvBind.setVisibility(View.GONE);
                break;
        }

        holder.mTvDeviceName.setText(name);
        holder.mTvDeviceMac.setText("地址：" + bleDevice.getMac());
        if (BleManager.getInstance().isConnected(bleDevice.getDevice())) {
            holder.mTvState.setText("已连接");
            holder.mTvState.setTextColor(themeColor);
            holder.itemView.setSelected(true);
        } else {
            holder.mTvState.setText("未连接");
            holder.mTvState.setTextColor(Color.WHITE);
            holder.itemView.setSelected(false);
        }
        holder.mIvAudioDevice.setVisibility(BleManager.getInstance()
                .isAudioBlue(bleDevice.getDevice())
                ? View.VISIBLE : View.GONE);
    }

    public void stopAnimBonding(BleHolder holder, BleDevice bleDevice) {
        holder.mVBondingAnim.setVisibility(View.GONE);
        bleDevice.setAnim(false);
        if (holder.mVBondingAnim.getAnimation() != null) {
            holder.mVBondingAnim.getAnimation().cancel();
            holder.mVBondingAnim.clearAnimation();
        }

    }

    public void startAnimBonding(BleHolder holder, BleDevice bleDevice) {
        Log.e("startAnimBonding", "jspjspjsp=========================================2222");
        if (holder.itemView.getWidth() < 1) {
            holder.itemView.postDelayed(() -> {
                startAnimBonding(holder, bleDevice);
            }, 100);
            return;
        }
        Log.e("startAnimBonding", "jspjspjsp========================================1111=");
        View view = holder.mVBondingAnim;
        view.setVisibility(View.VISIBLE);
        if (bleDevice.isAnim() && view.getAnimation() != null) {
            return;
        }
        if (translateAnimation == null) {
            translateAnimation = new TranslateAnimation(0 - view.getWidth(), holder.itemView.getWidth() + view.getWidth(), 0, 0);
            translateAnimation.setDuration(1500);
            translateAnimation.setRepeatCount(100);
            translateAnimation.setInterpolator(new DecelerateInterpolator());
        }
        view.startAnimation(translateAnimation);
        Log.e("startAnimBonding", "jspjspjsp=========================================");
        bleDevice.setAnim(true);

    }


    public class BleHolder extends RecyclerView.ViewHolder {

        private TextView mTvState;
        private TextView mTvDeviceName;
        private TextView mTvDeviceMac;
        private ImageView mIvAudioDevice;
        private TextView mTvBind;
        private View mVBondingAnim;

        public BleHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onBlueLongClickListener != null) {
                        onBlueLongClickListener.onLongCLick(mDataList, getBindingAdapterPosition());
                    }
                    return true;
                }
            });
        }

        private void initView(View itemView) {
            mTvState = (TextView) itemView.findViewById(R.id.tv_state);
            mTvDeviceName = (TextView) itemView.findViewById(R.id.tv_device_name);
            mTvDeviceMac = (TextView) itemView.findViewById(R.id.tv_device_mac);
            mIvAudioDevice = (ImageView) itemView.findViewById(R.id.iv_audio_device);
            mTvBind = (TextView) itemView.findViewById(R.id.tv_bind);
            mVBondingAnim = itemView.findViewById(R.id.v_bonding_anim);
        }
    }

    OnBlueLongClickListener onBlueLongClickListener;

    public void setOnBlueLongClickListener(OnBlueLongClickListener onBlueLongClickListener) {
        this.onBlueLongClickListener = onBlueLongClickListener;
    }

    public interface OnBlueLongClickListener {
        void onLongCLick(List<BleDevice> list, int position);
    }

}
