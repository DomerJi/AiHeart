package com.thfw.ui.voice.tts;

import android.media.AudioManager;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.thfw.base.ContextApp;
import com.thfw.base.utils.LogUtil;
import com.thfw.ui.voice.VoiceType;
import com.thfw.ui.voice.VoiceTypeManager;

/**
 * Author:pengs
 * Date: 2021/11/23 14:08
 * Describe:Todo
 */
public class TtsHelper implements ITtsFace {

    private static final String TAG = TtsHelper.class.getSimpleName();

    // "xiaoyan"; // "aisjinger"; // "aisjiuxu"; // "aisxping";
    private static final String voicerCloud = "x2_yifei";
    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    // 超长文本 分词设置4000个汉字/8192字节数
    private final static int TTS_BYTE_SIZE = 4000;

    private SpeechSynthesizer mTts;
    private CustomSynthesizerListener customSynthesizerListener;

    private TtsModel ttsModel;
    private CustomSynthesizerListener currentSynthesizerListener;

    private TtsHelper() {
        customSynthesizerListener = new CustomSynthesizerListener();
    }

    @Override
    public boolean initialized() {
        return mTts != null;
    }

    private void setTtsParam() {
        if (mTts == null) {
            return;
        }
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置使用云端引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerCloud);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC + "");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");

//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, ContextApp.get().getCacheDir().getAbsolutePath() + "/msc/tts.wav");

    }


    @Override
    public void init() {
        mTts = SpeechSynthesizer.createSynthesizer(ContextApp.get(), code -> {
            LogUtil.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                LogUtil.d(TAG, "InitListener init() Fail ");
            }
        });
        setTtsParam();
    }

    @Override
    public void prepare() {
        if (!initialized()) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.READ_PREPARE);
            init();
        }
    }

    @Override
    public boolean start(TtsModel ttsModel, CustomSynthesizerListener synthesizerListener) {
        this.ttsModel = ttsModel;
        this.currentSynthesizerListener = synthesizerListener;
        return start();
    }

    @Override
    public boolean start() {
        if (ttsModel == null) {
            LogUtil.e(TAG, "ttsModel not is null");
            return false;
        }
        prepare();
        VoiceTypeManager.getManager().setVoiceType(VoiceType.READ_START);
        if (ErrorCode.SUCCESS == mTts.startSpeaking(ttsModel.text, customSynthesizerListener)) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.READ_ING);
            return true;
        } else {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.READ_STOP);
            return false;
        }

    }

    @Override
    public boolean isIng() {
        return initialized() && mTts.isSpeaking();
    }

    @Override
    public void stop() {
        if (initialized()) {
            mTts.stopSpeaking();
        }
    }

    public class CustomSynthesizerListener implements SynthesizerListener {

        @Override
        public void onSpeakBegin() {
            if (currentSynthesizerListener != null) {
                currentSynthesizerListener.onSpeakBegin();
            }
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
            if (currentSynthesizerListener != null) {
                currentSynthesizerListener.onBufferProgress(i, i1, i2, s);
            }
        }

        @Override
        public void onSpeakPaused() {
            if (currentSynthesizerListener != null) {
                currentSynthesizerListener.onSpeakPaused();
            }
        }

        @Override
        public void onSpeakResumed() {
            if (currentSynthesizerListener != null) {
                currentSynthesizerListener.onSpeakResumed();
            }
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
            if (currentSynthesizerListener != null) {
                currentSynthesizerListener.onSpeakProgress(i, i1, i2);
            }
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (currentSynthesizerListener != null) {
                currentSynthesizerListener.onCompleted(speechError);
            }
            VoiceTypeManager.getManager().setVoiceType(VoiceType.READ_STOP);
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            if (currentSynthesizerListener != null) {
                currentSynthesizerListener.onEvent(i, i1, i2, bundle);
            }
        }
    }


}