package com.thfw.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.export_ym.R;
import com.thfw.models.TestDetailModel;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:24
 * Describe:音频合集类型列表
 */
public class TestSelectAdapter extends BaseAdapter<TestDetailModel.SubjectListBean, TestSelectAdapter.TestSelectHolder> {

    private int selectedIndex = -1;

    public TestSelectAdapter(List<TestDetailModel.SubjectListBean> dataList) {
        super(dataList);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @NonNull

    @Override
    public TestSelectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TestSelectHolder(inflate(R.layout.item_test_select_ym, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull TestSelectHolder holder, int position) {
        holder.mTvSelect.setSelected(selectedIndex == position);
        holder.mTvSelect.setText(mDataList.get(position).getOption() + ". " + mDataList.get(position).getAnswer());
    }

    public class TestSelectHolder extends RecyclerView.ViewHolder {

        private final TextView mTvSelect;

        public TestSelectHolder(@NonNull View itemView) {
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
