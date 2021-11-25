package com.thfw.ui.voice.speech;

import android.media.MediaRecorder;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.thfw.base.ContextApp;
import com.thfw.base.utils.LogUtil;
import com.thfw.ui.voice.VoiceType;
import com.thfw.ui.voice.VoiceTypeManager;

/**
 * Author:pengs
 * Date: 2021/11/23 14:26
 * Describe:Todo
 */
public class SpeechHelper implements ISpeechFace {


    private static final String TAG = SpeechHelper.class.getSimpleName();
    private static final long VAD_EOS = 1600;// 语音后端点（说完后1.6秒无语音结束对话）
    private static final long VAD_BOS = 6000;// 语音前端点（开始后6秒无语音结束对话）
    private static final String DEFAULT_TEXT = "...";
    private static final String DEFAULT_EMPTY = "";

    private SpeechRecognizer mIat;
    private CustomRecognizerListener mListener;

    @Override
    public boolean initialized() {
        return mIat != null;
    }

    @Override
    public void init() {
        LogUtil.i(TAG, "init start");
        // 初始化识别无 UI 识别对象
        mIat = SpeechRecognizer.createRecognizer(ContextApp.get(), new InitListener() {
            @Override
            public void onInit(int code) {
                LogUtil.d(TAG, "init code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    LogUtil.d(TAG, "init fail");
                }
            }
        });
        setSpeechParam();
    }


    /**
     * 参数设置
     *
     * @return
     */
    private void setSpeechParam() {
        if (mIat == null) {
            return;
        }
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, String.valueOf(VAD_BOS));
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, String.valueOf(VAD_EOS));
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.MIC);
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, ContextApp.get().getCacheDir().getAbsolutePath() + "/msc/iat.wav");
        // 通过设置此参数可偏向输出数字结果格式
        mIat.setParameter("nunum", "1");
    }


    @Override
    public void prepare() {
        if (!initialized()) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_PREPARE);
            init();
        }
    }

    @Override
    public boolean start() {
        prepare();
        VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_START);
        if (ErrorCode.SUCCESS == mIat.startListening(mListener)) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_ING);
            return true;
        } else {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_STOP);
            return false;
        }
    }

    @Override
    public boolean isIng() {
        return initialized() && mIat.isListening();
    }

    @Override
    public void stop() {
        if (initialized()) {
            mIat.stopListening();
        }
        VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_STOP);
    }

    @Override
    public void onResult(StringBuilder stringBuilder, boolean append, boolean end) {

    }

    public class CustomRecognizerListener implements RecognizerListener {

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_TEXT);
        }

        @Override
        public void onError(SpeechError speechError) {
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    }
}
