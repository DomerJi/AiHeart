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

    private static class Builder {
        private static PolicyHelper policyHelper = new PolicyHelper();
    }

    private int wakeType;

    private Handler handler;


    private PolicyHelper() {
        handler = new Handler(Looper.getMainLooper()) {
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
        handler.sendEmptyMessageDelayed(0, 100);
    }

    public static PolicyHelper getInstance() {
        return Builder.policyHelper;
    }

    public boolean isSpeechMode() {
        return this.wakeType == WakeType.SPEECH;
    }

    public boolean isPressMode() {
        return this.wakeType == WakeType.PRESS;
    }


    public void checkState() {
        switch (this.wakeType) {
            case WakeType.SPEECH:
            case WakeType.PRESS:
                if (!SpeechHelper.getInstance().isIng()) {
                    if (TtsHelper.getInstance().isIng()) {
                        TtsHelper.getInstance().stop();
                    }
                    SpeechHelper.getInstance().start();
                }
                sendCheckMsg();
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
