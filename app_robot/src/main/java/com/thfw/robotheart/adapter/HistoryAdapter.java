package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.HistoryModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class HistoryAdapter extends BaseAdapter<HistoryModel, HistoryAdapter.HistoryHolder> {


    public HistoryAdapter(List<HistoryModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public HistoryAdapter.HistoryHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new HistoryHolder(inflate(R.layout.item_history_test, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryAdapter.HistoryHolder holder, int position) {
        HistoryModel historyModel = mDataList.get(position);
        if (historyModel instanceof HistoryModel.HistoryTestModel) {
            HistoryModel.HistoryTestModel testModel = (HistoryModel.HistoryTestModel) historyModel;
            holder.mTvTitle.setText(testModel.getTitle());
            holder.mTvTime.setText("测试时间：" + testModel.getAddTime());
        } else if (historyModel instanceof HistoryModel.HistoryVideoModel) {
            HistoryModel.HistoryVideoModel videoModel = (HistoryModel.HistoryVideoModel) historyModel;
            holder.mTvTitle.setText(videoModel.getTitle());
            holder.mTvTime.setText("时间：" + videoModel.getAddTime());
        } else if (historyModel instanceof HistoryModel.HistoryAudioModel) {
            HistoryModel.HistoryAudioModel audioModel = (HistoryModel.HistoryAudioModel) historyModel;
            holder.mTvTitle.setText(audioModel.getTitle());
            holder.mTvSubTitle.setText("合集：" + audioModel.getTitle());
            holder.mTvSubTitle.setVisibility(View.VISIBLE);
            holder.mTvTime.setText("时间：" + audioModel.getAddTime());
        } else if (historyModel instanceof HistoryModel.HistoryBookModel) {
            HistoryModel.HistoryBookModel bookModel = (HistoryModel.HistoryBookModel) historyModel;
            holder.mTvTitle.setText(bookModel.getTitle());
            holder.mTvTime.setText("时间：" + bookModel.getAddTime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getType();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        protected final TextView mTvTitle;
        protected final TextView mTvSubTitle;
        protected final TextView mTvTime;

        public HistoryHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvSubTitle = itemView.findViewById(R.id.tv_sub_title);
            mTvTime = itemView.findViewById(R.id.tv_time);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }
}
