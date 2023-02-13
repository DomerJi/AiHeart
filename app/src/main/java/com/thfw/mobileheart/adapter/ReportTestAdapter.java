package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.ReportTestModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

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
    @NotNull
    @Override
    public ReportHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ReportHolder(inflate(R.layout.item_report_test, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReportHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position).getTitle());
        holder.mTvTime.setText(mDataList.get(position).getAddTime());
    }

    public class ReportHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;
        private ImageView mIvCall;
        private TextView mTvTime;
        private View mVLine;

        public ReportHolder(@NonNull @NotNull View itemView) {
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
            mIvCall = (ImageView) itemView.findViewById(R.id.iv_call);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mVLine = (View) itemView.findViewById(R.id.v_line);
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
