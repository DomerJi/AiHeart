package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.AudioTypeModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:24
 * Describe:音频合集类型列表
 */
public class ExerciseSelectAdapter extends BaseAdapter<AudioTypeModel, ExerciseSelectAdapter.AudioEctTypeHolder> {

    private int selectedIndex = -1;

    public ExerciseSelectAdapter(List<AudioTypeModel> dataList) {
        super(dataList);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @NonNull
    @NotNull
    @Override
    public AudioEctTypeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        return new AudioEctTypeHolder(inflate(R.layout.item_exercise_select, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AudioEctTypeHolder holder, int position) {
        holder.mTvSelect.setSelected(selectedIndex == position);
        holder.mTvSelect.setText("Title_" + position);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class AudioEctTypeHolder extends RecyclerView.ViewHolder {

        private final TextView mTvSelect;

        public AudioEctTypeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvSelect = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(v -> {
                selectedIndex = getBindingAdapterPosition();
                notifyDataSetChanged();
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), selectedIndex);
                }
            });
        }
    }

}
