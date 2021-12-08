package com.thfw.user.login;

import com.thfw.base.models.OrganizationModel;
import com.thfw.base.utils.StringUtil;
import com.thfw.user.IUser;
import com.thfw.user.IUserInfo;

import java.util.HashMap;
import java.util.List;

public class User implements IUser, IUserInfo {

    private int loginStatus;
    private String token;
    private String mobile;
    private List<OrganizationModel.OrganizationBean> organList;

    public void setOrganList(List<OrganizationModel.OrganizationBean> organList) {
        this.organList = organList;
    }

    public List<OrganizationModel.OrganizationBean> getOrganList() {
        return organList;
    }

    public String getOrganListStr() {
        String organStr = "";
        int size = organList.size();
        if (size >= 2) {
            organStr += organList.get(size - 2).getName() + " - " + organList.get(size - 1).getName();
        } else {
            organStr += organList.get(size).getName();
        }
        return organStr;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

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
        return token;
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
        return mobile;
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
