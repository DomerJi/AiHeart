package com.thfw.mobileheart.fragment.list;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;

/**
 * Author:pengs
 * Date: 2021/10/25 10:03
 * Describe:Todo
 */
public class TestHistoryListFragment extends BaseFragment {

    private String type;

    public TestHistoryListFragment(String type) {
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_test_history_list;
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
