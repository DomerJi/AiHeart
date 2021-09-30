package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.mobileheart.model.HeartBoxEntity;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/9/14 16:38
 * Describe:Todo
 */
public class HeartBoxAdapter extends BaseAdapter<HeartBoxEntity, HeartBoxAdapter.BoxViewHolder> {


    public HeartBoxAdapter(List<HeartBoxEntity> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public BoxViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BoxViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_heart_box_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BoxViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class BoxViewHolder extends RecyclerView.ViewHolder {

        public BoxViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(null, getBindingAdapterPosition());
                }
            });

        }
    }

}
