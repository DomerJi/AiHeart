package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.TestModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;

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
        return new TestHolder(inflate(R.layout.item_test_list_layout, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TestHolder holder, int position) {
        TestModel testModel = mDataList.get(position);
        GlideUtil.load(mContext, testModel.getPic(), holder.mRivAvatar);
        holder.mTvTitle.setText(testModel.getTitle());
        holder.mTvType.setText(testModel.getNum() + "人测过");
    }

    public class TestHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivAvatar;
        private TextView mTvTitle;
        private TextView mTvNumber;
        private TextView mTvType;

        public TestHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });

        }

        private void initView(View itemView) {
            mRivAvatar = (RoundedImageView) itemView.findViewById(R.id.riv_avatar);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            mTvType = (TextView) itemView.findViewById(R.id.tv_type);
        }
    }
}
