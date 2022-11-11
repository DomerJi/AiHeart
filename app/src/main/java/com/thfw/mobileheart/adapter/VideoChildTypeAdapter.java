package com.thfw.mobileheart.adapter;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.VideoTypeModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/3/14 12:13
 * Describe:Todo
 */
public class VideoChildTypeAdapter extends BaseAdapter<VideoTypeModel, VideoChildTypeAdapter.VideoChildHolder> {

    private int mSelectedIndex = -1;

    public VideoChildTypeAdapter(List<VideoTypeModel> dataList) {
        super(dataList);
    }

    public int getmSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(int mSelectedIndex) {
        this.mSelectedIndex = mSelectedIndex;
    }

    @NonNull
    @NotNull
    @Override
    public VideoChildHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new VideoChildHolder(inflate(R.layout.item_video_child_type_layout, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoChildHolder holder, int position) {
        VideoTypeModel typeModel = mDataList.get(position);
        holder.mTvTitle.setText(typeModel.name);
        holder.mIvFire.setVisibility(typeModel.fire == 1 ? View.VISIBLE : View.GONE);
        holder.mTvTitle.setSelected(position == mSelectedIndex);
        if (typeModel.isChangedColor()) {

            holder.mTvTitle.setTypeface(holder.mTvTitle.isSelected()
                    && typeModel.getSelectedColor() == typeModel.getUnSelectedColor()
                    ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            holder.mTvTitle.setTextColor(holder.mTvTitle.isSelected() ? typeModel.getSelectedColor() : typeModel.getUnSelectedColor());
        } else {
            holder.mTvTitle.setTypeface(Typeface.DEFAULT);
            holder.mTvTitle.setTextColor(mContext.getResources().getColorStateList(R.drawable.textcolor_gray_green_selector));
        }
    }

    public void resetSelectedIndex() {
        this.mSelectedIndex = -1;
        notifyDataSetChanged();
        if (mOnRvItemListener != null) {
            mOnRvItemListener.onItemClick(mDataList, mSelectedIndex);
        }
    }

    public class VideoChildHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
        private final ImageView mIvFire;

        public VideoChildHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mIvFire = itemView.findViewById(R.id.iv_fire);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (mSelectedIndex == position) {
                    mSelectedIndex = -1;
                } else {
                    mSelectedIndex = position;
                }

                notifyDataSetChanged();
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, mSelectedIndex);
                }
            });
        }
    }
}
