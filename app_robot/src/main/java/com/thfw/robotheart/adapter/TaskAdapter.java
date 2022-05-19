package com.thfw.robotheart.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.TaskItemModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class TaskAdapter extends BaseAdapter<TaskItemModel, TaskAdapter.TaskHolder> {

    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public TaskAdapter(List<TaskItemModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TaskHolder(inflate(R.layout.item_task, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TaskHolder holder, int position) {
        TaskItemModel itemModel = mDataList.get(position);
        holder.mTvTime.setText(itemModel.getVisibleTime(type));
        holder.mTvStatus.setText(itemModel.getFinishCount() + "/" + itemModel.getCount());
        holder.mTvTitle.setText(itemModel.getTitle());
        holder.mTvType.setText(itemModel.getTaskTypeStr());
        if (itemModel.getStatus() == 4) {
            holder.mTvFlag.setVisibility(View.VISIBLE);
            holder.mTvFlag.setText("未开始");
            holder.mTvFlag.setBackgroundResource(R.drawable.yellow_radius_bg);
            holder.mTvTitle.setTextColor(Color.WHITE);
            holder.mTvType.setTextColor(Color.WHITE);
            holder.mTvStatus.setTextColor(Color.WHITE);
            holder.mTvTime.setTextColor(Color.WHITE);
        } else if (itemModel.getStatus() == 3) {
            holder.mTvFlag.setVisibility(View.VISIBLE);
            holder.mTvFlag.setText("已作废");
            holder.mTvFlag.setBackgroundResource(R.drawable.yellow_radius_bg);
            holder.mTvTitle.setTextColor(0x8CCCCCCC);
            holder.mTvType.setTextColor(0x8CCCCCCC);
            holder.mTvStatus.setTextColor(0x8CCCCCCC);
            holder.mTvTime.setTextColor(0x8CCCCCCC);
        } else if (itemModel.getStatus() == 2) {
            holder.mTvFlag.setVisibility(View.VISIBLE);
            holder.mTvFlag.setText("已过期");
            holder.mTvFlag.setBackgroundResource(R.drawable.yellow_radius_bg);
            holder.mTvTitle.setTextColor(0x8CCCCCCC);
            holder.mTvType.setTextColor(0x8CCCCCCC);
            holder.mTvStatus.setTextColor(0x8CCCCCCC);
            holder.mTvTime.setTextColor(0x8CCCCCCC);
        } else {
            holder.mTvTitle.setTextColor(Color.WHITE);
            holder.mTvType.setTextColor(Color.WHITE);
            holder.mTvStatus.setTextColor(Color.WHITE);
            holder.mTvTime.setTextColor(Color.WHITE);
            holder.mTvFlag.setVisibility(View.GONE);
        }

    }

    public class TaskHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;
        private TextView mTvTime;
        private View mVLine;
        private TextView mTvFlag;
        private TextView mTvType;
        private TextView mTvStatus;

        public TaskHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }

        private void initView(View itemView) {
            mRivDot = (RoundedImageView) itemView.findViewById(R.id.riv_dot);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mVLine = (View) itemView.findViewById(R.id.v_line);
            mTvFlag = (TextView) itemView.findViewById(R.id.tv_flag);
            mTvType = (TextView) itemView.findViewById(R.id.tv_type);
            mTvStatus = (TextView) itemView.findViewById(R.id.tv_status);
        }
    }
}
