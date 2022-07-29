package com.thfw.xa.test;

import androidx.multidex.MultiDexApplication;

import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;

/**
 * Author:pengs
 * Date: 2022/7/26 10:10
 * Describe:Todo
 */
public class XaApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.init(this);
        SharePreferenceUtil.init(this);
    }
}
