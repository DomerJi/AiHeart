package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.HelpBackModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/13 11:27
 * Describe:Todo
 */
public class HelpBackAdapter extends BaseAdapter<HelpBackModel, HelpBackAdapter.HelpHolder> {

    public HelpBackAdapter(List<HelpBackModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public HelpBackAdapter.HelpHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new HelpHolder(LayoutInflater.from(mContext).inflate(R.layout.item_help_back_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HelpHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 10;
    }

    public class HelpHolder extends RecyclerView.ViewHolder {

        public HelpHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });
        }
    }
}
