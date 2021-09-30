package com.thfw.user.login;

public interface LoginStatus {
    // 未登录
    int UNKNOWN = -1;
    // 登录中...
    int LOGIN_ING = 0;
    // 登录完成
    int LOGINED = 1;
    // 退出
    int LOGOUT_EXIT = 2;
    // 隐身
    int LOGOUT_HIDE = 3;
    // 退出并清空
    int LOGOUT_DELETE = 4;

}
