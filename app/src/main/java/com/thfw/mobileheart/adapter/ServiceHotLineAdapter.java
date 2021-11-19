package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.mobileheart.model.HotLineModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/18 11:38
 * Describe:Todo
 */
public class ServiceHotLineAdapter extends BaseAdapter<HotLineModel, ServiceHotLineAdapter.ServiceHolder> {

    public ServiceHotLineAdapter(List<HotLineModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ServiceHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ServiceHolder(LayoutInflater.from(mContext).inflate(R.layout.item_hot_line_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ServiceHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 100;
    }

    public class ServiceHolder extends RecyclerView.ViewHolder {

        public ServiceHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
