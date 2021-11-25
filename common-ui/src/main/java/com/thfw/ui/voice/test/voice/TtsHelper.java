//package com.thfw.ui.voice.test.voice;
//
//import android.app.Activity;
//import android.content.SharedPreferences;
//import android.media.AudioManager;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.iflytek.cloud.ErrorCode;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechError;
//import com.iflytek.cloud.SpeechSynthesizer;
//import com.iflytek.cloud.SynthesizerListener;
//import com.myai.base.ContextApp;
//import com.myai.base.manager.VolumeManager;
//import com.thfw.ui.voice.speech.SpeechHelper;
//
//import java.util.Arrays;
//
///**
// * Author:pengs
// * Date: 2021/8/10 13:14
// * Describe:语音播报
// */
//public class TtsHelper {
//
//    public static final String WAKE_WROD = "小密";
//    private static final String TAG = TtsHelper.class.getSimpleName();
//    private static final String PREFER_NAME = "cn.soulbuddy.xymy.activity.setting";
//    private static final String voicerCloud = "x2_yifei";//"xiaoyan";//"aisjinger";//"aisjiuxu";//"aisxping";//
//    // 采样率
//    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
//    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
//    private final static int AUDIO_SAMPLE_RATE = 16000;
//    // 超长文本 分词设置4000个汉字/8192字节数
//    private final static int TTS_BYTE_SIZE = 4000;
//    private SpeechSynthesizer mTts;
//    private SharedPreferences mSharedPreferences;
//
//    private SynthesizerListener mTtsListener;
//    private String mWords;
//    // 超过8192字节使用
//    private String[] mWordArray;
//    private int mWordArrayIndex = 0;
//    private int progress;
//    private boolean isSpeaking;
//    private SynthesizerListener synthesizerListener = new SynthesizerListener() {
//
//        @Override
//        public void onSpeakBegin() {
//            isSpeaking = true;
//            SpeechHelper.getInstance().stopRecognize(0);
//            Log.i(TAG, "onSpeakBegin");
//            if (isNext()) {
//                return;
//            }
//            if (mTtsListener != null) {
//                mTtsListener.onSpeakBegin();
//            }
//        }
//
//        @Override
//        public void onBufferProgress(int i, int i1, int i2, String s) {
//            Log.i(TAG, "onBufferProgress");
//            if (isNext()) {
//                return;
//            }
//            if (mTtsListener != null) {
//                mTtsListener.onBufferProgress(i, i1, i2, s);
//            }
//        }
//
//        @Override
//        public void onSpeakPaused() {
//            Log.i(TAG, "onSpeakPaused");
//            isSpeaking = false;
//            if (isNext()) {
//                return;
//            }
//            if (mTtsListener != null) {
//                mTtsListener.onSpeakPaused();
//            }
//        }
//
//        @Override
//        public void onSpeakResumed() {
//            isSpeaking = true;
//            SpeechHelper.getInstance().stopRecognize(0);
//            Log.i(TAG, "onSpeakResumed");
//            if (isNext()) {
//                return;
//            }
//            if (mTtsListener != null) {
//                mTtsListener.onSpeakResumed();
//            }
//        }
//
//        @Override
//        public void onSpeakProgress(int i, int i1, int i2) {
//            progress = i;
//            if (isNext()) {
//                return;
//            }
//            Log.i(TAG, "onSpeakProgress progress =" + i + "% ; beginPos = " + i1 + ";endPos = " + i2);
//            if (mTtsListener != null) {
//                mTtsListener.onSpeakProgress(i, i1, i2);
//            }
//        }
//
//        @Override
//        public void onCompleted(SpeechError speechError) {
//            isSpeaking = false;
//            Log.i(TAG, "onCompleted");
//            mWords = "";
//            if (isNext()) {
//                mWordArrayIndex++;
//                startSpeaking(mWordArray[mWordArrayIndex], null);
//                return;
//            }
//            if (mTtsListener != null) {
//                mTtsListener.onCompleted(speechError);
//            }
//        }
//
//        @Override
//        public void onEvent(int i, int i1, int i2, Bundle bundle) {
//            Log.i(TAG, "onEvent" + i);
//            if (isNext()) {
//                return;
//            }
//            if (mTtsListener != null) {
//                mTtsListener.onEvent(i, i1, i2, bundle);
//            }
//        }
//    };
//
//    private TtsHelper() {
//        // audioRecord能接受的最小的buffer大小
//        mSharedPreferences = ContextApp.get().getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
//        init();
//    }
//
//    public static TtsHelper getInstance() {
//        return Factory.ttsHelper;
//    }
//
//    private void init() {
//        mTts = SpeechSynthesizer.createSynthesizer(ContextApp.get(), code -> {
//            Log.d(TAG, "InitListener init() code = " + code);
//            if (code == ErrorCode.SUCCESS) {
//            }
//        });
//    }
//
//    private void setTtsParam() {
//        // 清空参数
//        mTts.setParameter(SpeechConstant.PARAMS, null);
//
//        //设置使用云端引擎
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//        //设置发音人
//        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerCloud);
//
//        //设置合成语速
//        mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
//        //设置合成音调
//        mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
//        //设置合成音量
//        mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
//        //设置播放器音频流类型
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC + "");
//
//        // 设置播放合成音频打断音乐播放，默认为true
//        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
////        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
////
////        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, ContextApp.get().getCacheDir().getAbsolutePath() + "/msc/tts.wav");
//
//    }
//
//    public String getWords() {
//        return mWords == null ? "" : mWords;
//    }
//
//    /**
//     * 语音播放位置是否包含小密
//     *
//     * @return
//     */
//    public boolean isSpeakingWakeup() {
//        String words = mWords;
//        if (TextUtils.isEmpty(words)) {
//            return false;
//        }
//
//        if (VolumeManager.getVolumeFlag() < 0.35f) {
//            return false;
//        }
//
//        /*
//         * ”小密想问“下 小密想问会误唤醒
//         */
//        if (words.contains(WAKE_WROD)) {
//            // 偏移量，根据语音播放进度和唤醒后的差距进行设置
//            // 唤醒词4个字+3个偏移量
//            int offset = 7;
//            int len = words.length();
//            int progressPos = len * progress / 100;
//            int formindex = progressPos >= offset ? progressPos - offset : 0;
//            if (formindex >= len) {
//                return false;
//            }
//            int index = words.indexOf(WAKE_WROD, formindex);
//            if (index == -1) {
//                return false;
//            }
//            if (index < progressPos && progressPos - index <= offset) {
//                Log.d(TAG, "isSpeakingWakeup 【true】offset = " + offset + "; progressPos = " + progressPos + "; index = " + index);
//                return true;
//            } else {
//                Log.d(TAG, "isSpeakingWakeup 【false】offset = " + offset + "; progressPos = " + progressPos + "; index = " + index);
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
//    public void startSpeaking(String words, SynthesizerListener mListener) {
//
//        if (words == null) {
//            return;
//        }
//
//        int len = words.length();
//        if (len == 0) {
//            return;
//        }
//
//        if (SpeechHelper.getInstance().isSpeechBuffer()) {
//            Log.d(TAG, "isSpeechBuffer true");
//            return;
//        }
//
//        if (mListener != null) {
//            mTtsListener = mListener;
//        }
//
//        if (mTts == null) {
//            init();
//            if (mTts == null) {
//                return;
//            }
//        }
//
//        if (len > TTS_BYTE_SIZE) {
//            limitBigWords(len, words);
//        } else {
//            if (isTtsSpeaking() && words.equals(mWords)) {
//                Log.d(TAG, "words.equals(mWords) is true");
//                return;
//            }
//            mWords = words;
//        }
//        setTtsParam();
//        int code = mTts.startSpeaking(mWords, synthesizerListener);
//        if (code != ErrorCode.SUCCESS) {
//            //showTip(getText(R.string.tts_fail) + "" + code);
//            Log.e(TAG, "startSpeaking" + code);
//        }
//    }
//
//    public void removeTtsListener() {
//        this.mTtsListener = null;
//    }
//
//    /**
//     * 分词 超长文本8192字节使用
//     *
//     * @param len
//     * @param words
//     */
//    private void limitBigWords(int len, String words) {
//        int page = len / TTS_BYTE_SIZE;
//        page = len % TTS_BYTE_SIZE > 0 ? page + 1 : page;
//        mWordArray = new String[page];
//        int start = 0;
//        int end = 0;
//        for (int i = 0; i < page; i++) {
//            if (i == 0) {
//                start = 0;
//                end = (i + 1) * TTS_BYTE_SIZE;
//            } else if (i == (page - 1)) {
//                start = end;
//                end = len;
//            } else {
//                start = end;
//                end = (i + 1) * TTS_BYTE_SIZE;
//            }
//            Log.d(TAG, "start = " + start + "; end = " + end);
//            mWordArray[i] = words.substring(start, end);
//        }
//        Log.d(TAG, "mWordArray -> " + Arrays.toString(mWordArray));
//        mWordArrayIndex = 0;
//        mWords = mWordArray[mWordArrayIndex];
//    }
//
//    private boolean isNext() {
//        if (mWordArray != null && mWordArray.length > 0) {
//            if (mWordArrayIndex + 1 < mWordArray.length) {
//                return true;
//            }
//        }
//        resetWordArray();
//        return false;
//    }
//
//    private void resetWordArray() {
//        mWordArrayIndex = 0;
//        mWordArray = null;
//    }
//
//    public void stopTTS() {
//        if (mTts != null) {
//            isSpeaking = false;
//            mTts.stopSpeaking();
//        }
//        resetWordArray();
//    }
//
//    public void pauseTTS() {
//        if (mTts != null) {
//            if (isTtsSpeaking()) {
//                isSpeaking = false;
//                mTts.pauseSpeaking();
//            } else {
//                isSpeaking = false;
//            }
//        }
//    }
//
//    public boolean isTtsSpeaking() {
//        return mTts != null && mTts.isSpeaking();
//    }
//
//    public boolean isSpeaking() {
//        return isSpeaking;
//    }
//
//    public void resumeTTS() {
//        if (mTts != null) {
//            if (isTtsSpeaking()) {
//                isSpeaking = true;
//                mTts.resumeSpeaking();
//            } else {
//                isSpeaking = false;
//            }
//        }
//    }
//
//    private static class Factory {
//        private static final TtsHelper ttsHelper = new TtsHelper();
//    }
//}
