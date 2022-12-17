package com.thfw.ui.voice.wakeup;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.thfw.aiui.util.CaeWakeupHelper;
import com.thfw.base.ContextApp;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RobotUtil2;
import com.thfw.ui.voice.VoiceType;
import com.thfw.ui.voice.VoiceTypeManager;
import com.thfw.ui.voice.tts.TtsHelper;

/**
 * Author:pengs
 * Date: 2021/8/12 10:20
 * Describe:语音唤醒
 */
public class WakeupHelper implements IWakeUpFace {

    private static final String TAG = WakeupHelper.class.getSimpleName();

    // for wakeup
    private static final int curThresh = 1600;
    private static final String keep_alive = "1";
    private static final String ivwNetMode = "0";
    // 主要分为两种：唤醒（wakeup），唤醒识别（oneshot）
    private static final String IVW_SST_WAKEUP = "wakeup";
    private static final String IVW_SST_ONESHOT = "oneshot";
    private VoiceWakeuper mIvw;
    private static WakeupHelper wakeupHelper;
    private static boolean inited;

    public static WakeupHelper getInstance() {
        if (wakeupHelper == null) {
            synchronized (TtsHelper.class) {
                if (wakeupHelper == null) {
                    wakeupHelper = new WakeupHelper();
                    wakeupHelper.init();
                }
            }
        }
        return wakeupHelper;
    }

    private CustomWakeuperListener wakeuperListener;

    private WakeupHelper() {
        wakeuperListener = new CustomWakeuperListener();
    }


    @Override
    public boolean initialized() {
        return mIvw != null;
    }

    @Override
    public void init() {
        LogUtil.d(TAG, "init = " + mIvw);
        mIvw = VoiceWakeuper.getWakeuper();
        LogUtil.d(TAG, "init = " + mIvw);
        if (mIvw == null) {
            LogUtil.d(TAG, "createWakeuper start");
            mIvw = VoiceWakeuper.createWakeuper(ContextApp.get(), new InitListener() {
                @Override
                public void onInit(int code) {
                    LogUtil.d(TAG, "InitListener init() code = " + code);
                    if (code != ErrorCode.SUCCESS) {
                        LogUtil.e(TAG, "InitListener FAIL");
                    }
                }
            });
            Log.d(TAG, "createWakeuper end = " + mIvw);
            setIvmParams();
        } else {
            setIvmParams();
        }
    }

    private void setIvmParams() {
        if (mIvw == null) {
            return;
        }
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
        // 主要分为两种：唤醒（wakeup），唤醒识别（oneshot）
        mIvw.setParameter(SpeechConstant.IVW_SST, IVW_SST_WAKEUP);
        // 设置唤醒资源路径
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, IvmResPath.getResPath());
        // 设置持续进行唤醒
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
        // 设置闭环优化网络模式
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
        // 设置唤醒录音保存路径，保存最近一分钟的音频
//        mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, ContextApp.get().getCacheDir().getAbsolutePath() + "/msc/ivw.wav");
//        mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
//        mIvw.setParameter(SpeechConstant.NOTIFY_RECORD_DATA, "1");
    }


    @Override
    public void prepare() {
        if (!initialized()) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.WAKE_UP_PREPARE);
            init();
        }
    }

    @Override
    public boolean start() {
        if (RobotUtil2.isInstallRobot() && RobotUtil2.isEnableMic()) {
            CaeWakeupHelper.getInstance().setOnWakeUpListener(new CaeWakeupHelper.OnWakeUpListener() {
                @Override
                public void onWakeup(int angle, int beam) {
                    if (wakeUpListener != null) {
                        wakeUpListener.onWakeup(angle, beam);
                    }
                }
            });
            CaeWakeupHelper.getInstance().startRecord();
            return true;
        }
        prepare();
        if (initialized()) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.WAKE_UP_START);
            if (ErrorCode.SUCCESS == mIvw.startListening(wakeuperListener)) {
                VoiceTypeManager.getManager().setVoiceType(VoiceType.WAKE_UP_ING);
                return true;
            } else {
                VoiceTypeManager.getManager().setVoiceType(VoiceType.WAKE_UP_STOP);
                return false;
            }
        } else {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.WAKE_UP_STOP);
            return false;
        }
    }

    @Override
    public boolean isIng() {
        if (RobotUtil2.isInstallRobot() && RobotUtil2.isEnableMic()) {
            return CaeWakeupHelper.getInstance().isRecording();
        }
        return initialized() && mIvw.isListening();
    }

    @Override
    public void stop() {
        if (initialized()) {
            mIvw.stopListening();
            mIvw.destroy();
            mIvw = null;
        }
        if (RobotUtil2.isInstallRobot() && RobotUtil2.isEnableMic()) {
            try {
                if (CaeWakeupHelper.getInstance().isRecording()) {
                    CaeWakeupHelper.getInstance().destroyRecord();
                }
            } catch (Exception e) {
                LogUtil.d(TAG, "CaeWakeupHelper destroyRecord e " + e.toString());
            }
        }
        VoiceTypeManager.getManager().setVoiceType(VoiceType.WAKE_UP_STOP);
    }

    @Override
    public void destroy() {
        if (initialized()) {
            mIvw.stopListening();
            mIvw.destroy();
            mIvw = null;
        }
    }

    OnWakeUpListener wakeUpListener;

    public interface OnWakeUpListener {
        void onWakeup(int angle, int beam);

        void onError();
    }

    public void setWakeUpListener(OnWakeUpListener onWakeUpListener) {
        this.wakeUpListener = onWakeUpListener;
    }

    public class CustomWakeuperListener implements WakeuperListener {
        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onResult(WakeuperResult wakeuperResult) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.WAKE_UP_SUCCESS);
            if (wakeUpListener != null) {
                wakeUpListener.onWakeup(-1, -1);
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.WAKE_UP_STOP);
            if (wakeUpListener != null) {
                wakeUpListener.onError();
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            switch (i) {
                // 设置后 NOTIFY_RECORD_DATA 可回调
                case SpeechEvent.EVENT_RECORD_DATA:
                    byte[] recordBytes = bundle.getByteArray(SpeechEvent.KEY_EVENT_RECORD_DATA);
                    if (recordBytes == null) {
                        return;
                    }
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int i) {

        }
    }

    public static void initCae(Context context) {
        if (!inited) {
            CaeWakeupHelper.init(context);
            inited = true;
        }
    }
}
