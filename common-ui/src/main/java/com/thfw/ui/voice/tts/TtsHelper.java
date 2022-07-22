package com.thfw.ui.voice.tts;

import android.media.AudioManager;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.thfw.base.ContextApp;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.InformantUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.ui.voice.VoiceType;
import com.thfw.ui.voice.VoiceTypeManager;

import java.io.File;

/**
 * Author:pengs
 * Date: 2021/11/23 14:08
 * Describe:Todo
 */
public class TtsHelper implements ITtsFace {

    private static final String TAG = TtsHelper.class.getSimpleName();
    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    // 超长文本 分词设置4000个汉字/8192字节数
    private final static int TTS_BYTE_SIZE = 4000;


    public static final String WELCOME_TTS = "你好，我是小密，给你最贴心的心理服务";

    private SpeechSynthesizer mTts;
    private CustomSynthesizerListener customSynthesizerListener;

    private static TtsHelper ttsHelper;

    private TtsModel ttsModel;
    private SynthesizerListener currentSynthesizerListener;
    private SimpleExoPlayer exoPlayer;

    private TtsHelper() {
        customSynthesizerListener = new CustomSynthesizerListener();
    }

    public static TtsHelper getInstance() {
        if (ttsHelper == null) {
            synchronized (TtsHelper.class) {
                if (ttsHelper == null) {
                    ttsHelper = new TtsHelper();
                    ttsHelper.init();
                }
            }
        }
        return ttsHelper;
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
        mTts.setParameter(SpeechConstant.VOICE_NAME, InformantUtil.getInformant());
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
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
    }


    @Override
    public void init() {
        if (mTts == null) {
            synchronized (TtsHelper.class) {
                if (mTts == null) {
                    mTts = SpeechSynthesizer.getSynthesizer();
                }
                if (mTts == null) {
                    mTts = SpeechSynthesizer.createSynthesizer(ContextApp.get(), code -> {
                        LogUtil.d(TAG, "InitListener init() code = " + code);
                        if (code != ErrorCode.SUCCESS) {
                            LogUtil.d(TAG, "InitListener init() Fail ");
                        }
                    });
                }
            }
        }
        setTtsParam();
    }

    @Override
    public void prepare() {
        if (!initialized()) {
            VoiceTypeManager.getManager().setVoiceType(VoiceType.READ_PREPARE);
            init();
        } else {
            if (mTts != null) {
                //设置发音人
                mTts.setParameter(SpeechConstant.VOICE_NAME, InformantUtil.getInformant());
            }
        }

    }

    @Override
    public boolean start(TtsModel ttsModel, SynthesizerListener synthesizerListener) {
        this.ttsModel = ttsModel;
        this.currentSynthesizerListener = synthesizerListener;
        return start();
    }

    public void setCurrentSynthesizerListener(SimpleSynthesizerListener currentSynthesizerListener) {
        this.currentSynthesizerListener = currentSynthesizerListener;
    }

    @Override
    public boolean start() {
        if (ttsModel == null) {
            LogUtil.e(TAG, "ttsModel not is null");
            return false;
        }
        prepare();
        if (!initialized()) {
            LogUtil.e(TAG, "mTts not is null");
            return false;
        }
//        setTtsParam();
        VoiceTypeManager.getManager().setVoiceType(VoiceType.READ_START);
        LogUtil.d(TAG, "ttsModel.text = " + ttsModel.text);
        if (ttsModel.cache) {
            String cacheFile = getCacheFile(ttsModel.text);
            if (new File(cacheFile).exists()) {
                // 播放本地缓存
                playCacheFile(cacheFile);
                return true;
            } else if (WELCOME_TTS.equals(ttsModel.text)) {
                playCacheFile(getAssetsFile(ttsModel.text));
                return true;
            }
            mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, cacheFile);
        } else {
            setTtsParam();
        }
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
        return (initialized() && mTts.isSpeaking()) || (exoPlayer != null && exoPlayer.isPlaying());
    }

    @Override
    public void stop() {
        if (initialized()) {
            mTts.stopSpeaking();
        }
        onExoRelease();
    }

    public void destroy() {
        if (initialized()) {
            mTts.destroy();
            mTts = null;
        }
        onExoRelease();
    }

    public void pause() {
        if (initialized()) {
            mTts.pauseSpeaking();
        }
        onExoPause();
    }

    public void resume() {
        if (initialized()) {
            mTts.resumeSpeaking();
        }
        onExoResume();
    }

    public static class SimpleSynthesizerListener implements SynthesizerListener {

        @Override
        public void onSpeakBegin() {
            onIng(ttsHelper.isIng());
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
            onIng(ttsHelper.isIng());
        }

        @Override
        public void onSpeakPaused() {
            onIng(ttsHelper.isIng());
        }

        @Override
        public void onSpeakResumed() {
            onIng(ttsHelper.isIng());
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
            onIng(ttsHelper.isIng());
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            onIng(ttsHelper.isIng());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            onIng(ttsHelper.isIng());
        }

        public void onIng(boolean ing) {

        }
    }

    public class CustomSynthesizerListener implements SynthesizerListener {

        @Override
        public void onSpeakBegin() {
            onExoRelease();
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
            onExoRelease();
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


    /**
     * 播放本地缓存文件
     *
     * @param cacheFile
     */
    private void playCacheFile(String cacheFile) {
        if (!ToastUtil.isMainThread()) {
            HandlerUtil.getMainHandler().post(() -> {
                playCacheFile(cacheFile);
            });
            return;
        }
        onExoRelease();
        exoPlayer = new SimpleExoPlayer.Builder(ContextApp.get())
                .setAudioAttributes(AudioAttributes.DEFAULT, true)
                .build();
        exoPlayer.addMediaItem(MediaItem.fromUri(cacheFile));
        exoPlayer.prepare();
        exoPlayer.play();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    onExoRelease();
                    LogUtil.i(TAG, "playCacheFile complete");
                    if (currentSynthesizerListener != null) {
                        currentSynthesizerListener.onCompleted(new SpeechError(0));
                    }
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                LogUtil.i(TAG, "playCacheFile error");
                onExoRelease();
                if (currentSynthesizerListener != null) {
                    currentSynthesizerListener.onEvent(0, 0, 0, null);
                }
            }
        });
        if (currentSynthesizerListener != null) {
            currentSynthesizerListener.onSpeakBegin();
        }
        LogUtil.i(TAG, "playCacheFile start");
    }

    private void onExoRelease() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void onExoResume() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void onExoPause() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * tts 缓存文件 根据文本生成【全路径文件名】
     *
     * @param ttsText
     * @return
     */
    public String getCacheFile(String ttsText) {
        String fileName = ttsText.hashCode() + "_" + InformantUtil.getInformant();
        return ContextApp.get().getCacheDir().getAbsolutePath() + "/msc/" + fileName + ".wav";
    }

    public String getAssetsFile(String ttsText) {
        String fileName = ttsText.hashCode() + "_" + InformantUtil.getInformant();
        return "file:///android_asset/" + fileName + ".wav";
    }


}
