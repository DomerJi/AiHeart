package com.thfw.mobileheart.aiui;

import android.content.Context;
import android.media.AudioAttributes;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;

import java.util.Locale;

/**
 * Author:pengs
 * Date: 2021/8/3 16:48
 * Describe:Todo
 */
public class TTSAndroidManager {

    private TextToSpeech tts;
    private String ttsText;
    private boolean inited = false;

    public TTSAndroidManager(Context context) {
        tts = new TextToSpeech(context, new listener());
        tts.setPitch(1.0f);
        tts.setSpeechRate(0.45f);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
            tts.setAudioAttributes(audioAttributes);
        }
    }


    public void speak(String ttsText) {
        this.ttsText = ttsText;
        if (EmptyUtil.isEmpty(ttsText)) {
            return;
        }
        if (inited) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    LogUtil.d("onStart utteranceId -> " + utteranceId);
                }

                @Override
                public void onDone(String utteranceId) {
                    LogUtil.d("onDone utteranceId -> " + utteranceId);
                }

                @Override
                public void onError(String utteranceId) {
                    LogUtil.d("onError utteranceId -> " + utteranceId);
                }
            });
            tts.speak(ttsText, TextToSpeech.QUEUE_ADD, null);
        }
    }

    private class listener implements TextToSpeech.OnInitListener {

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                //设置播放语言
                int result = tts.setLanguage(Locale.CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ToastUtil.show("不支持");
                } else if (result == TextToSpeech.LANG_AVAILABLE) {
                    //初始化成功之后才可以播放文字
                    //否则会提示“speak failed: not bound to tts engine
                    //TextToSpeech.QUEUE_ADD会将加入队列的待播报文字按顺序播放
                    //TextToSpeech.QUEUE_FLUSH会替换原有文字
                    inited = true;
                    speak(ttsText);
                }

            } else {
                Log.e("TAG", "初始化失败");
            }

        }

        public void stopTTS() {
            if (tts != null) {
                tts.shutdown();
                tts.stop();
                tts = null;
            }
        }
    }
}
