package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.utils.Util;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/1/26 11:51
 * Describe:Todo
 */
public class AzQuickAdapter extends BaseAdapter<String, AzQuickAdapter.AzHolder> {

    public AzQuickAdapter(List<String> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public AzHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AzHolder(inflate(R.layout.item_az_char_layout, parent));
    }

    @Override
    public String getText(int position, int type) {
        switch (type) {
            case TYPE_SPEAK_TEXT:
                return mDataList.get(position);
        }
        return super.getText(position, type);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AzQuickAdapter.AzHolder holder, int position) {
        holder.mTvAz.setText(mDataList.get(position));
    }

    public class AzHolder extends RecyclerView.ViewHolder {

        private final TextView mTvAz;

        public AzHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvAz = itemView.findViewById(R.id.tv_az);
            int padding = (int) (MyApplication.getFontScale() * Util.dipToPx(4, mContext));
            mTvAz.setPadding(padding, padding, padding, padding);
        }
    }
}
