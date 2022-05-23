package com.thfw.base.utils;

import java.util.ArrayList;

/**
 * Author:pengs
 * Date: 2021/11/11 16:54
 * Describe:Todo
 */
public class HourChangeHelper {

    ArrayList<HourChangeListener> hourChangeListeners;

    private HourChangeHelper() {
        hourChangeListeners = new ArrayList<>();
    }

    public static HourChangeHelper getInstance() {
        return Factory.helper;
    }

    public void add(HourChangeListener listener) {
        if (!hourChangeListeners.contains(listener)) {
            hourChangeListeners.add(listener);
        }
    }

    public void remove(HourChangeListener listener) {
        if (hourChangeListeners.contains(listener)) {
            hourChangeListeners.remove(listener);
        }
    }

    public void notify(int collectionId, int hour, int musicId) {
        for (HourChangeListener listener : hourChangeListeners) {
            listener.hourChanged(collectionId, hour, musicId);
        }
    }

    public interface HourChangeListener {
        void hourChanged(int collectionId, int hour, int musicId);
    }

    public static class Factory {
        private static HourChangeHelper helper = new HourChangeHelper();
    }
}
