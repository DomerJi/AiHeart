package com.thfw.robotheart.activitys;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.receiver.BootCompleteReceiver;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.util.Dormant;
import com.thfw.ui.base.IBaseActivity;

/**
 * Author:pengs
 * Date: 2021/11/15 10:20
 * Describe:Todo
 */
public abstract class RobotBaseActivity<T extends IPresenter> extends IBaseActivity<T> {

    //重写字体缩放比例  api>25
    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            final Resources res = newBase.getResources();
            final Configuration config = res.getConfiguration();
            // 1 设置正常字体大小的倍数
            config.fontScale = MyApplication.getApp().getFontScale();
            final Context newContext = newBase.createConfigurationContext(config);
            super.attachBaseContext(newContext);
        } else {
            super.attachBaseContext(newBase);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // todo 关机动画
        BootCompleteReceiver.setShutDownCallback(() -> {
            if (isMeResumed()) {
                RobotUtil.shutdown(this);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BootCompleteReceiver.setShutDownCallback(null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Dormant.reset();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Dormant.reset();
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Dormant.reset();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Dormant.reset();
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Dormant.reset();
        return super.onKeyLongPress(keyCode, event);
    }
}
