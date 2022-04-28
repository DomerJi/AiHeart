package com.thfw.base.utils;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author:pengs
 * Date: 2021/11/11 16:54
 * Describe:Todo
 */
public class DataChangeHelper {

    ArrayList<DataChangeListener> dataChangeListeners;

    public static class Factory {
        private static DataChangeHelper helper = new DataChangeHelper();
    }

    public static DataChangeHelper getInstance() {
        return Factory.helper;
    }

    public boolean isNoEmpty() {
        return dataChangeListeners != null && !dataChangeListeners.isEmpty();
    }

    private DataChangeHelper() {
        dataChangeListeners = new ArrayList<>();
    }

    public void add(DataChangeListener listener) {
        if (!dataChangeListeners.contains(listener)) {
            dataChangeListeners.add(listener);
        }
    }

    public void remove(DataChangeListener listener) {
        if (dataChangeListeners.contains(listener)) {
            dataChangeListeners.remove(listener);
        }
    }

    public static void collectChange(ImageView mIvCollect, int id) {
        if (DataChangeHelper.getInstance().isNoEmpty()) {
            if (mIvCollect != null && !mIvCollect.isSelected()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(DataChangeHelper.DataChangeListener.KEY_TYPE, DataChangeHelper.DataChangeListener.TYPE_COLLECT);
                map.put(DataChangeHelper.DataChangeListener.KEY_ID, id);
                DataChangeHelper.getInstance().notify(map);
            }
        }
    }

    public void notify(HashMap<String, Object> map) {
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onChanged(map);
        }
    }

    public interface DataChangeListener {
        String KEY_ID = "id";
        String KEY_TYPE = "type";
        String TYPE_TASK = "type_task";
        String TYPE_COLLECT = "type_collect";
        String KEY_COUNT = "count";
        String KEY_CURRENT = "current";

        void onChanged(HashMap<String, Object> map);
    }
}
