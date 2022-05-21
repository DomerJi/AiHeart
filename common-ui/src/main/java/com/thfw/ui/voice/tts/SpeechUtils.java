package com.thfw.ui.voice.tts;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.thfw.base.utils.LogUtil;

import java.util.Locale;


public class SpeechUtils {

    private TextToSpeech textToSpeech; // TTS对象

    public SpeechUtils(Context context) {
        textToSpeech = new TextToSpeech(context.getApplicationContext(), (int i) -> {
            LogUtil.e("TestBug", "initCode = " + i);
            if (i == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.CHINA);
                textToSpeech.setPitch(1.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                textToSpeech.setSpeechRate(1.0f);
            }
        });
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                LogUtil.e("TestBug", "开始阅读");
            }

            @Override
            public void onDone(String utteranceId) {
                LogUtil.e("TestBug", "阅读完毕11111");
            }

            @Override
            public void onError(String utteranceId) {
                LogUtil.e("TestBug", "阅读出错");
            }
        });
    }

    public void speakText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (textToSpeech != null) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UniqueID");
            }
        }
    }

    public void ttsStop() {
        if (null != textToSpeech) {
            textToSpeech.stop();
        }
    }

    public void ttsDestory() {
        if (null != textToSpeech) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}