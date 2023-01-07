package com.thfw.robotheart.adapter;

import static com.thfw.robotheart.util.Dormant.KEY_DORMANT_MINUTE;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.utils.NumberUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.lhxk.InstructScrollHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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


    @Override
    public String getText(int position, int type) {
        switch (type) {
            case TYPE_SPEAK_TEXT:
                return mDataList.get(position) + "分钟," + NumberUtil.convert(mDataList.get(position)) + "分钟";
            case TYPE_SPEAK_ORDER:
                return InstructScrollHelper.speakNumber(position + 1);

        }
        return super.getText(position, type);
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

    @Override
    public void onSpeakItemClick(int position) {
        super.onSpeakItemClick(position);
        SharePreferenceUtil.setInt(KEY_DORMANT_MINUTE, mDataList.get(position));
        notifyDataSetChanged();
    }
}
