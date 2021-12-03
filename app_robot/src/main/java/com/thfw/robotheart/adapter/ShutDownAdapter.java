package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/1 14:15
 * Describe:Todo
 */
public class ShutDownAdapter extends BaseAdapter<String, ShutDownAdapter.ShutDownHolder> {

    public ShutDownAdapter(List<String> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ShutDownHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ShutDownHolder(inflate(R.layout.item_shutdown_adapter, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ShutDownHolder holder, int position) {
        holder.mTvTime.setText(mDataList.get(position % mDataList.size()));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class ShutDownHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTime;

        public ShutDownHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
