package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.SearchResultModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/29 14:18
 * Describe:Todo
 */
public class SearchAdapter extends BaseAdapter<SearchResultModel.ResultBean, SearchAdapter.ResultHolder> {

    public SearchAdapter(List<SearchResultModel.ResultBean> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ResultHolder(inflate(R.layout.item_search_result, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchAdapter.ResultHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position).getTitle());
    }

    public class ResultHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;

        public ResultHolder(@NonNull @NotNull View itemView) {
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
