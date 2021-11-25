package com.thfw.ui.voice.wakeup;

import com.iflytek.cloud.util.ResourceUtil;
import com.thfw.base.ContextApp;

/**
 * Author:pengs
 * Date: 2021/11/23 11:30
 * Describe:Todo
 */
public class IvmResPath {
    private static String resPath;

    public static String getResPath() {
        if (resPath == null) {
            resPath = ResourceUtil.generateResourcePath(ContextApp.get(), ResourceUtil.RESOURCE_TYPE.assets, "ivw/5f3de60d.jet");
        }
        return resPath;
    }

}
