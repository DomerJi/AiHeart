package com.thfw.ui.voice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.thfw.base.utils.LogUtil;
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
    private static final int MILLIS = 150;

    private int wakeType;
    private boolean switchReadAfter;
    private boolean requestIng;

    public void setRequestIng(boolean requestIng) {
        this.requestIng = requestIng;
        LogUtil.i(TAG, "requestIng = " + requestIng);
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

    private void sendCheckMsg(int millis) {
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0, millis);
    }

    private void sendCheckMsg() {
        sendCheckMsg(MILLIS);
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
                        sendCheckMsg(1000);
                    } else {
                        if (!SpeechHelper.getInstance().isIng()) {
                            if (TtsHelper.getInstance().isIng()) {
                                TtsHelper.getInstance().stop();
                            }
                            if (!SpeechHelper.getInstance().start()) {
                                sendCheckMsg(1500);
                            } else {
                                sendCheckMsg();
                            }
                        } else {
                            sendCheckMsg(500);
                        }
                    }

                }
                break;
            case WakeType.WAKEUPER:
                sendCheckMsg();
                break;
            default:
                if (SpeechHelper.getInstance().isIng()) {
                    SpeechHelper.getInstance().stop();
                }
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
            if (TtsHelper.getInstance().isIng()) {
                TtsHelper.getInstance().stop();
            }
            synchronized (PolicyHelper.class) {
                if (wakeType != WakeType.SPEECH) {
                    wakeType = WakeType.SPEECH;
                    setRequestIng(false);
                    SpeechHelper.getInstance().clearCacheText();
                    sendCheckMsg();
                }
            }
        }
    }

    public void startPressed() {
        if (wakeType != WakeType.PRESS) {
            if (TtsHelper.getInstance().isIng()) {
                TtsHelper.getInstance().stop();
            }
            synchronized (PolicyHelper.class) {
                if (wakeType != WakeType.PRESS) {
                    wakeType = WakeType.PRESS;
                    SpeechHelper.getInstance().clearCacheText();
                    sendCheckMsg();
                }
            }
        }
    }

    public void end() {
        synchronized (PolicyHelper.class) {
            wakeType = WakeType.NULL;
            handler.removeMessages(0);
            SpeechHelper.getInstance().stop();
            SpeechHelper.getInstance().destroy();
        }
    }


}
