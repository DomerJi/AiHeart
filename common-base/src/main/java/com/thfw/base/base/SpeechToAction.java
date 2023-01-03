package com.thfw.base.base;

import android.content.Intent;

import com.thfw.base.utils.LogUtil;

public class SpeechToAction {
    private static final String TAG = SpeechToAction.class.getSimpleName();
    public String text;
    public int type;
    public int code;
    public boolean like;
    public Intent intent;
    public Runnable runnable;

    public SpeechToAction(String text, Runnable runnable) {
        this.text = text;
        this.runnable = runnable;
    }

    public SpeechToAction setLike(boolean like) {
        this.like = like;
        return this;
    }

    public boolean run() {
        if (runnable != null) {
            try {
                runnable.run();
                LogUtil.i(TAG, "SpeechToAction -> text = " + text);
                return true;
            } catch (Exception e) {
                LogUtil.i(TAG, "SpeechToAction -> Exception e " + e.getMessage() + " text = " + text);
                return false;
            }
        } else {
            LogUtil.i(TAG, "SpeechToAction -> runnable is null text = " + text);
            return false;
        }
    }
}