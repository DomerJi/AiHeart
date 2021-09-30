package com.thfw.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.thfw.base.base.IPresenter;
import com.thfw.base.role.Limits;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * 通用基础Fragment
 */
public abstract class BaseFragment<T extends IPresenter> extends RxFragment implements Limits {

    protected final String TAG = this.getClass().getSimpleName();
    protected T mPresenter;
    protected Context mContext;
    protected boolean hasLoad = false;
    private boolean isVisible = false;
    private UserObserver userObserver;
    private View rootView;

    public void setHasLoad(boolean hasLoad) {
        this.hasLoad = hasLoad;
    }

    public boolean isNoHasLoad() {
        return !hasLoad;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(getContentView(), container, false);
        }
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!hasLoad) {
            mPresenter = onCreatePresenter();
            initView();
            userObserver = addObserver();
            if (userObserver != null) {
                UserManager.getInstance().addObserver(userObserver);
            }

            initData();
        }
    }

    protected <T extends View> T findViewById(@IdRes int resId) {
        if (getView() == null) {
            return null;
        }
        return getView().findViewById(resId);
    }

    @Override
    public void onPause() {
        super.onPause();
        onVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        onVisible(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        onVisible(!hidden);
    }


    /**
     * 除 ViewPgaer加载 Fragement情况下的可见状态
     * ViewPager 可见状态 Use {@link FragmentTransaction#setMaxLifecycle(Fragment, Lifecycle.State)}
     *
     * @param isVisible true 可见
     */
    public void onVisible(boolean isVisible) {
        lifecycleSubject.onNext(isVisible ? FragmentEvent.RESUME : FragmentEvent.PAUSE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userObserver != null) {
            UserManager.getInstance().deleteObserver(userObserver);
        }
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
        return (Class<T>) getActivity().getClass();
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

}