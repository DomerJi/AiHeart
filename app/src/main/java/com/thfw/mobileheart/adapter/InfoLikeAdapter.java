package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.models.InfoLikeModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * Author:pengs
 * Date: 2021/10/12 9:54
 * Describe:Todo
 */
public class InfoLikeAdapter extends BaseAdapter<InfoLikeModel, InfoLikeAdapter.InfoLikeHolder> {

    private int maxCount;

    public InfoLikeAdapter(List<InfoLikeModel> dataList, int maxCount) {
        super(dataList);
        this.maxCount = maxCount;
    }

    @NonNull
    @NotNull
    @Override
    public InfoLikeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new InfoLikeHolder(LayoutInflater.from(mContext).inflate(R.layout.item_info_like_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull InfoLikeHolder holder, int position) {
        if (EmptyUtil.isEmpty(mDataList)) {
            holder.mTvName.setText("+自定义");
        } else if (mDataList.size() == maxCount) {
            holder.mTvName.setText(mDataList.get(position).getPickerViewText());
        } else {
            if (mDataList.size() == position) {
                holder.mTvName.setText("+自定义");
            } else {
                holder.mTvName.setText(mDataList.get(position).getPickerViewText());
            }
        }

        holder.mTvName.setOnClickListener(v -> {
            if (position == mDataList.size()) {
                addItem();
            }
        });
    }

    private void addItem() {
        int count = new Random().nextInt(10);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(i);
        }

        mDataList.add(new InfoLikeModel(builder.toString()));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (EmptyUtil.isEmpty(mDataList)) {
            return 1;
        } else if (mDataList.size() == maxCount) {
            return maxCount;
        } else {
            return mDataList.size() + 1;
        }
    }

    public class InfoLikeHolder extends RecyclerView.ViewHolder {

        private final TextView mTvName;

        public InfoLikeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);

        }
    }
}
