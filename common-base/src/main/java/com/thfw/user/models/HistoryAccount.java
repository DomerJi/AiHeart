package com.thfw.user.models;

import com.thfw.base.utils.NumberUtil;

import java.io.Serializable;

/**
 * Author:pengs
 * Date: 2023/1/11 14:48
 * Describe:Todo
 */
public class HistoryAccount implements Serializable, Comparable<HistoryAccount> {

    public String avatar;
    public String account;
    public String simpleAccount;
    public long addTime;

    public HistoryAccount(String avatar, String account) {
        this.avatar = avatar;
        this.account = account;
        simpleAccount = NumberUtil.getConfoundAccount(account);
        this.addTime = System.currentTimeMillis();
    }

    public String getSimpleAccount() {
        return simpleAccount;
    }

    @Override
    public int compareTo(HistoryAccount o) {
        return addTime > o.addTime ? -1 : 1;
    }
}
