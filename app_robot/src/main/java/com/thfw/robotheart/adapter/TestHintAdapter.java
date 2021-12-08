package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.TestDetailModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/8 17:17
 * Describe:Todo
 */
public class TestHintAdapter extends BaseAdapter<TestDetailModel.HintBean, TestHintAdapter.TestHintHolder> {

    public TestHintAdapter(List dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public TestHintHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TestHintHolder(inflate(R.layout.item_test_hint, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TestHintHolder holder, int position) {
        holder.mTvHint.setText(mDataList.get(position).des);
        holder.mTvTitle.setText(mDataList.get(position).title);
    }


    public class TestHintHolder extends RecyclerView.ViewHolder {

        private View mVLeftTab;
        private TextView mTvTitle;
        private TextView mTvHint;

        public TestHintHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mVLeftTab = (View) itemView.findViewById(R.id.v_left_tab);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvHint = (TextView) itemView.findViewById(R.id.tv_hint);
        }
    }
}
