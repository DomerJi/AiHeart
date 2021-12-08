package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.VideoEtcOrderModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/14 16:22
 * Describe:Todo
 */
public class VideoEtcOrderAdapter extends BaseAdapter<VideoEtcOrderModel, VideoEtcOrderAdapter.EtcOrderHolder> {

    public VideoEtcOrderAdapter(List<VideoEtcOrderModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public EtcOrderHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new EtcOrderHolder(LayoutInflater.from(mContext).inflate(R.layout.item_etc_order_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EtcOrderHolder holder, int position) {
        holder.mTvOrder.setText(mDataList.get(position).order);
    }

    public class EtcOrderHolder extends RecyclerView.ViewHolder {
        private final TextView mTvOrder;

        public EtcOrderHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvOrder = itemView.findViewById(R.id.tv_order);
        }
    }
}
