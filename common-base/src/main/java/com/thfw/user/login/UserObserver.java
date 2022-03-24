package com.thfw.user.login;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.thfw.user.models.User;

import java.util.Observable;
import java.util.Observer;

public abstract class UserObserver implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                UserObserver.this.onChanged((UserManager) o, (User) arg);
            }
        }.sendEmptyMessage(0);

    }

    public abstract void onChanged(UserManager accountManager, User user);
}
