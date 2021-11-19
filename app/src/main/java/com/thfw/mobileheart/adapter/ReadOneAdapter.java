package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.mobileheart.model.ReadModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/8 15:04
 * Describe:
 */
public class ReadOneAdapter extends BaseAdapter<ReadModel, ReadOneAdapter.ReadHolder> {

    public ReadOneAdapter(List<ReadModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ReadHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ReadHolder(LayoutInflater.from(mContext).inflate(R.layout.item_read_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReadHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ReadHolder extends RecyclerView.ViewHolder {

        public ReadHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(null, getBindingAdapterPosition());
                }
            });

        }
    }
}
