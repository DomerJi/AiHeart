package com.thfw.robotheart.activitys;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.util.Dormant;
import com.thfw.ui.base.IBaseActivity;

/**
 * Author:pengs
 * Date: 2021/11/15 10:20
 * Describe:Todo
 */
public abstract class RobotBaseActivity<T extends IPresenter> extends IBaseActivity<T> {

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Dormant.reset();
        return super.dispatchTouchEvent(event);
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
