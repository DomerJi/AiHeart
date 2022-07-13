package com.thfw.base.models;

import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/6 15:57
 * Describe:Todo
 */
public class AccountPinyinModel implements IModel {

    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "AccountPinyinModel{" +
                "account='" + account + '\'' +
                '}';
    }
}
