package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.TaskDetailModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class TaskChildLineAdapter extends BaseAdapter<TaskDetailModel.ContentListBean, TaskChildLineAdapter.TaskHolder> {


    public TaskChildLineAdapter(List<TaskDetailModel.ContentListBean> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TaskHolder(inflate(R.layout.item_task_child, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TaskHolder holder, int position) {
        TaskDetailModel.ContentListBean itemModel = mDataList.get(position);
        holder.mTvTitle.setText(itemModel.getTitle());
        if (itemModel.getStatus() == 2) {
            holder.mTvFlag.setVisibility(View.VISIBLE);
            holder.mTvFlag.setText("已过期");
            holder.mTvFlag.setBackgroundResource(R.drawable.yellow_radius_bg);
        } else if (itemModel.getStatus() == 1) {
            holder.mTvFlag.setVisibility(View.VISIBLE);
            holder.mTvFlag.setText("已完成");
            holder.mTvFlag.setBackgroundResource(R.drawable.green_radius_bg);
        } else {
            holder.mTvFlag.setVisibility(View.GONE);
        }

    }

    public class TaskHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;
        private View mVLine;
        private TextView mTvFlag;

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
            mVLine = (View) itemView.findViewById(R.id.v_line);
            mTvFlag = (TextView) itemView.findViewById(R.id.tv_flag);
        }
    }

    @Override
    public String getText(int position, int type) {
        switch (type){
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                return mDataList.get(position).getTitle();
        }

        return super.getText(position, type);
    }
}
