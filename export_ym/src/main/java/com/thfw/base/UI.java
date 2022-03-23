package com.thfw.base;

import com.thfw.net.ResponeThrowable;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * UI 供 P层使用
 */
public interface UI<T> {

    LifecycleProvider getLifecycleProvider();

    void onSuccess(T data);

    void onFail(ResponeThrowable throwable);
}
