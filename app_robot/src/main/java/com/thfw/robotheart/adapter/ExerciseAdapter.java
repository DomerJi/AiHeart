package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.ExerciseModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/6 9:38
 * Describe:Todo
 */
public class ExerciseAdapter extends BaseAdapter<ExerciseModel, ExerciseAdapter.ExerciseHolder> {

    public ExerciseAdapter(List<ExerciseModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ExerciseHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ExerciseHolder(inflate(R.layout.item_exercise, parent));
    }

    @Override
    public int getItemCount() {
        return 80;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExerciseHolder holder, int position) {

    }

    public class ExerciseHolder extends RecyclerView.ViewHolder {

        public ExerciseHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });

        }
    }

}
