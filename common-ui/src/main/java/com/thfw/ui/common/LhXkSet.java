package com.thfw.ui.common;

import com.thfw.base.face.LhXkListener;
import com.thfw.base.utils.SharePreferenceUtil;

import static com.thfw.ui.common.LhXkSettingActivity.KEY_FACE_FOCUS;

public class LhXkSet {
    // 语音识别 是否开启
    public static int voiceOpen = LhXkSettingActivity.KEY_VOICE_DEFALUT;
    // 语音识别 是否文字上屏
    public static int voiceTextOpen = LhXkSettingActivity.KEY_VOICE_TEXT_DEFALUT;
    // 语音识别 文字上屏 位置
    public static int voiceTextGravity = LhXkSettingActivity.KEY_VOICE_TEXT_GRAVITY_DEFALUT;
    // 焦点跟随 是否开启
    public static int focusOpen = LhXkSettingActivity.KEY_FACE_FOCUS_DEFALUT;
    // 焦点跟随 距离
    public static float focusM = LhXkSettingActivity.KEY_FACE_FOCUS_M_DEFALUT;
    // 别看我后 记录单人还是多人
    public static int focusNoseeModel = LhXkSettingActivity.KEY_FACE_FOCUS_NOSEE_MODEL_DEFALUT;
    // 别看我后 持续多长时间
    public static int focusNoseeTime = LhXkSettingActivity.KEY_FACE_FOCUS_NOSEE_TIME_DEFALUT;

    public static void refreshLhXkSet() {
        refreshLhXkSet(true);
    }

    public static void init() {
        refreshLhXkSet(false);
    }

    public static void refreshLhXkSet(boolean changed) {
        LhXkSet.voiceOpen = SharePreferenceUtil.getInt(LhXkSettingActivity.KEY_VOICE,
                LhXkSettingActivity.KEY_VOICE_DEFALUT);
        LhXkSet.voiceTextOpen = SharePreferenceUtil.getInt(LhXkSettingActivity.KEY_VOICE_TEXT,
                LhXkSettingActivity.KEY_VOICE_TEXT_DEFALUT);
        LhXkSet.voiceTextGravity = SharePreferenceUtil.getInt(LhXkSettingActivity.KEY_VOICE_TEXT_GRAVITY,
                LhXkSettingActivity.KEY_VOICE_TEXT_GRAVITY_DEFALUT);

        LhXkSet.focusOpen = SharePreferenceUtil.getInt(KEY_FACE_FOCUS,
                LhXkSettingActivity.KEY_FACE_FOCUS_DEFALUT);
        LhXkSet.focusM = SharePreferenceUtil.getFloat(LhXkSettingActivity.KEY_FACE_FOCUS_M,
                LhXkSettingActivity.KEY_FACE_FOCUS_M_DEFALUT);
        LhXkSet.focusNoseeModel = SharePreferenceUtil.getInt(LhXkSettingActivity.KEY_FACE_FOCUS_NOSEE_MODEL,
                LhXkSettingActivity.KEY_FACE_FOCUS_NOSEE_MODEL_DEFALUT);
        LhXkSet.focusNoseeTime = SharePreferenceUtil.getInt(LhXkSettingActivity.KEY_FACE_FOCUS_NOSEE_TIME,
                LhXkSettingActivity.KEY_FACE_FOCUS_NOSEE_TIME_DEFALUT);
    }

    static LhXkListener lhXkListener;

    public static void setLhXkListener(LhXkListener lhXkListener) {
        LhXkSet.lhXkListener = lhXkListener;
    }
}