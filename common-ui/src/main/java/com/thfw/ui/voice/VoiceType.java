package com.thfw.ui.voice;

/**
 * Author:pengs
 * Date: 2021/11/22 15:50
 * Describe:语音听、读、执行中、执行成功、执行失败
 */
public enum VoiceType {
    // 默认 - 等待开始
    NORMAL,

    // 【听】准备->开始->倾听中->结束->识别结果
    ACCEPT_PREPARE,
    ACCEPT_START,
    ACCEPT_ING,
    ACCEPT_STOP,
    ACCEPT_TEXT,

    /**
     * 【读】文本->准备->开始->播报中->结束
     */
    READ_TEXT,
    READ_PREPARE,
    READ_START,
    READ_ING,
    READ_STOP,

    /**
     * 【唤醒】准备->开始->等待唤醒中->成功唤醒(唤醒文本)->结束
     */
    WAKE_UP_PREPARE,
    WAKE_UP_START,
    WAKE_UP_ING,
    WAKE_UP_SUCCESS,
    WAKE_UP_STOP,

    // 开始执行
    EXECUTE_START,
    // 执行中
    EXECUTE_ING,
    // 执行失败
    EXECUTE_FAIL,
    // 执行成功
    EXECUTE_SUCCESS,
}
