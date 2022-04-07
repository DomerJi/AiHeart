package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/29 15:39
 * Describe:Todo
 */
public class SearchHistoryAdapter extends BaseAdapter<String, SearchHistoryAdapter.SearchHolderHodler> {


    public SearchHistoryAdapter(List<String> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public SearchHolderHodler onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new SearchHolderHodler(inflate(R.layout.item_search_history, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchHolderHodler holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() > 10 ? 10 : super.getItemCount();
    }

    public class SearchHolderHodler extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;

        public SearchHolderHodler(@NonNull @NotNull View itemView) {
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
