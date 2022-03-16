package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.BookStudyTypeModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/3/14 12:13
 * Describe:Todo
 */
public class StudyChildTypeAdapter extends BaseAdapter<BookStudyTypeModel, StudyChildTypeAdapter.VideoChildHolder> {

    public StudyChildTypeAdapter(List<BookStudyTypeModel> dataList) {
        super(dataList);
    }

    private int mSelectedIndex = -1;

    public int getmSelectedIndex() {
        return mSelectedIndex;
    }

    @NonNull
    @NotNull
    @Override
    public VideoChildHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new VideoChildHolder(inflate(R.layout.item_video_child_type_layout, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoChildHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position).name);
        holder.mTvTitle.setSelected(position == mSelectedIndex);
    }

    public class VideoChildHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;

        public VideoChildHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
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

    public void setSelectedIndex(int mSelectedIndex) {
        this.mSelectedIndex = mSelectedIndex;
    }

    public void resetSelectedIndex() {
        this.mSelectedIndex = -1;
        notifyDataSetChanged();
        if (mOnRvItemListener != null) {
            mOnRvItemListener.onItemClick(mDataList, mSelectedIndex);
        }
    }
}
