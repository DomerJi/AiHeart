package com.thfw.base.net;


public class ApiHost {

    /**
     * 正式环境
     */
    public static final String ONLINE_HOST = "http://psyhealthfw.thyunmai.com:40184/";

    /**
     * 测试环境
     */
    public static final String TEST_HOST = "https://tianhe.soulbuddy.cn/";

    /**
     * 正在使用
     */
    private static String CURRENT_HOST = ONLINE_HOST;


    public static void setCurrentHost(String currentHost) {
        CURRENT_HOST = currentHost;
    }

    public static String getHost() {
        if (CURRENT_HOST == null) {
            CURRENT_HOST = ONLINE_HOST;
        }
        return CURRENT_HOST;
    }

}
