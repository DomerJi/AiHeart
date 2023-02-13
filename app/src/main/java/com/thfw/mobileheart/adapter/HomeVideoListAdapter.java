package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/9 9:34
 * Describe:Todo
 */
public class HomeVideoListAdapter extends BaseAdapter<VideoEtcModel, HomeVideoListAdapter.VideoHolder> {


    public HomeVideoListAdapter(List<VideoEtcModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new VideoHolder(inflate(R.layout.item_video_list_layout, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoHolder holder, int position) {
        VideoEtcModel etcModel = mDataList.get(position);
        GlideUtil.loadThumbnail(mContext, etcModel.getPic(), holder.mRivBg);
        holder.mTvTitle.setText(etcModel.getTitle());
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivBg;
        private TextView mTvTitle;

        public VideoHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });
        }

        private void initView(View itemView) {
            mRivBg = (RoundedImageView) itemView.findViewById(R.id.riv_bg);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }


    @Override
    public String getText(int position, int type) {
        switch (type){
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                return mDataList.get(position).getTitle();
        }

        return super.getText(position, type);
    }
}
