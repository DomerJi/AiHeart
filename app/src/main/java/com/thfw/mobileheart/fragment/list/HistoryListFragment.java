package com.thfw.mobileheart.fragment.list;

import android.graphics.Color;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;

import java.util.Random;


public class HistoryListFragment extends BaseFragment {

    public HistoryListFragment(String data) {
        super();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_me_history_list;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        Random random = new Random();
        getView().setBackgroundColor(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
    }

    @Override
    public void initData() {

    }

}