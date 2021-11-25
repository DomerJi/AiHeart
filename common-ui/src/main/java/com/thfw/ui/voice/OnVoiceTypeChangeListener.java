package com.thfw.ui.voice;

/**
 * Author:pengs
 * Date: 2021/11/22 16:56
 * Describe:Todo
 */
public abstract class OnVoiceTypeChangeListener {


    public void update(VoiceTypeManager manager) {
        onChanged(manager.getVoiceType(), manager.getVoiceHolderView(), manager.getVoiceModel());
    }

    public abstract void onChanged(VoiceType type, VoiceHolderView voiceHolderView, VoiceModel voiceModel);
}
