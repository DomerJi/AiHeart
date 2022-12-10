package com.thfw.ui.voice.tts;

import android.os.Bundle;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

public class SimpleSynthesizerListener implements SynthesizerListener {

    ITtsFace iTtsFace;

    public SimpleSynthesizerListener() {
        this.iTtsFace = TtsHelper.getInstance();
    }

    @Override
    public void onSpeakBegin() {
        onIng(iTtsFace.isIng());
    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {
        onIng(iTtsFace.isIng());
    }

    @Override
    public void onSpeakPaused() {
        onIng(iTtsFace.isIng());
    }

    @Override
    public void onSpeakResumed() {
        onIng(iTtsFace.isIng());
    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {
        onIng(iTtsFace.isIng());
    }

    @Override
    public void onCompleted(SpeechError speechError) {
        onIng(iTtsFace.isIng());
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {
        onIng(iTtsFace.isIng());
    }

    public void onIng(boolean ing) {

    }
}