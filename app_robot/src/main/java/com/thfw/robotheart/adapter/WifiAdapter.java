package com.thfw.robotheart.adapter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
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
    private HashMap<String, Boolean> saveAnim;
    private WifiManager mWifiManager;
    private OnWifiItemListener onWifiItemListener;
    private String ssId;
    private TranslateAnimation translateAnimation;

    public void notifySsId(String ssId) {
        if (!TextUtils.isEmpty(ssId)) {
            if (mDataList != null) {
                int size = mDataList.size();
                for (int position = 0; position < size; position++) {
                    if (ssId.equals(mDataList.get(position).SSID)) {
                        notifyItemChanged(position);
                        break;
                    }
                }
            }
        }
    }

    public void setSsId(String ssId) {
        this.ssId = ssId;
        if (TextUtils.isEmpty(ssId)) {
            notifyDataSetChanged();
        } else {
            notifySsId(ssId);
        }
    }

    public WifiAdapter(List<ScanResult> scanResults) {
        super(scanResults);
        this.scanResults = scanResults;
        this.savePassWord = new HashMap<>();
        this.saveAnim = new HashMap<>();

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
            stopAnimBonding(holder, scanResult);
            holder.itemView.setSelected(true);
        } else if (!TextUtils.isEmpty(ssId) && ssId.equals(scanResult.SSID)) {
            holder.mTvConnectState.setText("连接中..");
            startAnimBonding(holder, scanResult);
            holder.mTvConnectState.setVisibility(View.VISIBLE);
            holder.itemView.setSelected(false);
        } else {
            holder.itemView.setSelected(false);
            stopAnimBonding(holder, scanResult);
            holder.mTvConnectState.setText("未连接");
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

    public void stopAnimBonding(WifiHolder holder, ScanResult scanResult) {
        holder.mVBondingAnim.setVisibility(View.GONE);
        saveAnim.put(scanResult.SSID, false);
        if (holder.mVBondingAnim.getAnimation() != null) {
            holder.mVBondingAnim.getAnimation().cancel();
            holder.mVBondingAnim.clearAnimation();
        }

    }

    public void startAnimBonding(WifiHolder holder, ScanResult scanResult) {
        Log.e("startAnimBonding", "jspjspjsp=========================================2222");
        if (holder.itemView.getWidth() < 1) {
            holder.itemView.postDelayed(() -> {
                startAnimBonding(holder, scanResult);
            }, 100);
            return;
        }
        Log.e("startAnimBonding", "jspjspjsp========================================1111=");
        View view = holder.mVBondingAnim;
        view.setVisibility(View.VISIBLE);
        if (saveAnim.get(scanResult.SSID) && view.getAnimation() != null) {
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
        saveAnim.put(scanResult.SSID, true);
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
                    if (!WifiHelper.get().isWifiConnected(scanResult.SSID)) {
                        Util.removeWifiBySsid(mWifiManager, scanResult.SSID);
                        notifyDataSetChanged();
                        return;
                    }
                    WifiHelper.get().disconnect(new DisconnectionSuccessListener() {
                        @Override
                        public void success() {
                            Util.removeWifiBySsid(mWifiManager, scanResult.SSID);
                            Toast.makeText(mContext, "成功断开链接", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
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
    public static String getEncrypt(WifiManager mWifiManager, ScanResult scanResult) {
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
        private View mVBondingAnim;

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
            mVBondingAnim = itemView.findViewById(R.id.v_bonding_anim);
        }
    }
}
