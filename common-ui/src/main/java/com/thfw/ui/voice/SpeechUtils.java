package com.thfw.ui.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

 

public class SpeechUtils {
    private Context context;


    private static final String TAG = "SpeechUtils";
    private static SpeechUtils singleton;

    private TextToSpeech textToSpeech; // TTS对象

    public static SpeechUtils getInstance(Context context) {
        if (singleton == null) {
            synchronized (SpeechUtils.class) {
                if (singleton == null) {
                    singleton = new SpeechUtils(context);
                }
            }
        }
        return singleton;
    }

    public SpeechUtils(Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    //textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setLanguage(Locale.CHINA);
                    textToSpeech.setPitch(0.95f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    textToSpeech.setSpeechRate(1.2f);

                }
            }
        });
    }

    public void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text,
                    TextToSpeech.QUEUE_FLUSH, null);
        }

    }

}