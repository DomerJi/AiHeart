package com.thfw.mobileheart.adapter;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnSpeakTextListener;
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

    private int mSelectedIndex = -1;

    public StudyChildTypeAdapter(List<BookStudyTypeModel> dataList) {
        super(dataList);
    }

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
        BookStudyTypeModel typeModel =  mDataList.get(position);
        holder.mTvTitle.setText(typeModel.name);
        holder.mTvTitle.setSelected(position == mSelectedIndex);
        holder.mIvFire.setVisibility(typeModel.fire == 1 ? View.VISIBLE : View.GONE);
        if (typeModel.fire == 0) {
            holder.mIvFire.setVisibility(View.GONE);
        } else {
            holder.mIvFire.setVisibility(View.VISIBLE);
            holder.mIvFire.setImageLevel(typeModel.fire);
        }
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

    @Override
    public String getText(int position, int type) {
        switch (type){
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                return mDataList.get(position).name;
        }

        return super.getText(position, type);
    }
}
