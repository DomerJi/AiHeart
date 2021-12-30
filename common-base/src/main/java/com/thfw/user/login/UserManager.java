package com.thfw.user.login;

import com.thfw.base.net.CommonInterceptor;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;

import java.util.Observable;


/**
 * 账号管理
 */
public class UserManager extends Observable {

    private static volatile UserManager instance;
    private static final String KEY_USER = "key.user";
    private User user = new User();

    private UserManager() {
        String json = SharePreferenceUtil.getString(KEY_USER, null);
        user = GsonUtil.fromJson(json, User.class);
        if (user == null) {
            user = new User();
        }
        CommonInterceptor.setTokenListener(new CommonInterceptor.OnTokenListener() {
            @Override
            public String getToken() {
                return isLogin() ? user.getToken() : "null";
            }
        });
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    public String getUID() {
        if (getUser() != null && getUser().getUserId() != null) {
            return getUser().getUserId();
        }
        return "visitor";
    }

    public User getUser() {
        return user;

    }


    /**
     * 登录
     *
     * @param newUser
     */
    public void login(User newUser) {
        if (newUser == null) {
            return;
        }
        this.user = newUser;
        notifyUserInfo();
    }

    /**
     * @return 是否登录
     */
    public boolean isLogin() {
        return user.getLoginStatus() == LoginStatus.LOGINED;
    }

    /**
     * 退出登录
     *
     * @param flag {@link LoginStatus#LOGOUT_EXIT}
     *             {@link LoginStatus#LOGOUT_HIDE}
     *             {@link LoginStatus#LOGOUT_DELETE}
     */
    public void logout(int flag) {
        user.logout(flag);
        setChanged();
        notifyObservers(user);
        SharePreferenceUtil.setString(KEY_USER, "");
    }

    public void notifyUserInfo() {
        setChanged();
        String userJson = GsonUtil.toJson(user);
        notifyObservers(user);
        LogUtil.e("login -> userJson" + userJson);
        SharePreferenceUtil.setString(KEY_USER, userJson);
    }

}
