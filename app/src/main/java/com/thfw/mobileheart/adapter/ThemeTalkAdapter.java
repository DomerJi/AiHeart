package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.ThemeTalkModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 17:12
 * Describe:音频专辑列表
 */
public class ThemeTalkAdapter extends BaseAdapter<ThemeTalkModel, ThemeTalkAdapter.AudioEdtListHolder> {

    public ThemeTalkAdapter(List<ThemeTalkModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public AudioEdtListHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == ThemeTalkModel.TYPE_TOP) {
            return new AudioEdtListHolder(inflate(R.layout.item_theme_talk_top, parent));
        }
        return new AudioEdtListHolder(inflate(R.layout.item_theme_talk_list, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AudioEdtListHolder holder, int position) {
        if (getItemViewType(position) == ThemeTalkModel.TYPE_BODY) {
            ThemeTalkModel model = mDataList.get(position);
            holder.mTvTitle.setText(model.getTitle());
            GlideUtil.load(mContext, model.getPic(), holder.mRivImage);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getItemType();
    }

    public class AudioEdtListHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
        private final TextView mTvHour;
        private final RoundedImageView mRivImage;

        public AudioEdtListHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRivImage = itemView.findViewById(R.id.riv_etc_bg);
            mTvHour = itemView.findViewById(R.id.tv_hour);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });
        }
    }

}
