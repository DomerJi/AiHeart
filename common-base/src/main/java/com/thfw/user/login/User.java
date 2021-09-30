package com.thfw.user.login;

import com.thfw.base.utils.StringUtil;
import com.thfw.user.IUser;
import com.thfw.user.IUserInfo;

import java.util.HashMap;

public class User implements IUser, IUserInfo {

    private int loginStatus;

    public User() {
        this.loginStatus = LoginStatus.UNKNOWN;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public void logout(int flag) {
        this.loginStatus = flag;
    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public int getLoginType() {
        return 0;
    }

    @Override
    public String getOpenId() {
        return null;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    @Override
    public String getMobile() {
        return null;
    }

    @Override
    public String getNickName() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public HashMap<String, String> getAttrs() {
        return null;
    }

    public boolean equals(User user) {
        if (user == user) {
            return true;
        } else if (getLoginStatus() == user.getLoginStatus() &&
                StringUtil.contentEquals(getUserId(), user.getUserId()) &&
                StringUtil.contentEquals(getNickName(), user.getNickName()) &&
                StringUtil.contentEquals(getAvatar(), user.getAvatar()) &&
                StringUtil.contentEquals(getMobile(), user.getMobile())) {
            return true;
        } else {
            return false;
        }

    }
}
