package com.thfw.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.trello.rxlifecycle2.android.FragmentEvent;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * 通用基础Fragment
 */
public abstract class IBaseFragment<T extends IPresenter> extends RxFragment {

    protected final String TAG = this.getClass().getSimpleName();
    protected T mPresenter;
    protected Context mContext;
    protected boolean hasLoad = false;
    private boolean isVisible = false;
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
            onReCreateView();
        } else {
            rootView = inflater.inflate(getContentView(), container, false);
        }
        return rootView;

    }

    protected void onReCreateView() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!hasLoad) {
            mPresenter = onCreatePresenter();
            initView();
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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        View v = getActivity().getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


}