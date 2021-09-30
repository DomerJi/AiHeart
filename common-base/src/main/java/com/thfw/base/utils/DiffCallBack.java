package com.thfw.base.utils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

/**
 * Created by jishuaipeng on 2021-04-26.
 * <p>
 * 数据对比类 实现 recyclerView 局部刷新
 *
 * @param <T> <T extends DiffCallBack.DiffImp>
 */
public class DiffCallBack<T extends DiffCallBack.DiffImp> extends DiffUtil.Callback {

    private List<T> oldList;
    private List<T> newList;

    public DiffCallBack(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).areItemsTheSame(newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).areContentsTheSame(newList.get(newItemPosition));
    }

    public interface DiffImp<T> {

        boolean areItemsTheSame(T t);

        boolean areContentsTheSame(T t);
    }
}