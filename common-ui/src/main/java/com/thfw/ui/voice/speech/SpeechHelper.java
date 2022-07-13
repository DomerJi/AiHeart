package com.thfw.ui.voice.speech;

import android.media.MediaRecorder;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.thfw.base.ContextApp;
import com.thfw.base.utils.LogUtil;
import com.thfw.ui.voice.JsonParser;
import com.thfw.ui.voice.PolicyHelper;
import com.thfw.ui.voice.VoiceType;
import com.thfw.ui.voice.VoiceTypeManager;
import com.thfw.ui.voice.tts.TtsHelper;

/**
 * Author:pengs
 * Date: 2021/11/23 14:26
 * Describe:Todo
 */
public class SpeechHelper implements ISpeechFace {


    private static final String TAG = SpeechHelper.class.getSimpleName();
    private static final long VAD_EOS = 1800;// 语音后端点（说完后1.8秒无语音结束对话）
    private static final long VAD_EOS_2 = 2400;// 语音后端点（说完后2.4秒无语音结束对话）
    private static final long VAD_BOS = 10000;// 语音前端点（开始后6秒无语音结束对话）
    private StringBuilder mSpeechResult;

    private SpeechRecognizer mIat;
    private CustomRecognizerListener mListener;
    private static SpeechHelper speechHelper;
    private static int ingState = -1;

    private SpeechHelper() {
        mSpeechResult = new StringBuilder();
        mListener = new CustomRecognizerListener();
    }

    public void clearCacheText() {
        mSpeechResult.setLength(0);
    }

    public static SpeechHelper getInstance() {
        if (speechHelper == null) {
            synchronized (TtsHelper.class) {
                if (speechHelper == null) {
                    speechHelper = new SpeechHelper();
                    speechHelper.init();
                }
            }
        }
        return speechHelper;
    }

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
        mIat.setParameter(SpeechConstant.ASR_DWA, "wpgs");
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
        if (initialized()) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_START);
            // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
            mIat.setParameter(SpeechConstant.VAD_EOS, String.valueOf(PolicyHelper
                    .getInstance().isSwitchReadAfter() ? VAD_EOS_2 : VAD_EOS));
            if (ErrorCode.SUCCESS == mIat.startListening(mListener)) {
                VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_ING);
                return true;
            } else {
                VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_STOP);
                return false;
            }
        } else {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_STOP);
            return false;
        }
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
        ingState = -1;
        checkIngState();
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
        SpeechHelper.getInstance().clearCacheText();
        VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_STOP);
    }

    public void destroy() {
        if (initialized()) {
            mIat.destroy();
            mIat = null;
        }
    }

    public class CustomRecognizerListener implements RecognizerListener {

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {
            checkIngState();
        }

        @Override
        public void onEndOfSpeech() {
            isRestart();
            checkIngState();
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.ACCEPT_TEXT);
            String text = JsonParser.printResult(recognizerResult, isLast);
            LogUtil.i(TAG, "onResult -> text = " + text + "; isLast = " + isLast);

            if (PolicyHelper.getInstance().isPressMode()) {
                if (resultListener != null) {
                    resultListener.onResult(mSpeechResult.toString() + text, false, isLast);
                    if (isLast) {
                        mSpeechResult.append(text);
                    }
                }
            } else {
                if (resultListener != null) {
                    resultListener.onResult(text, false, isLast);
                }
            }
            isRestart();
            checkIngState();
        }

        @Override
        public void onError(SpeechError speechError) {

            LogUtil.i(TAG, "onError -> speechError = " + speechError.getErrorDescription());
            isRestart();
            checkIngState();
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            LogUtil.i(TAG, "onError -> i = " + i);
            if (SpeechEvent.EVENT_RECORD_STOP == i) {
                isRestart();
            }
            checkIngState();
        }
    }

    private void isRestart() {
        if ((PolicyHelper.getInstance().isSpeechMode()
                || PolicyHelper.getInstance().isPressMode())
                && !isIng()) {
            start();
        }
    }

    private void checkIngState() {
        if (resultListener != null) {
            int state = isIng() ? 1 : 0;
            if (ingState == -1 || state != ingState) {
                ingState = state;
                resultListener.onIng(isIng());
            }
        }
    }

    public ResultListener resultListener;

    public interface ResultListener {
        void onResult(String result, boolean append, boolean end);

        void onIng(boolean ing);
    }
}
