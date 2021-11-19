package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.mobileheart.model.TestModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/8 15:04
 * Describe:
 */
public class TestOneAdapter extends BaseAdapter<TestModel, TestOneAdapter.TestHolder> {

    public TestOneAdapter(List<TestModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public TestHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TestHolder(LayoutInflater.from(mContext).inflate(R.layout.item_test_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TestHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class TestHolder extends RecyclerView.ViewHolder {

        public TestHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(null, getBindingAdapterPosition());
                }
            });

        }
    }
}
