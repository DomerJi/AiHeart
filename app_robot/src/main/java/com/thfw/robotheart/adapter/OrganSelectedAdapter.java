package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.OrganizationModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/7 15:33
 * Describe:Todo
 */
public class OrganSelectedAdapter extends BaseAdapter<OrganizationModel.OrganizationBean, OrganSelectedAdapter.OranSelectedHolder> {

    public OrganSelectedAdapter(List<OrganizationModel.OrganizationBean> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public OranSelectedHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new OranSelectedHolder(inflate(R.layout.item_organ_select, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OranSelectedHolder holder, int position) {
        holder.mTvName.setText(mDataList.get(position).getName());
        holder.mIvNext.setVisibility(mDataList.get(position).isChild() ? View.INVISIBLE : View.VISIBLE);
    }

    public class OranSelectedHolder extends RecyclerView.ViewHolder {

        private final TextView mTvName;
        private final ImageView mIvNext;

        public OranSelectedHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIvNext = itemView.findViewById(R.id.iv_next);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }
}
