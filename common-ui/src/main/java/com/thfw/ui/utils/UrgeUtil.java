package com.thfw.ui.utils;

import java.util.HashMap;

/**
 * Author:pengs
 * Date: 2022/7/13 14:34
 * Describe:催促消息
 */
public class UrgeUtil {
    private static UrgeListener listener;
    private static HashMap<Integer, Object> map;
    private static int show;
    public final static long URGE_DELAY_TIME = 20000;
    public static final int URGE_OBJ = 1;

    public static void setListener(UrgeListener listener) {
        UrgeUtil.listener = listener;
        if (UrgeUtil.listener != null && show == 1) {
            show = 0;
            UrgeUtil.listener.onUrge(map);
        }
    }

    public static void notify(HashMap<Integer, Object> map) {
        if (listener != null) {
            listener.onUrge(map);
            UrgeUtil.map = null;
        } else {
            UrgeUtil.map = map;
            show = 1;
        }
    }

    public interface UrgeListener {
        void onUrge(HashMap<Integer, Object> map);
    }
}
