package com.thfw.ui.voice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.thfw.ui.voice.speech.SpeechHelper;
import com.thfw.ui.voice.tts.TtsHelper;

/**
 * Author:pengs
 * Date: 2022/1/12 10:39
 * Describe:Todo
 */
public class PolicyHelper {
    private static final String TAG = PolicyHelper.class.getSimpleName();
    private static PolicyHelper policyHelper;

    private int wakeType;
    private boolean switchReadAfter;
    private boolean requestIng;

    public void setRequestIng(boolean requestIng) {
        this.requestIng = requestIng;
        if (this.requestIng) {
            synchronized (PolicyHelper.class) {
                if (SpeechHelper.getInstance().isIng()) {
                    SpeechHelper.getInstance().stop();
                }
            }
        }
    }

    public boolean isRequestIng() {
        return requestIng;
    }

    public void setSwitchReadAfter(boolean switchReadAfter) {
        this.switchReadAfter = switchReadAfter;
    }

    private Handler handler;


    private PolicyHelper() {
        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                checkState();
            }
        };
        this.wakeType = WakeType.NULL;
    }


    private void sendCheckMsg() {
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(0, 120);
    }

    public static PolicyHelper getInstance() {
        if (policyHelper == null) {
            synchronized (PolicyHelper.class) {
                if (policyHelper == null) {
                    policyHelper = new PolicyHelper();
                }
            }
        }
        return policyHelper;
    }

    public boolean isSpeechMode() {
        return this.wakeType == WakeType.SPEECH;
    }

    public boolean isSwitchReadAfter() {
        return isSpeechMode() && switchReadAfter;
    }


    public boolean isPressMode() {
        return this.wakeType == WakeType.PRESS;
    }


    public void checkState() {

        switch (this.wakeType) {
            case WakeType.SPEECH:
            case WakeType.PRESS:
                synchronized (PolicyHelper.class) {
                    if (requestIng) {
                        if (SpeechHelper.getInstance().isIng()) {
                            SpeechHelper.getInstance().stop();
                        }
                    } else {
                        if (!SpeechHelper.getInstance().isIng()) {
                            if (TtsHelper.getInstance().isIng()) {
                                TtsHelper.getInstance().stop();
                            }
                            SpeechHelper.getInstance().start();
                        }
                    }
                    sendCheckMsg();
                }
                break;
            case WakeType.WAKEUPER:
                sendCheckMsg();
                break;
            default:
                break;

        }

    }

    public int getWakeType() {
        return wakeType;
    }

    public void setResultListener(SpeechHelper.ResultListener resultListener) {
        SpeechHelper.getInstance().setResultListener(resultListener);
    }

    public void startSpeech() {
        if (wakeType != WakeType.SPEECH) {
            wakeType = WakeType.SPEECH;
            SpeechHelper.getInstance().clearCacheText();
            sendCheckMsg();
        }
    }

    public void startPressed() {
        if (wakeType != WakeType.PRESS) {
            wakeType = WakeType.PRESS;
            SpeechHelper.getInstance().clearCacheText();
            sendCheckMsg();
        }
    }

    public void end() {
        SpeechHelper.getInstance().stop();
        wakeType = WakeType.NULL;
    }


}
