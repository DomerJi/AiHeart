package com.thfw.base.models;

import android.util.Log;

/**
 * Author:pengs
 * Date: 2022/12/15 16:12
 * Describe:Todo
 */
public class SpeechModel {

    public String text;
    public String outText;
    public String message;
    public int type;
    public boolean matches;

    public SpeechModel setMatches(boolean matches) {
        this.matches = matches;
        return this;
    }

    public static class Type {
        public static final int ING = 0;
        public static final int SUCCESS = 1;
        public static final int FAIL = 2;
    }

    private static SpeechModel speechModel;

    private SpeechModel() {
    }

    public static SpeechModel create(String text) {
        if (speechModel == null) {
            speechModel = new SpeechModel();
        }
        speechModel.text = text;
        speechModel.type = Type.ING;
        return speechModel;
    }

    public SpeechModel setOutText(String outText) {
        Log.i("SpeechModel", "outText -> " + outText);
        this.outText = outText;
        return this;
    }

    public String getOutText() {
        return outText;
    }

    public SpeechModel setType(int type) {
        this.type = type;
        return this;
    }

    public SpeechModel setMessage(String message) {
        this.message = message;
        return this;
    }
}
