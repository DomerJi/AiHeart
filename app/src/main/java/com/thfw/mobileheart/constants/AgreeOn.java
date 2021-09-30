package com.thfw.mobileheart.constants;

public enum AgreeOn {

    AGREE_3G("https://www.baidu.com", "中国移动认证服务条款"),
    AGREE_USER("https://www.baidu.com", "用户服务协议"),
    AGREE_MSG("https://www.baidu.com", "隐私保护政策"),
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
