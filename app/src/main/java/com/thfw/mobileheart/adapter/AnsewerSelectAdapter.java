package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.mobileheart.R;
import com.thfw.mobileheart.util.AnsewerModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/18 17:19
 * Describe:Todo
 */
public class AnsewerSelectAdapter extends BaseAdapter<AnsewerModel, AnsewerSelectAdapter.SelectHolder> {

    public AnsewerSelectAdapter(List<AnsewerModel> dataList) {
        super(dataList);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @NonNull
    @NotNull
    @Override
    public SelectHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new SelectHolder(LayoutInflater.from(mContext).inflate(R.layout.item_ansewer_select_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectHolder holder, int position) {
        holder.mTvOrder.setText(mDataList.get(position).order);
        holder.mTvSelectContetn.setText(mDataList.get(position).content);
    }

    public class SelectHolder extends RecyclerView.ViewHolder {

        private TextView mTvOrder;
        private TextView mTvSelectContetn;

        public SelectHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            if (!(mRecyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
                LinearLayout linearLayout = itemView.findViewById(R.id.item_root);
                ViewGroup.LayoutParams lp = linearLayout.getLayoutParams();
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            mTvOrder = (TextView) itemView.findViewById(R.id.tv_order);
            mTvSelectContetn = (TextView) itemView.findViewById(R.id.tv_select_contetn);
        }
    }
}
