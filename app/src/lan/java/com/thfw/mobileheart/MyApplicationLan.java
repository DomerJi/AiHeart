package com.thfw.mobileheart;

import com.thfw.base.net.ApiHost;

/**
 * 入口
 * Created By jishuaipeng on 2023/8/2
 */
public class MyApplicationLan extends MyApplication {
    {
        ApiHost.setCurrentHost("http://clients.natapp1.cc/");
        ApiHost.setCurrentTestH5Host("http://clients.natapp1.cc/soul_the_land/depth_result.html?id=xxx");
        ApiHost.setCurrentAgreeHost("https://psyhealth.work/");
    }
}
