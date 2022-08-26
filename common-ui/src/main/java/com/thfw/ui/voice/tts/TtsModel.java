package com.thfw.ui.voice.tts;

import android.text.TextUtils;

/**
 * Author:pengs
 * Date: 2021/11/23 16:37
 * Describe:Todo
 */
public class TtsModel {

    public TtsType ttsType;
    public String text;
    public boolean cache = true;

    public TtsModel(String text) {
        if (TextUtils.isEmpty(text)) {
            this.text = "";
        } else {
            this.text = text.replaceAll("( |&nbsp;|<br>|</p>|<p>)", "");
        }
        this.ttsType = TtsType.READ_NORMAL;
    }

    public TtsModel setCache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public TtsModel(String text, TtsType ttsType) {
        this.ttsType = ttsType;
    }

    public enum TtsType {
        /**
         * 普通文本
         */
        READ_NORMAL,

        /**
         * 【系统播报】欢迎-拜拜-应答-提醒
         */
        READ_SYSTEM_WELCOME,
        READ_SYSTEM_BYE_BYE,
        READ_SYSTEM_REPLY,
        READ_SYSTEM_HINT,

    }
}
