package com.thfw.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/6 16:44
 * Describe:Todo
 */
public class TokenModel implements IModel {
    public String token;
    // 是否加入过组织
    public int organization;

    @SerializedName("set_user_info")
    public boolean setUserInfo;

    public boolean isNoOrganization() {
        return organization == 0;
//        return organization != 0;
    }

    public boolean isNoSetUserInfo() {
//        return true;
        return !setUserInfo;
    }
}
