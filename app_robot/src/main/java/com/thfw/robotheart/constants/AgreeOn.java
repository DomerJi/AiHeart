package com.thfw.robotheart.constants;

import com.thfw.base.net.ApiHost;

public enum AgreeOn {


    AGREE_USER(ApiHost.getHost() + "tianhe/user_server.html", "用户服务协议"),
    AGREE_MSG(ApiHost.getHost() + "tianhe/secret_protect.html", "隐私保护政策"),
    @Deprecated
    AGREE_3G("https://www.baidu.com", "中国移动认证服务条款"),
    @Deprecated
    AGREE_AGREE("https://www.baidu.com", "知情同意书");

    // 成员变量
    private String url;
    private String title;

    // 构造方法
    private AgreeOn(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
