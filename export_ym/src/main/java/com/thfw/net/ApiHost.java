package com.thfw.net;


public class ApiHost {

    /**
     * 正式环境
     */
    public static String ONLINE_HOST = "https://fw.psyhealth.work/";

    /**
     * 测试环境
     */
    public static String TEST_HOST = "http://183.232.231.82";


    public static String getHost() {
        return ONLINE_HOST;
    }

}
