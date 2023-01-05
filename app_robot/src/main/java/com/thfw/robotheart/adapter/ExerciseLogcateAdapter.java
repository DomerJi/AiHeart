package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.ExerciseModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.lhxk.InstructScrollHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/3 14:36
 * Describe:Todo
 */
public class ExerciseLogcateAdapter extends BaseAdapter<ExerciseModel.LinkModel, ExerciseLogcateAdapter.LogcateItemHolder> {

    private int mCurrentIndex = 0;

    public ExerciseLogcateAdapter(List<ExerciseModel.LinkModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public LogcateItemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new LogcateItemHolder(inflate(R.layout.item_exercise_logcate_list, parent));
    }

    public void setCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LogcateItemHolder holder, int position) {
        ExerciseModel.LinkModel linkModel = mDataList.get(position);
        holder.mTvTitle.setText(linkModel.getTitle());
        holder.mTvOrder.setText((position + 1) + ".");
        switch (linkModel.getStatus()) {
            case 0:
                holder.mTvState.setVisibility(View.GONE);
                holder.mIvLock.setImageResource(R.mipmap.ic_wifi_lock_on);
                holder.mIvLock.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.mTvState.setText("已完成");
                holder.mTvState.setVisibility(View.VISIBLE);
                holder.mIvLock.setVisibility(View.GONE);
                break;
            default:
                holder.mTvState.setVisibility(View.GONE);
                holder.mIvLock.setImageResource(R.mipmap.ic_wifi_local_off);
                holder.mIvLock.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public String getText(int position, int type) {
        switch (type) {
            case TYPE_SPEAK_TEXT:
                return mDataList.get(position).getTitle();
            case TYPE_SPEAK_ORDER:
                return InstructScrollHelper.speakNumber(position + 1);

        }
        return super.getText(position, type);
    }


    public class LogcateItemHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private TextView mTvOrder;
        private TextView mTvState;
        private ImageView mIvLock;

        public LogcateItemHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mCurrentIndex = getBindingAdapterPosition();
                    mOnRvItemListener.onItemClick(getDataList(), mCurrentIndex);
                    notifyDataSetChanged();
                }
            });
        }

        private void initView(View itemView) {
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvOrder = (TextView) itemView.findViewById(R.id.tv_order);
            mTvState = (TextView) itemView.findViewById(R.id.tv_state);
            mIvLock = (ImageView) itemView.findViewById(R.id.iv_lock);
        }
    }
}
