package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.ChatEntity;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends BaseAdapter<ChatEntity, ChatAdapter.ChatHolder> {

    public ChatAdapter(List<ChatEntity> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ChatEntity.TYPE_FROM:// 【对方】 对话
                return new ChatHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_from_to_layout, parent, false));
            case ChatEntity.TYPE_TO: // 【我】对话
                return new ChatHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_to_from_layout, parent, false));
            case ChatEntity.TYPE_END_SERVICE: // 您已结束本次服务
                return new ChatHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_end_service_layout, parent, false));
            case ChatEntity.TYPE_FEEDBACK: // 您对本次回答满意吗？
                return new ChatHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_freeback_layout, parent, false));
            default:
                return new ChatHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_from_to_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatHolder holder, int position) {
        if (holder.mTvTalk != null) {
            holder.mTvTalk.setText(mDataList.get(position).talk);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTalk;

        public ChatHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTalk = itemView.findViewById(R.id.tv_talk);
        }
    }
}
