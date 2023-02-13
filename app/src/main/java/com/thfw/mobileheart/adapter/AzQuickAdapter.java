package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.mobileheart.R;

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
    public void onBindViewHolder(@NonNull @NotNull AzHolder holder, int position) {
        holder.mTvAz.setText(mDataList.get(position));
    }

    public class AzHolder extends RecyclerView.ViewHolder {

        private final TextView mTvAz;

        public AzHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvAz = itemView.findViewById(R.id.tv_az);
        }
    }

    @Override
    public String getText(int position, int type) {
        switch (type){
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                return mDataList.get(position);
        }

        return super.getText(position, type);
    }
}
