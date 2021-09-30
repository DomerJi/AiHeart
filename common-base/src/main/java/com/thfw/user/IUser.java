package com.thfw.user;

public interface IUser {

    /**
     * @return 用户标识
     */
    String getUserId();

    /**
     * @return 加密token用于网络请求认证
     */
    String getToken();

    /**
     * @return 登录方式
     */
    int getLoginType();

    /**
     * @return 第三方登录标识
     */
    String getOpenId();


    class LogoutException extends Throwable {
        public LogoutException() {
            super("退出登录失败 - 请参考 IUser#logout(int flag) 参数说明");
        }
    }

}
