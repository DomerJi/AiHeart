package com.thfw.robotheart.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    protected Context mContext;
    protected RecyclerView mRecyclerView;

    protected List<T> mDataList;
    protected OnRvItemListener<T> mOnRvItemListener;

    public BaseAdapter(List<T> dataList) {
        this.mDataList = dataList;
    }

    public void setDataList(List<T> mDataList) {
        this.mDataList = mDataList;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void setDataListNotify(List<T> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    public void addData(T mData) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        mDataList.add(mData);
    }

    public void addDataNotify(T mData) {
        addData(mData);
        notifyDataSetChanged();
    }

    public void addDataList(List<T> mDataList) {
        if (this.mDataList == null) {
            this.mDataList = new ArrayList<>();
        }
        this.mDataList.addAll(mDataList);
    }

    public void addDataListNotify(List<T> mDataList) {
        addDataList(mDataList);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        this.mContext = recyclerView.getContext();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.mRecyclerView = null;
        this.mContext = null;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public OnRvItemListener<T> getOnRvItemListener() {
        return mOnRvItemListener;
    }

    public void setOnRvItemListener(OnRvItemListener<T> onRvItemListener) {
        this.mOnRvItemListener = onRvItemListener;
    }
}
