package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.mobileheart.model.AudioModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/9 9:34
 * Describe:Todo
 */
public class HomeVideoListAdapter extends BaseAdapter<AudioModel, HomeVideoListAdapter.VideoHolder> {


    public HomeVideoListAdapter(List dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public VideoHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });
        }
    }
}
