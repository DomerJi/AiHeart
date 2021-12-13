package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;

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
public class BookListAdapter extends BaseAdapter<BookStudyItemModel, BookListAdapter.BookListHolder> {

    public BookListAdapter(List<BookStudyItemModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public BookListAdapter.BookListHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BookListHolder(inflate(R.layout.item_book_study, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookListAdapter.BookListHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 30;
    }

    public class BookListHolder extends RecyclerView.ViewHolder {

        public BookListHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
