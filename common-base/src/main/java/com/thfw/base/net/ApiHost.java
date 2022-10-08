package com.thfw.base.net;


import com.thfw.base.utils.SharePreferenceUtil;

public class ApiHost {

    public static final String KEY_SELECTED_HOST = "key.selected.host";
    public static final String KEY_CUSTOM_HOST = "key.custom.host";

    /**
     * 正式环境
     */
    public static final String ONLINE_HOST = "https://fw.psyhealth.work/";

    /**
     * 测试环境
     */
    public static final String TEST_HOST = "http://101.37.149.192:8080/";

    /**
     * 正在使用
     */
    private static String CURRENT_HOST;


    public static void setCurrentHost(String currentHost) {
        CURRENT_HOST = currentHost;
    }

    public static String getHost() {
        if (CURRENT_HOST == null) {
            CURRENT_HOST = SharePreferenceUtil.getString(KEY_SELECTED_HOST, ApiHost.ONLINE_HOST);
        }
        return CURRENT_HOST;
    }

    public static String getNo8080Host() {
        return getHost().replace(":8080", "");
    }

    public static String getTestH5Host(Object id) {
        return "https://resource.soulbuddy.cn/public/soul_the_land/depth_result.html?id=" + String.valueOf(id);
    }

}
