package com.thfw.robotheart.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.CollectModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.lhxk.InstructScrollHelper;
import com.thfw.ui.widget.OrderView;

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
        holder.mTvTime.setText(collectModel.duration);
        holder.mTvTitle.setText(collectModel.title);
        holder.mOrderView.setOrder(position + 1);
        if (TextUtils.isEmpty(collectModel.type)) {
            holder.mTvType.setVisibility(View.INVISIBLE);
        } else {
            holder.mTvType.setVisibility(View.VISIBLE);
            holder.mTvType.setText("分类：" + collectModel.type);
        }

    }


    @Override
    public String getText(int position, int type) {
        switch (type) {
            case TYPE_SPEAK_TEXT:
                return mDataList.get(position).title;
            case TYPE_SPEAK_ORDER:
                return InstructScrollHelper.speakNumber(position + 1);

        }
        return super.getText(position, type);
    }

    public class CollectHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;
        private TextView mTvTimeTime;
        private TextView mTvType;
        private TextView mTvTime;
        private View mVLine;
        private OrderView mOrderView;

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
            mTvType = (TextView) itemView.findViewById(R.id.tv_type);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mVLine = (View) itemView.findViewById(R.id.v_line);
            mOrderView = itemView.findViewById(R.id.orderView);
        }
    }
}
