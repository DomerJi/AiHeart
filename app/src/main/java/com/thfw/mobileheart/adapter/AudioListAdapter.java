package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/9 9:34
 * Describe:Todo
 */
public class AudioListAdapter extends BaseAdapter<AudioEtcModel, AudioListAdapter.AudioHolder> {


    public AudioListAdapter(List dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public AudioHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AudioHolder(inflate(R.layout.item_audio_list_layout, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AudioHolder holder, int position) {
        AudioEtcModel model = mDataList.get(position);
        holder.mTvTitle.setText(model.getTitle());
        String mHour;
        if (model.getListenHistorySize() <= 0) {
//            mHour = "<font color='" + UIConfig.COLOR_HOUR + "'>" + bean.getMusicSize()
//                    + "</font>课时";
            holder.mTvState.setText(model.getMusicSize() + "课时");
        } else {
            mHour = "已练习至  <font color='" + UIConfig.COLOR_HOUR + "'>" + model.getListenHistorySize()
                    + "</font>/" + model.getMusicSize() + "  课时";
            holder.mTvState.setText(HtmlCompat.fromHtml(mHour, HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
        GlideUtil.load(mContext, model.getImg(), holder.mRivBg);
    }


    public class AudioHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivBg;
        private TextView mTvTitle;
        private TextView mTvState;

        public AudioHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mRivBg = (RoundedImageView) itemView.findViewById(R.id.riv_bg);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvState = (TextView) itemView.findViewById(R.id.tv_state);

            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });
        }

    }
}
