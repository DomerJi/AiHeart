package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.HotCallModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 14:32
 * Describe:Todo
 */
public class HotPhoneAdapter extends BaseAdapter<HotCallModel, HotPhoneAdapter.BookStudyHolder> {

    public HotPhoneAdapter(List<HotCallModel> dataList) {
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
        HotCallModel callModel = mDataList.get(position);
        holder.mTvTitle.setText(callModel.title);
        holder.mTvTime.setText(callModel.time.trim());
        holder.mTvPhone.setText(callModel.phone.trim());
    }

    public class BookStudyHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
        private final TextView mTvTime;
        private final TextView mTvPhone;

        public BookStudyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvPhone = itemView.findViewById(R.id.tv_phone);
        }
    }
}
