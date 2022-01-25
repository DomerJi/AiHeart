package com.thfw.robotheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.PickerData;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/12 9:54
 * Describe:Todo
 */
public class InfoLikeAdapter extends BaseAdapter<PickerData, InfoLikeAdapter.InfoLikeHolder> {

    public InfoLikeAdapter(List<PickerData> dataList) {
        super(dataList);
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
        } else {
            if (mDataList.size() == position) {
                holder.mTvName.setText("+自定义");
            } else {
                holder.mTvName.setText(mDataList.get(position).getPickerViewText());
            }
        }


    }

    @Override
    public int getItemCount() {
        if (EmptyUtil.isEmpty(mDataList)) {
            return 1;
        } else {
            return mDataList.size() + 1;
        }
    }

    public class InfoLikeHolder extends RecyclerView.ViewHolder {

        private final TextView mTvName;

        public InfoLikeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvName.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }


}
