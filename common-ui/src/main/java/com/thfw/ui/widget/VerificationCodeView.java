package com.thfw.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thfw.base.utils.NetworkUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.ui.R;

public class VerificationCodeView extends FrameLayout {

    private static final int MSG_WHAT_INTERVAL = 1;
    private final long intervalDelayed = 1000;
    private final int intervalTime = 60;
    boolean isGainVerificationCodeClick = false;
    private EditText mEtVerificationCode;
    private TextView mTvGainVerification;
    private VerificationListener verificationListener;
    private int currentIntervalTime;
    private Handler handler;

    public VerificationCodeView(@NonNull Context context) {
        this(context, null);
    }

    public VerificationCodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public VerificationCodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_verification_code_view, this);
        mEtVerificationCode = findViewById(R.id.et_verification_code);
        mTvGainVerification = findViewById(R.id.tv_gain_verification_code);
        mTvGainVerification.setOnClickListener(v -> {
            if (verificationListener != null && currentIntervalTime <= 0) {
                // todo 此处有网络，便视为验证码发送成功。
                if (NetworkUtil.isNetConnected(getContext())) {
                    verificationListener.gainVerificationCodeClick();
                    isGainVerificationCodeClick = true;
                    sendGainVerificationStatus(true);
                } else {
                    sendGainVerificationStatus(false);
                }

            }
        });
    }

    public boolean isGainVerificationCodeClick() {
        return isGainVerificationCodeClick;
    }

    public EditText getVerificationCode() {
        return mEtVerificationCode;
    }

    public TextView getGainVerification() {
        return mTvGainVerification;
    }

    public void setVerificationListener(VerificationListener verificationListener) {
        this.verificationListener = verificationListener;
    }

    public void sendGainVerificationStatus(boolean success) {
        if (success) {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == MSG_WHAT_INTERVAL) {
                            mTvGainVerification.setText(currentIntervalTime + "S");
                            if (currentIntervalTime <= 0) {
                                mTvGainVerification.setEnabled(true);
                                mTvGainVerification.setText(getContext().getString(R.string.gain_verification_code));
                            } else {
                                currentIntervalTime--;
                                handler.sendEmptyMessageDelayed(MSG_WHAT_INTERVAL, intervalDelayed);
                            }

                        }
                    }
                };
            }
            mTvGainVerification.setEnabled(false);
            currentIntervalTime = intervalTime;
            handler.sendEmptyMessage(MSG_WHAT_INTERVAL);
            ToastUtil.show(getContext().getString(R.string.verification_code_send_success));
        } else {
            ToastUtil.show(getContext().getString(R.string.net_no_connected));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public interface VerificationListener {
        void gainVerificationCodeClick();
    }

}
