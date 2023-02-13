package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/27 15:54
 * Describe:Todo
 */
public class ChatSelectAdapter extends BaseAdapter<DialogTalkModel.CheckRadioBean, ChatSelectAdapter.SelectHolder> {


    public ChatSelectAdapter(List<DialogTalkModel.CheckRadioBean> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public SelectHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new SelectHolder(inflate(R.layout.item_chat_select_radio, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatSelectAdapter.SelectHolder holder, int position) {
        holder.mTvSelect.setText(mDataList.get(position).getValue());
    }

    public class SelectHolder extends RecyclerView.ViewHolder {

        private final TextView mTvSelect;

        public SelectHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvSelect = itemView.findViewById(R.id.tv_select);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }

    @Override
    public String getText(int position, int type) {
        switch (type){
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                return mDataList.get(position).getValue();
        }

        return super.getText(position, type);
    }
}
