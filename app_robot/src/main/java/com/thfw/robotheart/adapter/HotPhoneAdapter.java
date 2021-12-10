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
public class HotPhoneAdapter extends BaseAdapter<BookStudyItemModel, HotPhoneAdapter.BookStudyHolder> {

    public HotPhoneAdapter(List<BookStudyItemModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public BookStudyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BookStudyHolder(inflate(R.layout.item_hot_phone, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookStudyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 30;
    }

    public class BookStudyHolder extends RecyclerView.ViewHolder {

        public BookStudyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
