package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/3 14:36
 * Describe:Todo
 */
public class VideoItemAdapter extends BaseAdapter<VideoEtcModel, VideoItemAdapter.VideoItemHolder> {

    private int mCurrentIndex = 0;

    public VideoItemAdapter(List<VideoEtcModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public VideoItemAdapter.VideoItemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new VideoItemHolder(inflate(R.layout.item_audio_item_list, parent));
    }

    public void setCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoItemAdapter.VideoItemHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position).getTitle());
        holder.mTvTitle.setSelected(mCurrentIndex == position);
        holder.mTvCurrentPlay.setVisibility(mCurrentIndex == position ? View.VISIBLE : View.GONE);
    }

    public class VideoItemHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;
        private TextView mTvCurrentPlay;
        private TextView mTvCollectState;

        public VideoItemHolder(@NonNull @NotNull View itemView) {
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
