package com.thfw.xa.test.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.xa.test.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/7/26 9:08
 * Describe:Todo
 */
public class HomeAdapter extends BaseAdapter<String, HomeAdapter.HomeHolder> {

    public HomeAdapter(List<String> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new HomeHolder(inflate(R.layout.layout_home_item_imp, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position));
    }

    public class HomeHolder extends RecyclerView.ViewHolder {


        private final TextView mTvTitle;

        public HomeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }
}
