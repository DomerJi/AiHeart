package com.thfw.robotheart.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.RecyclerView;

import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/8/9 17:21
 * Describe:wifi 列表
 */
public class WifiAdapter extends BaseAdapter<ScanResult, WifiAdapter.WifiHolder> {

    private Context mContext;
    private List<ScanResult> scanResults;
    private HashMap<String, Boolean> savePassWord;
    private WifiManager mWifiManager;
    private OnWifiItemListener onWifiItemListener;

    public WifiAdapter(List<ScanResult> scanResults) {
        super(scanResults);
        this.scanResults = scanResults;
        this.savePassWord = new HashMap<>();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mContext = recyclerView.getContext();
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
        boolean connected = WifiUtils.withContext(mContext.getApplicationContext()).isWifiConnected(scanResult.SSID);
        holder.mTvConnectState.setVisibility(connected ? View.VISIBLE : View.INVISIBLE);
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
        holder.itemView.setOnClickListener(v -> {
            if (connected) {
                new AlertDialog.Builder(mContext).setTitle("忘记网络").setMessage("是否忘记此网络")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                WifiUtils.withContext(mContext.getApplicationContext())
                                        .disconnect(new DisconnectionSuccessListener() {
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
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

                return;
            }
            if (onWifiItemListener != null) {
                onWifiItemListener.onItemClick(scanResult, capabilities, position);
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
