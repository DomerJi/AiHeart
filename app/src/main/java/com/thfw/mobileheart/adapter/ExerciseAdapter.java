package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.ExerciseModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/8 15:04
 * Describe:
 */
public class ExerciseAdapter extends BaseAdapter<ExerciseModel, ExerciseAdapter.ExercisHolder> {

    public ExerciseAdapter(List<ExerciseModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ExercisHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ExercisHolder(LayoutInflater.from(mContext).inflate(R.layout.item_exercise_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExercisHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ExercisHolder extends RecyclerView.ViewHolder {

        public ExercisHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(null, getBindingAdapterPosition());
                }
            });

        }
    }
}
