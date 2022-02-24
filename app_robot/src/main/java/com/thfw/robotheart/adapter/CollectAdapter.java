package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.CollectModel;
import com.thfw.base.utils.HourUtil;
import com.thfw.robotheart.R;

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
    public void onBindViewHolder(@NonNull @NotNull CollectAdapter.CollectHolder holder, int position) {
        CollectModel collectModel = mDataList.get(position);
        holder.mTvTime.setText("收藏时间:" + HourUtil.getYYMMDD_HHMM(System.currentTimeMillis()));
        holder.mTvTitle.setText(collectModel.title);
    }

    public class CollectHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;
        private TextView mTvTimeTime;
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
            mRivDot = (RoundedImageView) itemView.findViewById(R.id.riv_dot);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvTimeTime = (TextView) itemView.findViewById(R.id.tv_time_time);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mVLine = (View) itemView.findViewById(R.id.v_line);
        }
    }
}
