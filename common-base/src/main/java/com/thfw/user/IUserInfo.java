package com.thfw.user;

import java.util.HashMap;

public interface IUserInfo {

    /**
     * @return 头像
     */
    String getAvatar();

    /**
     * @return 手机号
     */
    String getMobile();

    /**
     * @return 昵称
     */
    String getNickName();

    /**
     * @return 邮箱
     */
    String getEmail();

    /**
     * @return 其他属性
     */
    HashMap<String, String> getAttrs();


}
