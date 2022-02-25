package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.thfw.robotheart.util.Dormant.KEY_DORMANT_MINUTE;

/**
 * Author:pengs
 * Date: 2022/2/25 9:38
 * Describe:Todo
 */
public class DormantAdapter extends BaseAdapter<Integer, DormantAdapter.DormantViewHolder> {


    public DormantAdapter(List<Integer> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public DormantViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new DormantViewHolder(inflate(R.layout.item_dormant, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DormantViewHolder holder, int position) {
        int minute = mDataList.get(position);
        holder.mTvTime.setText(minute + "分钟");
        if (SharePreferenceUtil.getInt(KEY_DORMANT_MINUTE, 5) == minute) {
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
                SharePreferenceUtil.setInt(KEY_DORMANT_MINUTE, mDataList.get(getBindingAdapterPosition()));
                notifyDataSetChanged();
            });
        }
    }
}
