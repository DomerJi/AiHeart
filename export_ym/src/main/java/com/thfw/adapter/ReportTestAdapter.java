package com.thfw.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.export_ym.R;
import com.thfw.models.ReportTestModel;


import java.util.List;

/**
 * Author:pengs
 * Date: 2022/2/10 11:02
 * Describe:Todo
 */
public class ReportTestAdapter extends BaseAdapter<ReportTestModel, ReportTestAdapter.ReportHolder> {


    public ReportTestAdapter(List<ReportTestModel> dataList) {
        super(dataList);
    }

    @NonNull
    @Override
    public ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReportHolder(inflate(R.layout.item_report_test_ym, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ReportHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position).getTitle());
        holder.mTvTime.setText(mDataList.get(position).getAddTime());
    }

    public class ReportHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;
        private TextView mTvTime;
        private View mVLine;

        public ReportHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }

        private void initView(View itemView) {
            mRivDot = (RoundedImageView) itemView.findViewById(R.id.riv_dot);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mVLine = (View) itemView.findViewById(R.id.v_line);
        }
    }
}
