package com.thfw.robotheart.fragments.sets;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.thanosfisherman.wifiutils.WifiConnectorBuilder;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.util.WifiHelper;

import org.jetbrains.annotations.NotNull;

/**
 * wifi 输入密码
 */
public class WifiInputFragment extends RobotBaseFragment {


    OnCompleteInputListener completeInputListener;
    private ScanResult scanResult;
    private String passType;
    private int position;
    private TextView mTvConnect;
    private LinearLayout mLlUser;
    private EditText mEtUser;
    private LinearLayout mLlPass;
    private EditText mEtPass;
    private ProgressBar mPbLoading;

    public WifiInputFragment(ScanResult scanResult, String passType, int position) {
        super();
        this.scanResult = scanResult;
        this.passType = passType;
        this.position = position;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_wifi_input;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTvConnect = (TextView) findViewById(R.id.tv_connect);
        mLlUser = (LinearLayout) findViewById(R.id.ll_user);
        mEtUser = (EditText) findViewById(R.id.et_user);
        mLlPass = (LinearLayout) findViewById(R.id.ll_pass);
        mEtPass = (EditText) findViewById(R.id.et_pass);
        mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        // "WPA" "WEP"
        if ("WEP".equals(passType)) {
            mLlUser.setVisibility(View.VISIBLE);
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    boolean flag1 = TextUtils.isEmpty(mEtUser.getText().toString());
                    boolean flag2 = TextUtils.isEmpty(mEtPass.getText().toString());
                    mTvConnect.setEnabled(flag1 && flag2);
                }
            };
            mEtUser.addTextChangedListener(textWatcher);
            mEtPass.addTextChangedListener(textWatcher);
            mTvConnect.setOnClickListener(v -> {
                if (WifiHelper.get().isWifiConnected()) {
                    hideInput();
                    connectUi(true);
                    WifiHelper.get().disconnect(new DisconnectionSuccessListener() {
                        @Override
                        public void success() {
                            connect();
                        }

                        @Override
                        public void failed(@NonNull @NotNull DisconnectionErrorCode errorCode) {
                            connect();
                        }
                    });
                } else {
                    connect();
                }
            });
        } else if ("WPA".equals(passType)) {
            mLlUser.setVisibility(View.GONE);
            mEtPass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mTvConnect.setEnabled(s.toString().length() > 0);
                }
            });
            mTvConnect.setOnClickListener(v -> {
                connect();
            });
        }
    }

    @Override
    public void initData() {

    }

    /**
     * 连接wifi
     */
    private void connect() {
        hideInput();
        connectUi(true);
        String user = mEtUser.getText().toString();
        WifiConnectorBuilder.WifiSuccessListener successListener;
        if (!TextUtils.isEmpty(user)) {
            successListener = WifiHelper.get()
                    .connectWith(user, scanResult.BSSID, mEtPass.getText().toString());

        } else {
            successListener = WifiHelper.get()
                    .connectWith(scanResult.SSID, scanResult.BSSID, mEtPass.getText().toString());
        }
        successListener.setTimeout(10000)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        connectUi(false);
                        if (completeInputListener != null) {
                            completeInputListener.onComplete();
                        }
                        ToastUtil.show("成功链接至-" + scanResult.SSID);
                        successListener.onConnectionResult(null);
                    }

                    @Override
                    public void failed(@NonNull ConnectionErrorCode errorCode) {
                        connectUi(false);
                        Util.removeWifiBySsid((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE), scanResult.SSID);
                        ToastUtil.show("链接失败");
                        successListener.onConnectionResult(null);
                    }
                }).start();
    }

    /**
     * 连接loading提示
     *
     * @param show
     */
    private void connectUi(boolean show) {
        mTvConnect.setEnabled(!show);
        mTvConnect.setText(show ? "" : "连接");
        mPbLoading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setCompleteInputListener(OnCompleteInputListener completeInputListener) {
        this.completeInputListener = completeInputListener;
    }

    public interface OnCompleteInputListener {
        void onComplete();
    }
}