package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.BookItemModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 14:32
 * Describe:Todo
 */
public class BookListAdapter extends BaseAdapter<BookItemModel, BookListAdapter.BookListHolder> {

    public BookListAdapter(List<BookItemModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public BookListHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BookListHolder(inflate(R.layout.item_book_list, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookListHolder holder, int position) {
        holder.mTvOrder.setText(String.valueOf(position + 1) + "、");
        holder.mTvTitle.setText(mDataList.get(position).getTitle());
    }


    public class BookListHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
        private final TextView mTvOrder;

        public BookListHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvOrder = itemView.findViewById(R.id.tv_order);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });

        }
    }
}
