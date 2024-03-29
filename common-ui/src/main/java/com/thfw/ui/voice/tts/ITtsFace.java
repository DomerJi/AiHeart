package com.thfw.ui.voice.tts;

import com.iflytek.cloud.SynthesizerListener;
import com.thfw.ui.voice.ILife;

/**
 * Author:pengs
 * Date: 2021/11/23 10:28
 * Describe:Todo
 */
public interface ITtsFace extends ILife {

    boolean start(TtsModel ttsModel, SynthesizerListener synthesizerListener);

    void setCurrentSynthesizerListener(SimpleSynthesizerListener currentSynthesizerListener);

    void pause();
}
