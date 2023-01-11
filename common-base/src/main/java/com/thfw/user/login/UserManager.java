package com.thfw.user.login;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.thfw.base.ContextApp;
import com.thfw.base.net.CommonInterceptor;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.MyPreferences;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.user.models.HistoryAccount;
import com.thfw.user.models.User;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;


/**
 * 账号管理
 */
public class UserManager extends Observable {

    private static volatile UserManager instance;
    private static final String KEY_USER = "key.user";
    private User user = new User();

    private boolean isNewLogin;

    public boolean isNewLogin() {
        return isNewLogin;
    }


    public static final String KEY_HISTORY_ACCOUNT = "history.account";
    public static final String KEY_HISTORY_ACCOUNT_AVATAR = "history.account.avatar";
    public static final String KEY_HISTORY_ACCOUNT_NUMBER = "history.account.number";

    public static void setHistoryAccount(String accountAll, String avatar) {
        HashMap<String, HistoryAccount> caches = getHistoryAccount();
        if (caches != null) {
            Collection<HistoryAccount> collections = caches.values();
            for (HistoryAccount historyAccount : collections) {
                String[] accounts = accountAll.split(",");
                if (Arrays.asList(accounts).contains(historyAccount.account)) {
                    historyAccount.avatar = avatar;
                    break;
                }
            }
        }

        SharePreferenceUtil.setString(KEY_HISTORY_ACCOUNT, GsonUtil.toJson(caches));
    }

    public static void removeHistoryAccount(String account) {
        HashMap<String, HistoryAccount> caches = getHistoryAccount();
        if (caches != null) {
            caches.remove(account);
        }
        SharePreferenceUtil.setString(KEY_HISTORY_ACCOUNT, GsonUtil.toJson(caches));
    }

    public static void addHistoryAccount(String account, String avatar) {
        HashMap<String, HistoryAccount> caches = getHistoryAccount();

        if (caches == null) {
            caches = new HashMap<>();
        }
        caches.put(account, new HistoryAccount(avatar, account));

        if (caches.size() > 2) {
            long time = 0;
            HistoryAccount deleteAccount = null;
            Collection<HistoryAccount> collections = caches.values();
            for (HistoryAccount historyAccount : collections) {
                if (time > historyAccount.addTime) {
                    deleteAccount = historyAccount;
                }
            }
            if (deleteAccount != null) {
                caches.remove(deleteAccount.account);
            }
        }
        SharePreferenceUtil.setString(KEY_HISTORY_ACCOUNT, GsonUtil.toJson(caches));
    }

    public static HashMap<String, HistoryAccount> getHistoryAccount() {
        Type type = new TypeToken<HashMap<String, HistoryAccount>>() {
        }.getType();
        return SharePreferenceUtil.getObject(KEY_HISTORY_ACCOUNT, type);
    }

    private UserManager() {
        String json = SharePreferenceUtil.getString(KEY_USER, null);
        user = GsonUtil.fromJson(json, User.class);
        if (user == null) {
            user = new User();
        }
        CommonInterceptor.setTokenListener(new CommonInterceptor.OnTokenListener() {
            @Override
            public String getToken() {
                return isLogin() ? user.getToken() : null;
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
        if (isTrueLogin()) {
            if (getUser() != null && !TextUtils.isEmpty(getUser().getUserId())) {
                return getUser().getUserId();
            }
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
        notifyUserInfo(isTrueLogin());
    }

    /**
     * @return 是否登录
     */
    public boolean isLogin() {
        return user.getLoginStatus() == LoginStatus.LOGINED || user.getLoginStatus() == LoginStatus.LOGOUT_HIDE;
    }


    public boolean isTrueLogin() {
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
        LogUtil.d("logout flag = " + flag);
        user.logout(flag);
        setChanged();
        notifyObservers(user);
        SharePreferenceUtil.setString(KEY_USER, "");
        MyPreferences.getInstance(ContextApp.get()).setAgreePrivacyAgreement(false);
    }

    public void login() {
        if (user != null && user.getLoginStatus() != LoginStatus.LOGOUT_EXIT) {
            user.setLoginStatus(LoginStatus.LOGINED);
            notifyUserInfo(isTrueLogin());
        }
    }

    private void notifyUserInfo(boolean isNewLogin) {
        this.isNewLogin = isNewLogin;
        setChanged();
        String userJson = GsonUtil.toJson(user);
        notifyObservers(user);
        LogUtil.e("login -> userJson" + userJson);
        if (user.getLoginStatus() == LoginStatus.LOGINED) {
            SharePreferenceUtil.setString(KEY_USER, userJson);
        }
        MyPreferences.getInstance(ContextApp.get()).setAgreePrivacyAgreement(isTrueLogin());
        if (agreedInitListener != null) {
            if (isTrueLogin()) {
                agreedInitListener.onAgreed(true);
            }
        }
    }

    private OnAgreedInitListener agreedInitListener;


    public void setAgreedInitListener(OnAgreedInitListener agreedInitListener) {
        this.agreedInitListener = agreedInitListener;
    }

    public interface OnAgreedInitListener {
        void onAgreed(boolean agreed);
    }


    public void notifyUserInfo() {
        notifyUserInfo(false);
    }

}
