package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.AudioEtcDetailModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/3 14:36
 * Describe:Todo
 */
public class ExerciseLogcateAdapter extends BaseAdapter<AudioEtcDetailModel.AudioItemModel, ExerciseLogcateAdapter.LogcateItemHolder> {

    private int mCurrentIndex = 0;

    public ExerciseLogcateAdapter(List<AudioEtcDetailModel.AudioItemModel> dataList) {
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
//        holder.mTvTitle.setText(mDataList.get(position).url);
//        holder.mTvTitle.setSelected(mCurrentIndex == position);
//        holder.mTvCurrentPlay.setVisibility(mCurrentIndex == position ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class LogcateItemHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;
        private TextView mTvCurrentPlay;
        private TextView mTvCollectState;

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
            mRivDot = (RoundedImageView) itemView.findViewById(R.id.riv_dot);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvCurrentPlay = (TextView) itemView.findViewById(R.id.tv_current_play);
            mTvCollectState = (TextView) itemView.findViewById(R.id.tv_collect_state);
        }
    }
}
