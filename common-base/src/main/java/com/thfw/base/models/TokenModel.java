package com.thfw.base.models;

import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/6 16:44
 * Describe:Todo
 */
public class TokenModel implements IModel {
    public String token;
    // 是否加入过组织
    public int organization;

    public boolean isNoOrganization() {
        return organization == 0;
//        return organization != 0;
    }
}
