package com.thfw.models;

import android.text.TextUtils;

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
    public Object organization;

    @SerializedName("set_user_info")
    public boolean setUserInfo;

    public boolean isNoOrganization() {
        if (organization instanceof Integer) {
            Integer i = (Integer) organization;
            return i == 0;
        } else if (organization instanceof String) {
            return TextUtils.isEmpty(String.valueOf(organization));
        } else {
            return false;
        }

//        return organization != 0;
    }

    public boolean isNoSetUserInfo() {
//        return true;
        return !setUserInfo;
    }
}
