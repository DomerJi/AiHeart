package com.thfw.ui.voice;

import com.thfw.base.utils.LogUtil;
import com.thfw.ui.voice.view.HomeVoiceStatusImp;
import com.thfw.ui.voice.view.PersonVoiceStatusImp;
import com.thfw.ui.voice.view.ToolVoiceStatusImp;

import java.util.HashMap;

/**
 * Author:pengs
 * Date: 2021/11/23 11:39
 * Describe:Todo
 */
public class VoiceTypeManager {
    private static final String TAG = VoiceTypeManager.class.getSimpleName();
    private VoiceHolderView voiceHolderView;
    private VoiceType voiceType = VoiceType.NORMAL;
    private VoiceModel voiceModel;
    private boolean change;
    private HashMap<Integer, OnVoiceTypeChangeListener> listeners;

    public VoiceHolderView getVoiceHolderView() {
        return voiceHolderView;
    }

    public VoiceModel getVoiceModel() {
        return voiceModel;
    }

    public VoiceType getVoiceType() {
        return voiceType;
    }

    private static VoiceTypeManager manager;

    public static VoiceTypeManager getManager() {
        if (manager == null) {
            synchronized (VoiceTypeManager.class) {
                if (manager == null) {
                    manager = new VoiceTypeManager();
                }
            }
        }
        return manager;
    }


    private VoiceTypeManager() {
        listeners = new HashMap<>();
        listeners.put(VoiceHolderView.Type.TYPE_HOME, new HomeVoiceStatusImp());
        listeners.put(VoiceHolderView.Type.TYPE_TOOL, new ToolVoiceStatusImp());
        listeners.put(VoiceHolderView.Type.TYPE_PERSONIFICATION, new PersonVoiceStatusImp());
    }

    public void setVoiceType(VoiceType voiceType, VoiceModel voiceModel, VoiceHolderView voiceHolderView) {
        this.voiceModel = voiceModel;
        this.voiceHolderView = voiceHolderView;
        if (this.voiceHolderView != voiceHolderView) {
            setChanged();
        }
        setVoiceType(voiceType);
    }

    public void setVoiceType(VoiceType voiceType, VoiceModel voiceModel) {
        this.voiceModel = voiceModel;
        setVoiceType(voiceType);
    }

    public void setVoiceType(VoiceType voiceType) {
        this.voiceModel = null;
        if (this.voiceType != voiceType) {
            this.voiceType = voiceType;
            setChanged();
        }
        if (hasChanged()) {
            clearChanged();
            LogUtil.d(TAG, "voiceType = " + voiceType.ordinal());
            if (this.voiceHolderView != null) {
                listeners.get(voiceHolderView.type).update(getManager());
            }
        }
    }

    public void setChanged() {
        this.change = true;
    }

    public boolean hasChanged() {
        return change;
    }

    public void clearChanged() {
        this.change = false;
    }
}
