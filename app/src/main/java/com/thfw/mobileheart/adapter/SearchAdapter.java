package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.SearchResultModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/29 14:18
 * Describe:Todo
 */
public class SearchAdapter extends BaseAdapter<SearchResultModel.ResultBean, SearchAdapter.ResultHolder> {

    private int type = -1;

    public SearchAdapter(List<SearchResultModel.ResultBean> dataList) {
        super(dataList);
        if (!EmptyUtil.isEmpty(dataList)) {
            type = dataList.get(0).getType();
        }
    }

    @NonNull
    @NotNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ResultHolder(inflate(R.layout.item_search_result, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchAdapter.ResultHolder holder, int position) {
        SearchResultModel.ResultBean resultModel = mDataList.get(position);
        holder.mTvTitle.setText(resultModel.getTitle());

        if (type == SearchResultModel.TYPE_HOT_PHONE) {
            holder.mTvPhone.setText(resultModel.getPhone());
            holder.mTvTime.setText(resultModel.getTime());
        }
    }

    public class ResultHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
        private ImageView mIvCall;
        private TextView mTvPhone;
        private TextView mTvTime;

        public ResultHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            if (type == SearchResultModel.TYPE_HOT_PHONE) {
                mIvCall = itemView.findViewById(R.id.iv_call);
                mIvCall.setVisibility(View.VISIBLE);
                mTvPhone = itemView.findViewById(R.id.tv_phone);
                mTvPhone.setVisibility(View.VISIBLE);
                mTvTime = itemView.findViewById(R.id.tv_time);
                mTvTime.setVisibility(View.VISIBLE);
            }
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }
}
