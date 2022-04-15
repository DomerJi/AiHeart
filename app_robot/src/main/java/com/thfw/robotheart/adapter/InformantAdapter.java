package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.Informant;
import com.thfw.base.utils.InformantUtil;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/2/25 9:38
 * Describe:Todo
 */
public class InformantAdapter extends BaseAdapter<Informant, InformantAdapter.DormantViewHolder> {

    public InformantAdapter(List<Informant> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public DormantViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new DormantViewHolder(inflate(R.layout.item_informant, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DormantViewHolder holder, int position) {
        Informant informant = mDataList.get(position);
        holder.mTvTime.setText(informant.name);
        if (InformantUtil.getInformant().equals(informant.vnc)) {
            holder.mTvFlag.setVisibility(View.VISIBLE);
        } else {
            holder.mTvFlag.setVisibility(View.INVISIBLE);
        }
    }


    public class DormantViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTime;
        private final TextView mTvFlag;

        public DormantViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvFlag = itemView.findViewById(R.id.tv_flag);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                InformantUtil.setInformant(mDataList.get(position).vnc);
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, position);
                }
                notifyDataSetChanged();
            });
        }
    }

}
