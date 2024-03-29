package com.thfw.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.jaeger.library.StatusBarUtil;
import com.thfw.base.base.IPresenter;
import com.thfw.base.role.Limits;
import com.thfw.base.utils.LogUtil;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;

/**
 * 通用基础Activity
 */
public abstract class IBaseActivity<T extends IPresenter> extends RxActivity implements Limits {

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
    private int resumed = -1;
    protected Context mContext;
    UserObserver userObserver;
    private int statusBarColor = STATUSBAR_NONE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarBackGround(getStatusBarColor());
        mContext = this;
        userObserver = addObserver();
        if (userObserver != null) {
            UserManager.getInstance().addObserver(userObserver);
        }
        setCustomTheme();
        setContentView(getContentView());
        mPresenter = onCreatePresenter();
        initView();
        initData();
    }

    public void setCustomTheme() {

    }

    public int getStatusBarColor() {
        return STATUSBAR_NONE;
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 顔色
     */
    public void setStatusBarBackGround(int color) {
        LogUtil.d("setStatusBarBackGround(" + color + ")");
        switch (color) {
            case STATUSBAR_NONE:
                break;
            case STATUSBAR_TRANSPARENT:
//                // todo 验证此种方式的兼容性
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                // 原来使用的方法
//                StatusBarUtil.setTranslucent(this, 0);
                // 底部虚拟导航栏适配
//                if (NavigationBarUtil.checkDeviceHasNavigationBar(this)) {
//                    LogUtil.e("hasNavigationBarShow -> true");
//                    getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0, 0, 0, NavigationBarUtil.getNavigationBarHeight(this));
//                }
                break;
            default:
                // 默认白色，如果再设置白色不需要
                if (color != statusBarColor) {
                    statusBarColor = color;
                    StatusBarUtil.setColor(this, color, 0);
                }
                setStatusBarTextColor(color == STATUSBAR_WHITE);
                break;
        }
    }

    public void setStatusBarTextColor(boolean isBlack) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // 设置字体颜色 （绿色背景-白色字体）（白色背景-黑色字体）
            ViewCompat.getWindowInsetsController(getWindow().getDecorView()).setAppearanceLightStatusBars(isBlack);
        } else {
            // API30以下
            getWindow().getDecorView().setSystemUiVisibility(isBlack ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    @Override
    public void onDestroy() {
        if (userObserver != null) {
            UserManager.getInstance().deleteObserver(userObserver);
        }
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
        super.onDestroy();
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
     * @return 子类实现User状态监听
     */
    public UserObserver addObserver() {
        return null;
    }

    @Override
    public Fragment getFragment(int action) {
        return null;
    }

    @Override
    public <T extends Activity> Class<T> getActivity(int action) {
        return (Class<T>) this.getClass();
    }

    @Override
    public boolean isVisible(int id) {
        return true;
    }

    @Override
    public boolean setVisible(View view) {
        if (view == null) {
            return false;
        }
        boolean isVisible = isVisible(view.getId());
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        return isVisible;
    }

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

    public boolean isMeResumed() {
        return resumed > 0;
    }

    public boolean isMeResumed2() {
        return resumed == 2;
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

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        resumed = 0;
    }

}