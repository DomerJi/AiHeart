package com.thfw.robotheart.adapter;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 17:12
 * Describe:音频专辑列表
 */
public class AudioEtcListAdapter extends BaseAdapter<AudioEtcModel, AudioEtcListAdapter.AudioEdtListHolder> {

    public AudioEtcListAdapter(List<AudioEtcModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public AudioEdtListHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AudioEdtListHolder(inflate(R.layout.item_audio_etc_list, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AudioEdtListHolder holder, int position) {
        String mHour = "已练习至  <font color='" + UIConfig.COLOR_HOUR + "'>" + 1
                + "</font>/" + position + 2 + "  课时";

        holder.mTvHour.setText(Html.fromHtml(mHour));
        holder.mTvTitle.setText("Title_" + position);
        GlideUtil.load(mContext, R.mipmap.cat, holder.mRivImage);

    }

    @Override
    public int getItemCount() {
        return 30;
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
