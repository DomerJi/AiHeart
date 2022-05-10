package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.TaskItemModel;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.push.helper.PushHandle;
import com.thfw.mobileheart.util.MsgCountManager;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class MsgTaskAdapter extends BaseAdapter<TaskItemModel, MsgTaskAdapter.TaskHolder> {


    public MsgTaskAdapter(List<TaskItemModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TaskHolder(inflate(R.layout.item_msg, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TaskHolder holder, int position) {
        TaskItemModel itemModel = mDataList.get(position);
        GlideUtil.load(mContext, PushHandle.getTaskIcon(itemModel.getTaskType()), holder.mRivType);
        holder.mTvTitle.setText(itemModel.getTitle());
        holder.mTvContent.setVisibility(View.GONE);
        if (MsgCountManager.getInstance().getNumTask() > 0) {
            holder.mVDotState.setVisibility(itemModel.getReadStatus() == 0 ? View.VISIBLE : View.INVISIBLE);
        } else {
            holder.mVDotState.setVisibility(View.INVISIBLE);
        }
        holder.mTvTime.setText(itemModel.getCreateTime());
    }


    public class TaskHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private RoundedImageView mRivType;
        private TextView mTvTitle;
        private TextView mTvContent;
        private View mVDotState;
        private TextView mTvFlag;
        private TextView mTvTime;

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
            mRivType = (RoundedImageView) itemView.findViewById(R.id.riv_type);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
            mVDotState = (View) itemView.findViewById(R.id.v_dot_state);
            mTvFlag = (TextView) itemView.findViewById(R.id.tv_flag);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
