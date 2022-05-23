package com.thfw.user.login;

import com.thfw.base.utils.HandlerUtil;
import com.thfw.user.models.User;

import java.util.Observable;
import java.util.Observer;

public abstract class UserObserver implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        HandlerUtil.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                UserObserver.this.onChanged((UserManager) o, (User) arg);
            }
        });

    }

    public abstract void onChanged(UserManager accountManager, User user);
}
