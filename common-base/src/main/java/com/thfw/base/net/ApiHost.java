package com.thfw.base.net;


public class ApiHost {

    /**
     * 沙盒环境
     */
    public static String SANDBOX_HOST = "https://sandbox-vehicle.baidu.com/";

    /**
     * 正式环境
     */
    public static String ONLINE_HOST = "https://tianhe.soulbuddy.cn/";

    /**
     * 测试环境
     */
    public static String TEST_HOST = "http://183.232.231.82";


    public static String getHost() {
        return ONLINE_HOST;
    }

}
