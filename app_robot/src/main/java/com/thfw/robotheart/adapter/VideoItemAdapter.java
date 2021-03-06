package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.VideoModel;
import com.thfw.robotheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/3 14:36
 * Describe:Todo
 */
public class VideoItemAdapter extends BaseAdapter<VideoModel.RecommendModel, VideoItemAdapter.VideoItemHolder> {

    private int mCurrentIndex = 0;

    public VideoItemAdapter(List<VideoModel.RecommendModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public VideoItemAdapter.VideoItemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new VideoItemHolder(inflate(R.layout.item_video_item_list, parent));
    }

    public void setCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoItemAdapter.VideoItemHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position).getTitle());
        holder.mTvTitle.setSelected(mCurrentIndex == position);
        GlideUtil.loadThumbnail(mContext, mDataList.get(position).getImg(), holder.mRivDot);
    }

    public class VideoItemHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;

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
            mRivDot = (RoundedImageView) itemView.findViewById(R.id.riv_pic);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
