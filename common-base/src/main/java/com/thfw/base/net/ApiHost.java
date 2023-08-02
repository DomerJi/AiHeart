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
    public static final String TEST_HOST = "http://fw.psyhealth.work:8080/";

    /**
     * 正在使用
     */
    private static String CURRENT_HOST;

    private static String CURRENT_AGREE_HOST = "https://psyhealth.work/";
    private static String CURRENT_TEST_H5_HOST = "https://resource.soulbuddy.cn/public/soul_the_land/depth_result.html?id=";


    public static void setCurrentHost(String currentHost) {
        CURRENT_HOST = currentHost;
    }

    public static void setCurrentTestH5Host(String currentTestH5Host) {
        CURRENT_TEST_H5_HOST = currentTestH5Host;
    }

    public static void setCurrentAgreeHost(String currentAgreeHost) {
        CURRENT_AGREE_HOST = currentAgreeHost;
    }

    public static String getHost() {
        if (CURRENT_HOST == null) {
            CURRENT_HOST = SharePreferenceUtil.getString(KEY_SELECTED_HOST, ApiHost.ONLINE_HOST);
        }
        return CURRENT_HOST;
    }

    /**
     * 用户隐私协议
     *
     * @return
     */
    public static String getNo8080Host() {
        return CURRENT_AGREE_HOST;
    }

    // 测评结果地址
    public static String getTestH5Host(Object id) {
        return CURRENT_TEST_H5_HOST + String.valueOf(id);
    }

}
