package com.thfw.ui.voice.speech;

import com.thfw.ui.voice.ILife;

/**
 * Author:pengs
 * Date: 2021/11/23 10:28
 * Describe:Todo
 */
public interface ISpeechFace extends ILife {

    void onResult(String newText, boolean append, boolean end);
}
