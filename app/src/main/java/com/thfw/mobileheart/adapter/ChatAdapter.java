package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.robotheart.R;
import com.thfw.mobileheart.model.ChatEntity;

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
        if (viewType == 0) {
            return new ChatHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_from_to_layout, parent, false));
        } else {
            return new ChatHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_to_from_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatHolder holder, int position) {
        holder.mTvTalk.setText(mDataList.get(position).talk);
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
