package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.StatusEntity;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/7/29 15:22
 * Describe:选择状态列表适配
 */
public class StatusAdapter extends BaseAdapter<StatusEntity, RecyclerView.ViewHolder> {

    private ImageView mIvTopBanner;

    private int mSelectedIndex = -1;

    public StatusAdapter(List<StatusEntity> dataList) {
        super(dataList);
    }


    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case StatusEntity.TYPE_BODY:
                return new StatusHolder(LayoutInflater.from(mContext).inflate(R.layout.item_status_body, parent, false));
            case StatusEntity.TYPE_GROUP:
                return new StatusHolder(LayoutInflater.from(mContext).inflate(R.layout.item_status_group, parent, false));
            default:
                return new StatusTopHolder(LayoutInflater.from(mContext).inflate(R.layout.item_status_top, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == StatusEntity.TYPE_BODY) {
            int seat = mDataList.get(position).bodyPosition % 3;
            StatusHolder statusHolder = (StatusHolder) holder;
            statusHolder.mClMood.setSelected(position == mSelectedIndex);
            if (seat == 0) {
                Util.setViewMargin(holder.itemView, true, 15, 5, 8, 8);
            } else if (seat == 1) {
                Util.setViewMargin(holder.itemView, true, 10, 10, 8, 8);
            } else {
                Util.setViewMargin(holder.itemView, true, 5, 15, 8, 8);
            }
        }


    }

    public int getTopBanner() {
        return mIvTopBanner == null ? 0 : mIvTopBanner.getMeasuredHeight();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    public class StatusTopHolder extends RecyclerView.ViewHolder {


        public StatusTopHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mIvTopBanner = itemView.findViewById(R.id.iv_top_banner);
        }
    }

    public class StatusHolder extends RecyclerView.ViewHolder {


        private final ConstraintLayout mClMood;

        public StatusHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mClMood = itemView.findViewById(R.id.cl_mood);
            itemView.setOnClickListener(v -> {
                int oldIndex = mSelectedIndex;
                mSelectedIndex = getBindingAdapterPosition();
                if (oldIndex != -1) {
                    notifyItemChanged(oldIndex);
                }
                notifyItemChanged(mSelectedIndex);
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });
        }
    }
}
