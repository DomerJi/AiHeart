package com.thfw.util;

import android.view.View;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.adapter.BaseAdapter;
import com.thfw.view.ILoading;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/7/22 9:04
 * Describe:分页加载数据，上拉加载下拉刷新，首次加载状态 -> 辅助管理
 */
public class PageHelper<T> {

    private int firstPage = 1;
    private int currentPage = 1;
    private boolean refreshEnable = true;

    private BaseAdapter adapter;
    private ILoading iLoading;
    private SmartRefreshLayout refreshLayout;


    public PageHelper(ILoading iLoading, SmartRefreshLayout refreshLayout, BaseAdapter adapter) {
        this.adapter = adapter;
        this.iLoading = iLoading;
        this.refreshLayout = refreshLayout;
        this.refreshLayout.setEnableLoadMore(false);
        this.refreshLayout.setEnableRefresh(false);
    }

    public void setRefreshEnable(boolean enable) {
        this.refreshEnable = enable;
    }

    /**
     * @return 当前页是否是第一页
     */
    public boolean isFirstPage() {
        return currentPage == firstPage;
    }

    /**
     * 重置第一页页码 页码不为1的情况
     *
     * @param firstPage
     */
    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    /**
     * 刷新调用，页码恢复初始值
     */
    public void onRefresh() {
        this.currentPage = this.firstPage;
    }

    /**
     * @return 当前页码
     */
    public int getPage() {
        return currentPage;
    }


    public void onSuccess(List<T> list) {
        onSuccess(list, false);
    }


    /**
     * 加载数据-成功
     *
     * @param list
     */
    public void onSuccess(List<T> list, boolean isTop) {
        if (adapter == null) {
            return;
        }
        if (firstPage == currentPage) {
            adapter.setDataListNotify(list);
        } else {
            if (isTop) {
                adapter.addDataListNotify(list, isTop);
            } else {
                adapter.addDataListNotify(list);
            }
        }

        boolean emptyDatas = EmptyUtil.isEmpty(adapter.getDataList());
        if (iLoading != null) {
            if (emptyDatas) {
                iLoading.showEmpty();
            } else {
                iLoading.hide();
            }
        }

        if (refreshLayout != null) {
            refreshLayout.setEnableRefresh(refreshEnable && !emptyDatas);
            refreshLayout.setEnableLoadMore(!emptyDatas);
            refreshLayout.setNoMoreData(EmptyUtil.isEmpty(list));

            if (refreshLayout.isRefreshing()) {
                refreshLayout.finishRefresh();
            }
            if (refreshLayout.isLoading()) {
                refreshLayout.finishLoadMore();
            }
        }
        // 此页数据不为空则页码 + 1
        if (!EmptyUtil.isEmpty(list)) {
            currentPage++;
        }
    }

    /**
     * 加载数据-失败
     *
     * @param level
     * @param onClickListener
     */
    public void onFail(ILoading.Level level, View.OnClickListener onClickListener) {

        if (EmptyUtil.isEmpty(adapter.getDataList())) {
            if (iLoading != null) {
                iLoading.showFail(level, onClickListener);
            }
            if (refreshLayout != null) {
                refreshLayout.setEnableRefresh(false);
                refreshLayout.setEnableLoadMore(false);
            }
        }
        if (refreshLayout != null) {
            if (refreshLayout.isRefreshing()) {
                refreshLayout.finishRefresh(false);
            }
            if (refreshLayout.isLoading()) {
                refreshLayout.finishLoadMore(false);
            }
        }
    }

    public void onFail(View.OnClickListener onClickListener) {
        onFail(ILoading.ERROR, onClickListener);
    }

}
