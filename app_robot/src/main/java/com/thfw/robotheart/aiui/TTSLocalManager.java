package com.thfw.robotheart.aiui;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;

public class TTSLocalManager {

    private static final String TAG = "TTSLocalManager";
    public static String voicerLocal = "xiaoyan";
    public static String voicerXtts = "xiaofeng";
    private final SpeechSynthesizer mTts;
    String mEngineType = SpeechConstant.TYPE_XTTS;
    private Context mContext;

    public TTSLocalManager(Context context, InitListener initListener) {
        // 初始化合成对象
        this.mContext = context;
        mTts = SpeechSynthesizer.createSynthesizer(context, initListener);

    }

    public void destroy() {
        if (mTts != null) {
            mTts.destroy();
        }
    }

    /**
     * 参数设置
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
            //设置使用本地引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicerXtts);
        }
        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, SharePreferenceUtil.getString("speed_preference", "50"));
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, SharePreferenceUtil.getString("pitch_preference", "50"));
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, SharePreferenceUtil.getString("volume_preference", "50"));
        //设置播放器音频流类型
//        AudioManager.STREAM_SYSTEM
//        AudioAttributes.CONTENT_TYPE_SPEECH
        mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_NOTIFICATION+"");
        //	mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.BACKGROUND_SOUND, "1");

        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");


    }

    //获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        String type = "tts";
        if (mEngineType.equals(SpeechConstant.TYPE_XTTS)) {
            type = "xtts";
        }
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, type + "/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        if (mEngineType.equals(SpeechConstant.TYPE_XTTS)) {
            tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + voicerXtts + ".jet"));
        } else {
            tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + voicerLocal + ".jet"));
        }

        return tempBuffer.toString();
    }

    public void start(String text, SynthesizerListener mTtsListener) {
        // 开始合成
        // 收到onCompleted 回调时，合成结束、生成合成音频
        // 合成的音频格式：只支持pcm格式
        setParam();
        Log.d(TAG, "准备点击： " + System.currentTimeMillis());
        int code = mTts.startSpeaking(text, mTtsListener);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            ToastUtil.show("语音合成失败,错误码: " + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        }
    }

    // 取消合成
    public void stopSpeaking() {
        if (mTts != null) {
            mTts.stopSpeaking();
        }
    }

    // 暂停播放
    public void pauseSpeaking() {
        if (mTts != null) {
            mTts.pauseSpeaking();
        }

    }

    // 继续播放
    public void resumeSpeaking() {
        if (mTts != null) {
            mTts.resumeSpeaking();
        }
    }

    public static class CustomSynthesizerListener implements SynthesizerListener {

        @Override
        public void onSpeakBegin() {
            LogUtil.e(TAG, "onSpeakBegin");
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
            LogUtil.e(TAG, "onBufferProgress i" + i + " -i1 =  " + i1 + "; i2 = " + i2);
        }

        @Override
        public void onSpeakPaused() {
            LogUtil.e(TAG, "onSpeakPaused");
        }

        @Override
        public void onSpeakResumed() {
            LogUtil.e(TAG, "onSpeakResumed");
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
            LogUtil.e(TAG, "onSpeakProgress");
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            LogUtil.e(TAG, "onCompleted");
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            LogUtil.e(TAG, "onEvent");
        }
    }

}
