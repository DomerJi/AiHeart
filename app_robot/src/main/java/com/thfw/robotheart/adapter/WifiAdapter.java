package com.thfw.robotheart.adapter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.WifiHelper;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;

import java.util.HashMap;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/8/9 17:21
 * Describe:wifi 列表
 */
public class WifiAdapter extends BaseAdapter<ScanResult, WifiAdapter.WifiHolder> {

    private List<ScanResult> scanResults;
    private HashMap<String, Boolean> savePassWord;
    private WifiManager mWifiManager;
    private OnWifiItemListener onWifiItemListener;
    private ScanResult ssidIng;

    public void setSsidIng(ScanResult ssidIng) {
        this.ssidIng = ssidIng;
    }

    public WifiAdapter(List<ScanResult> scanResults) {
        super(scanResults);
        this.scanResults = scanResults;
        this.savePassWord = new HashMap<>();

    }

    public void setWifiManager(WifiManager mWifiManager) {
        this.mWifiManager = mWifiManager;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public WifiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WifiHolder(LayoutInflater.from(mContext).inflate(R.layout.item_wifi_list, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull WifiHolder holder, int position) {

        ScanResult scanResult = scanResults.get(position);
        String capabilities = getEncrypt(mWifiManager, scanResult);
        if (TextUtils.isEmpty(capabilities)) {
            holder.mIvLock.setVisibility(View.GONE);
        } else {
            holder.mIvLock.setVisibility(View.VISIBLE);
        }
        boolean connected = WifiHelper.get().isWifiConnected(scanResult.SSID);
        if (connected) {
            holder.mTvConnectState.setText("已连接");
            holder.mTvConnectState.setVisibility(View.VISIBLE);
        }
        if (scanResult == ssidIng) {
            holder.mTvConnectState.setText("连接中..");
            holder.mTvConnectState.setVisibility(View.VISIBLE);
        } else {
            holder.mTvConnectState.setText("已连接");
            holder.mTvConnectState.setVisibility(connected ? View.VISIBLE : View.INVISIBLE);
        }
        holder.mTvName.setText(scanResult.SSID);
        savePassWord.put(scanResult.SSID, Util.isSavePassWord(mWifiManager, scanResult.SSID));
        Log.i("savePassWord", "savePassWord = " + savePassWord.get(scanResult.SSID));
        if (savePassWord.get(scanResult.SSID)) {
            holder.mTvPass.setVisibility(View.VISIBLE);
            holder.mTvPass.setText("已保存");
            // todo 不需要密码
            holder.mIvLock.setImageResource(R.mipmap.ic_set_lock_on);
        } else {
            // todo 不需要密码
            holder.mIvLock.setImageResource(R.mipmap.ic_wifi_local_off);
            holder.mTvPass.setVisibility(View.GONE);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (savePassWord.get(scanResult.SSID)) {
                    forget(scanResult);
                    return true;
                }
                return false;
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (WifiHelper.get().isWifiConnected(scanResult.SSID)) {
                forget(scanResult);
                return;
            }
            if (onWifiItemListener != null) {
                onWifiItemListener.onItemClick(scanResult, capabilities, position);
            }
        });

    }

    private void forget(ScanResult scanResult) {
        DialogRobotFactory.createCustomDialog((FragmentActivity) mContext, new DialogRobotFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvTitle.setText("忘记网络");
                mTvHint.setText("是否忘记此网络");
                mTvRight.setText("是");
                mTvLeft.setText("否");
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                tDialog.dismiss();
                if (view.getId() == R.id.tv_right) {
                    WifiHelper.get().disconnect(new DisconnectionSuccessListener() {
                        @Override
                        public void success() {
                            notifyDataSetChanged();
                            Util.removeWifiBySsid(mWifiManager, scanResult.SSID);
                            Toast.makeText(mContext, "成功断开链接", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failed(@NonNull DisconnectionErrorCode errorCode) {
                            Toast.makeText(mContext, "断开链接失败: " + errorCode.toString(), Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取wifi加密方式
     */
    public String getEncrypt(WifiManager mWifiManager, ScanResult scanResult) {
        if (mWifiManager != null) {
            String capabilities = scanResult.capabilities;
            if (!TextUtils.isEmpty(capabilities)) {
                if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                    return "WPA";
                } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                    return "WEP";
                } else if (capabilities.contains("PSK") || capabilities.contains("psk")) {
                    return "PSK";
                } else {
                    return "";
                }
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return scanResults == null ? 0 : scanResults.size();
    }

    public void setOnWifiItemListener(OnWifiItemListener onWifiItemListener) {
        this.onWifiItemListener = onWifiItemListener;
    }

    public interface OnWifiItemListener {
        void onItemClick(ScanResult scanResult, String passType, int position);
    }

    public class WifiHolder extends RecyclerView.ViewHolder {

        private TextView mTvConnectState;
        private TextView mTvName;
        private ImageView mIvLock;
        private ImageView mIvLevel;
        private TextView mTvPass;

        public WifiHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mTvConnectState = (TextView) itemView.findViewById(R.id.tv_connect_state);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mIvLock = (ImageView) itemView.findViewById(R.id.iv_lock);
            mIvLevel = (ImageView) itemView.findViewById(R.id.iv_level);
            mTvPass = (TextView) itemView.findViewById(R.id.tv_pass);
        }
    }
}
