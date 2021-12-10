package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.api.HistoryApi;
import com.thfw.base.models.HistoryModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class HistoryAdapter extends BaseAdapter<HistoryModel, RecyclerView.ViewHolder> {


    public HistoryAdapter(List<HistoryModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HistoryApi.TYPE_TEST:
                return new HistoryTestHolder(inflate(R.layout.item_history_test, parent));
            default:
                return new HistoryHolder(inflate(R.layout.item_history_test, parent));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HistoryTestHolder) {
            HistoryModel.HistoryTestModel testModel = (HistoryModel.HistoryTestModel) mDataList.get(position);
            HistoryTestHolder historyHolder = (HistoryTestHolder) holder;
            historyHolder.mTvTitle.setText(testModel.getTitle());
            historyHolder.mTvTime.setText(testModel.getAddTime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getType();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        protected final TextView mTvTitle;
        protected final TextView mTvTime;

        public HistoryHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    public class HistoryTestHolder extends HistoryHolder {

        public HistoryTestHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
