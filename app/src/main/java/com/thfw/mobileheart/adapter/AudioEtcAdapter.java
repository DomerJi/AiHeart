package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/9 10:39
 * Describe:Todo
 */
public class AudioEtcAdapter extends BaseAdapter<AudioEtcModel, AudioEtcAdapter.EtcHolder> {

    public AudioEtcAdapter(List<AudioEtcModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public EtcHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new EtcHolder(LayoutInflater.from(mContext).inflate(R.layout.item_audio_etc_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EtcHolder holder, int position) {
        holder.mTvOrder.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return 30;
    }

    public class EtcHolder extends RecyclerView.ViewHolder {

        private TextView mTvOrder;
        private TextView mTvTitle;
        private TextView mTvListening;
        private TextView mTvTime;
        private TextView mTvStatus;

        public EtcHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });
            mTvOrder = (TextView) itemView.findViewById(R.id.tv_order);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvListening = (TextView) itemView.findViewById(R.id.tv_listening);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvStatus = (TextView) itemView.findViewById(R.id.tv_status);
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
