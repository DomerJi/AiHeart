package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.MeAskModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/14 9:23
 * Describe:Todo
 */
public class MeAskListAdapter extends BaseAdapter<MeAskModel, MeAskListAdapter.AskHolder> {

    public MeAskListAdapter(List<MeAskModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public AskHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AskHolder(LayoutInflater.from(mContext).inflate(R.layout.item_me_ask_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AskHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class AskHolder extends RecyclerView.ViewHolder {

        public AskHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
