package com.thfw.base.base;

import com.thfw.base.utils.LogUtil;

import java.lang.ref.SoftReference;
import java.lang.reflect.Proxy;

/**
 * Date:2019/5/31
 * Author:yushengjin
 * Description:www.baidu.com/iov
 */
public abstract class IPresenter<U extends UI> {

    private final String TAG = this.getClass().getSimpleName();

    private SoftReference<U> mUiReference;
    private U mProxyUI;

    public IPresenter(U ui) {
        if (ui != null) {
            mUiReference = new SoftReference<>(ui);
            mProxyUI = (U) Proxy.newProxyInstance(ui.getClass().getClassLoader(),
                    ui.getClass().getInterfaces(), (proxy, method, args) -> {
                        if (isViewAttached()) {
                            return method.invoke(mUiReference.get(), args);
                        }
                        return null;
                    });
        }
    }

    private boolean isViewAttached() {
        return mUiReference != null && mUiReference.get() != null;
    }

    protected U getUI() {
        return mProxyUI;
    }

    public void onDestroy() {
        if (isViewAttached()) {
            mUiReference.clear();
            mUiReference = null;
        }
        LogUtil.d(TAG, "Present is Destroy");
    }
}
