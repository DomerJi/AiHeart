package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.mobileheart.model.AssessReportModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/15 17:22
 * Describe:Todo
 */
public class AssessReportAdapter extends BaseAdapter<AssessReportModel, AssessReportAdapter.AssessHolder> {

    public AssessReportAdapter(List<AssessReportModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public AssessHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AssessHolder(LayoutInflater.from(mContext).inflate(R.layout.item_assess_report_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AssessHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class AssessHolder extends RecyclerView.ViewHolder {

        public AssessHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

}