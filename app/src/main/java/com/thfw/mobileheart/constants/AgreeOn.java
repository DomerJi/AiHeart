package com.thfw.mobileheart.constants;

public enum AgreeOn {


    AGREE_USER("https://resource.soulbuddy.cn/public/uploads/tianhe/user_server_mobile.html", "用户服务协议"),
    AGREE_MSG("https://resource.soulbuddy.cn/public/uploads/tianhe/secret_protect_mobile.html", "隐私保护政策"),
    @Deprecated
    AGREE_3G("https://www.baidu.com", "中国移动认证服务条款"),
    @Deprecated
    AGREE_AGREE("https://www.baidu.com", "知情同意书"),
    @Deprecated
    AGREE_ABOUT("https://www.baidu.com", "关于我们");

    // 成员变量
    private String url;
    private String title;

    // 构造方法
    private AgreeOn(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public static boolean isAgreeUrl(String url) {
        if (AGREE_USER.getUrl().equals(url)
                || AGREE_MSG.getUrl().equals(url)) {
            return true;
        }
        return false;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
