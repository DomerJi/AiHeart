package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.BookStudyItemModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.lhxk.InstructScrollHelper;
import com.thfw.ui.widget.OrderView;

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
        holder.mOrderView.setOrder(position + 1);
    }

    @Override
    public String getText(int position, int type) {
        switch (type) {
            case TYPE_SPEAK_TEXT:
                return mDataList.get(position).getTitle();
            case TYPE_SPEAK_ORDER:
                return InstructScrollHelper.speakNumber(position + 1);

        }
        return super.getText(position, type);
    }


    public class BookStudyHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
        private final OrderView mOrderView;

        public BookStudyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mOrderView = itemView.findViewById(R.id.orderView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }
}
