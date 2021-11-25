package com.thfw.ui.voice.tts;

/**
 * Author:pengs
 * Date: 2021/11/23 16:37
 * Describe:Todo
 */
public class TtsModel {

    public TtsType ttsType;
    public String text;

    public TtsModel(String text) {
        this.ttsType = TtsType.READ_NORMAL;
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
