package com.thfw.mobileheart.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.CollectModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class CollectAdapter extends BaseAdapter<CollectModel, CollectAdapter.CollectHolder> {


    public CollectAdapter(List<CollectModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public CollectHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CollectHolder(inflate(R.layout.item_collect, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CollectHolder holder, int position) {
        CollectModel collectModel = mDataList.get(position);
        holder.mTvTime.setText(collectModel.duration);
        holder.mTvTitle.setText(collectModel.title);
        if (TextUtils.isEmpty(collectModel.type)) {
            holder.mTvType.setVisibility(View.INVISIBLE);
        } else {
            holder.mTvType.setVisibility(View.VISIBLE);
            holder.mTvType.setText("分类：" + collectModel.type);
        }

    }

    public class CollectHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private TextView mTvType;
        private TextView mTvTime;
        private View mVLine;

        public CollectHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }

        private void initView(View itemView) {
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvType = (TextView) itemView.findViewById(R.id.tv_type);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mVLine = (View) itemView.findViewById(R.id.v_line);
        }
    }

    @Override
    public String getText(int position, int type) {
        switch (type){
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                return mDataList.get(position).title;
        }

        return super.getText(position, type);
    }
}
