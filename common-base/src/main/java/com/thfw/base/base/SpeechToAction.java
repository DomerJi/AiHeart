package com.thfw.base.base;

import android.content.Intent;
import android.text.TextUtils;

import com.thfw.base.models.SpeechModel;
import com.thfw.base.utils.LogUtil;

public class SpeechToAction {
    private static final String TAG = SpeechToAction.class.getSimpleName();
    public String text;
    public Instruction instruction;
    public int type;
    public int code;
    public String replace;
    public boolean like;
    public Intent intent;
    public Runnable runnable;

    // 替换文本中的 词汇 如播放 方便匹配列表
    public SpeechToAction setReplace(String replace) {
        this.replace = replace;
        return this;
    }

    public String getReplace() {
        return replace;
    }

    public boolean isReplace(){
        return !TextUtils.isEmpty(replace);
    }

    public SpeechToAction(String text, Runnable runnable) {
        LogUtil.i(TAG, "text -->> " + text);
        this.text = text;
        this.runnable = runnable;
    }

    public SpeechToAction(Instruction instruction, Runnable runnable) {
        LogUtil.i(TAG, "text -->> " + text);
        this.instruction = instruction;
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

    public static class Instruction {
        public SpeechModel speechModel;
        public SpeechModel matching(String speechText) {
            return null;
        }
    }
}