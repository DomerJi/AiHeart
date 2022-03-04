package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.AudioEtcDetailModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/3 14:36
 * Describe:Todo
 */
public class AudioItemAdapter extends BaseAdapter<AudioEtcDetailModel.AudioItemModel, AudioItemAdapter.AudioItemHolder> {

    private int mCurrentIndex = 0;

    public AudioItemAdapter(List<AudioEtcDetailModel.AudioItemModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public AudioItemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AudioItemHolder(inflate(R.layout.item_audio_item_list, parent));
    }

    public void setCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AudioItemHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position).getTitle());
        holder.mTvTitle.setSelected(mCurrentIndex == position);
        holder.mTvCurrentPlay.setVisibility(mCurrentIndex == position ? View.VISIBLE : View.GONE);
        holder.mTvOrder.setText((position + 1) + ".");
        holder.mTvOrder.setSelected(mCurrentIndex == position);
        holder.mTvDuration.setText("时长：" + mDataList.get(position).getDuration());
        if (mDataList.get(position).status == 1) {
            holder.mTvFlag.setVisibility(View.VISIBLE);
        } else {
            holder.mTvFlag.setVisibility(View.GONE);
        }
    }

    public class AudioItemHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private TextView mTvCurrentPlay;
        private TextView mTvCollectState;
        private TextView mTvOrder;
        private TextView mTvFlag;
        private TextView mTvDuration;

        public AudioItemHolder(@NonNull @NotNull View itemView) {
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
            mTvCurrentPlay = (TextView) itemView.findViewById(R.id.tv_current_play);
            mTvCollectState = (TextView) itemView.findViewById(R.id.tv_collect_state);
            mTvFlag = itemView.findViewById(R.id.tv_flag);
            mTvDuration = itemView.findViewById(R.id.tv_duration);
        }
    }
}
