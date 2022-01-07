package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.BookStudyItemModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 14:32
 * Describe:Todo
 */
public class BookStudyListAdapter extends BaseAdapter<BookStudyItemModel, BookStudyListAdapter.BookStudyHolder> {

    public BookStudyListAdapter(List<BookStudyItemModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public BookStudyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BookStudyHolder(inflate(R.layout.item_book_study, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookStudyHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position).getTitle());
    }


    public class BookStudyHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;

        public BookStudyHolder(@NonNull @NotNull View itemView) {
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
