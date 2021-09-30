package com.thfw.user.login;

import java.util.Observable;


/**
 * 账号管理
 */
public class UserManager extends Observable {

    private static volatile UserManager instance;
    private User user = new User();

    private UserManager() {
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
        if (this.user.equals(newUser)) {
            return;
        }
        setChanged();
        notifyObservers(user);
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
    }

}
