package com.thfw.robotheart.util;

import com.thanosfisherman.wifiutils.WifiConnectorBuilder;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thfw.base.ContextApp;

/**
 * Author:pengs
 * Date: 2022/5/31 20:02
 * Describe:Todo
 */
public class WifiHelper {
    static WifiConnectorBuilder.WifiUtilsBuilder builder;

    public static WifiConnectorBuilder.WifiUtilsBuilder get() {
        if (builder == null) {
            builder = WifiUtils.withContext(ContextApp.get());
        }
        return builder;
    }

    public static void release() {
        if (builder != null) {
            builder = null;
        }
    }
}

