package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/6 16:44
 * Describe:Todo
 */
public class TokenModel implements IModel {
    public String token;
    // 是否加入过组织
    @SerializedName("organization")
    public int organization;
    // 是否设置过用户信息
    @SerializedName("set_user_info")
    public boolean setUserInfo;
    // 已拥有的机构权限
    @SerializedName("auth_type")
    public List<String> authType;

    public List<String> getAuthType() {
        return authType;
    }

    public boolean isNoOrganization() {
        return organization == 0;
    }

    public boolean isNoSetUserInfo() {
        return !setUserInfo;
    }

    public boolean isSetUserInfo() {
        return setUserInfo;
    }
}
