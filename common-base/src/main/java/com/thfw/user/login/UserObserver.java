package com.thfw.user.login;

import java.util.Observable;
import java.util.Observer;

public abstract class UserObserver implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        onChanged((UserManager) o, (User) arg);
    }

    public abstract void onChanged(UserManager accountManager, User user);
}
