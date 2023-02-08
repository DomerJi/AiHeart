package com.thfw.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.thfw.util.YmActivityManager;


/**
 * 通用基础Activity
 */
public abstract class IBaseActivity<T extends IPresenter> extends RxActivity {

    public static final String KEY_DATA = "key.data";
    // 默认状态栏白色
    public static final int STATUSBAR_WHITE = Color.WHITE;
    public static final int STATUSBAR_CUSTOM = 0xFF59C6C1;
    public static final int STATUSBAR_BALCK = Color.BLACK;
    public static final int STATUSBAR_NONE = -100;
    public static final int STATUSBAR_TRANSPARENT = Color.TRANSPARENT;
    public static final long TOAST_DELAY_MILLIS = 1500;
    protected final String TAG = this.getClass().getSimpleName();
    protected T mPresenter;
    protected Context mContext;
    private int resumed = -1;
    private int statusBarColor = STATUSBAR_WHITE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        YmActivityManager.getActivityManager().pushActivity(this);
        setContentView(getContentView());
        mPresenter = onCreatePresenter();
        initView();
        initData();
    }


    public int getStatusBarColor() {
        return STATUSBAR_NONE;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YmActivityManager.getActivityManager().popActivity();
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
    }

    /**
     * @return 返回页面LayoutId
     */
    public abstract int getContentView();

    /**
     * @return 控制层Presenter
     */
    public abstract T onCreatePresenter();

    /**
     * 初始化View
     */
    public abstract void initView();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resumed == -1) {
            resumed = 1;
        } else {
            resumed = 2;
        }
    }

    public boolean isMeResumed() {
        return resumed > 0;
    }

    public boolean isMeResumed2() {
        return resumed == 2;
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        resumed = 0;
    }
}