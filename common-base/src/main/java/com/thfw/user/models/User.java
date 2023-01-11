package com.thfw.user.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.R;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.NumberUtil;
import com.thfw.base.utils.StringUtil;
import com.thfw.user.IUser;
import com.thfw.user.IUserInfo;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class User implements IUser, IUserInfo {

    private int loginStatus;
    private String token;
    private String mobile;
    // 用户所拥有的机构权限
    private List<String> authTypeList;

    public void setAuthTypeList(List<String> authTypeList) {
        this.authTypeList = authTypeList;
    }

    private UserInfo userInfo = new UserInfo();

    public void setUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            UserManager.setHistoryAccount(userInfo.account + "," + userInfo.mobile, userInfo.pic);
            this.userInfo = userInfo;
        }
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public static class UserInfo implements Serializable {

        /**
         * user_name :
         * pic :
         * true_name :
         * organization : 6
         * id : 100001
         * birth :
         * sex : 0
         * mobile : 16630007656
         * education : 0
         * marital_status : 0
         * child_status : 0
         * political_outlook : 0
         * rank :
         * hobby : []
         * support : []
         * native :
         * nation :
         */

        @SerializedName("user_name")
        public String userName;
        @SerializedName("account")
        public String account;
        @SerializedName("pic")
        public String pic;
        @SerializedName("true_name")
        public String trueName;
        @SerializedName("organization")
        public int organization;
        @SerializedName("id")
        public int id;
        @SerializedName("birth")
        public String birth;
        @SerializedName("sex")
        public int sex;
        @SerializedName("mobile")
        public String mobile;
        @SerializedName("education")
        public int education;
        @SerializedName("marital_status")
        public int maritalStatus;
        @SerializedName("child_status")
        public int childStatus;
        @SerializedName("political_outlook")
        public int politicalOutlook;
        @SerializedName("join_time")
        public String joinTime;
        @SerializedName("rank")
        public String rank;
        @SerializedName("department")
        public String department;
        @SerializedName("native")
        public String nativeX;
        @SerializedName("nation")
        public String nation;
        @SerializedName("hobby")
        public List<String> hobby;
        @SerializedName("support")
        public List<String> support;
    }

    private List<OrganizationModel.OrganizationBean> organList;

    public void setOrganList(List<OrganizationModel.OrganizationBean> organList) {
        this.organList = organList;
    }

    public List<OrganizationModel.OrganizationBean> getOrganList() {
        return organList;
    }

    public String getOrganListStr() {
        String organStr = "";
        if (EmptyUtil.isEmpty(organList)) {
            return "";
        }
        int size = organList.size();
        if (size >= 2) {
            organStr += organList.get(size - 2).getName() + " - " + organList.get(size - 1).getName();
        } else {
            organStr += organList.get(size - 1).getName();
        }
        return organStr;
    }


    public Object getVisibleAvatar() {
        if (TextUtils.isEmpty(getAvatar())) {
            return R.drawable.ic_default_avatar_phone;
        }
        return getAvatar();
    }

    /**
     * 无昵称， 显示手机号 123****7890
     */
    public String getVisibleName() {
        if (TextUtils.isEmpty(getNickName())) {
            return NumberUtil.getConfoundAccount(mobile);
        }
        return getNickName();
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
        return userInfo != null ? String.valueOf(userInfo.id) : null;
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
        return userInfo != null ? userInfo.pic : null;
    }

    @Override
    public String getMobile() {
        if (TextUtils.isEmpty(mobile)) {
            return userInfo != null ? userInfo.mobile : null;
        } else {
            return mobile;
        }

    }

    @Override
    public String getNickName() {
        return userInfo != null ? userInfo.userName : null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    public int organization;

    public int getOrganization() {
        return organization;
    }

    public boolean isNoSetOrgan() {
        return organization <= 0;
    }

    public void setOrganization(int organization) {
        this.organization = organization;
    }

    @SerializedName("set_user_info")
    public boolean setUserInfo;

    public void setSetUserInfo(boolean setUserInfo) {
        this.setUserInfo = setUserInfo;
    }

    public boolean isSetUserInfo() {
        return setUserInfo;
    }

    @Override
    public HashMap<String, String> getAttrs() {
        return null;
    }

    public boolean equals(User user) {
        if (user == user) {
            return true;
        } else if (getLoginStatus() == user.getLoginStatus() && StringUtil.contentEquals(getUserId(), user.getUserId()) && StringUtil.contentEquals(getNickName(), user.getNickName()) && StringUtil.contentEquals(getAvatar(), user.getAvatar()) && StringUtil.contentEquals(getMobile(), user.getMobile())) {
            return true;
        } else {
            return false;
        }

    }
}
