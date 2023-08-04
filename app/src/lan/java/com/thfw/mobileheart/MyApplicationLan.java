package com.thfw.mobileheart;

import com.thfw.base.net.ApiHost;

/**
 * 入口
 * Created By jishuaipeng on 2023/8/2
 */
public class MyApplicationLan extends MyApplication {
    {
        ApiHost.setOnlineHost("http://clients.natapp1.cc/");
        ApiHost.setTestHost("https://fw.psyhealth.work/");
    }
}
