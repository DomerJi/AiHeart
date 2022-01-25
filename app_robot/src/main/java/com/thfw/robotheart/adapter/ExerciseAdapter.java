package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.ExerciseModel;
import com.thfw.robotheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/6 9:38
 * Describe:Todo
 */
public class ExerciseAdapter extends BaseAdapter<ExerciseModel, ExerciseAdapter.ExerciseHolder> {


    public ExerciseAdapter(List<ExerciseModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ExerciseHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ExerciseHolder(inflate(R.layout.item_exercise, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExerciseHolder holder, int position) {
        ExerciseModel model = mDataList.get(position);
        holder.mTvTitle.setText(model.getTitle());
        holder.mTvHint.setText(model.getDesc());
        GlideUtil.load(mContext, model.getPic(), holder.mRivAvatar);
        if (model.getHistoryCount() == model.getCount()) {
            holder.mTvHour.setText(model.getCount() + "课时");
            holder.mTvState.setVisibility(View.VISIBLE);
        } else if (model.getHistoryCount() > 0) {
            holder.mTvState.setVisibility(View.GONE);
            holder.mTvHour.setText("已完成" + model.getHistoryCount() + "/" + model.getCount() + "课时");
        } else {
            holder.mTvState.setVisibility(View.GONE);
            holder.mTvHour.setText(model.getCount() + "课时");
        }

    }


    public class ExerciseHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivAvatar;
        private TextView mTvTitle;
        private TextView mTvHour;
        private TextView mTvHint;
        private TextView mTvState;

        public ExerciseHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });

        }

        private void initView(View itemView) {
            mRivAvatar = (RoundedImageView) itemView.findViewById(R.id.riv_avatar);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvHour = (TextView) itemView.findViewById(R.id.tv_hour);
            mTvHint = (TextView) itemView.findViewById(R.id.tv_hint);
            mTvState = (TextView) itemView.findViewById(R.id.tv_state);
        }
    }

}
