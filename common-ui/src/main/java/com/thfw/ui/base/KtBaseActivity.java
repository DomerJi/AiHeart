package com.thfw.ui.base;

import com.thfw.base.base.IPresenter;

/**
 * Author:pengs
 * Date: 2022/11/22 10:14
 * Describe:Todo
 */
public class KtBaseActivity extends IBaseActivity{
    @Override
    public int getContentView() {
        return 0;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}
