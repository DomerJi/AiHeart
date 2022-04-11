package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.TestResultModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/31 11:28
 * Describe:Todo
 */
public class TestRecommendAdapter extends BaseAdapter<TestResultModel.RecommendInfoBean, TestRecommendAdapter.RecommendHolder> {


    public TestRecommendAdapter(List<TestResultModel.RecommendInfoBean> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public RecommendHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new RecommendHolder(inflate(R.layout.item_test_recommend, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecommendHolder holder, int position) {
        TestResultModel.RecommendInfoBean recommendInfoBean = mDataList.get(position);
        holder.mTvTitle.setText(recommendInfoBean.getInfo().getTitle());
        holder.mTvType.setText(recommendInfoBean.getName());
        GlideUtil.load(mContext, recommendInfoBean.getInfo().getPic(), holder.mRivRecommendBg);
    }

    public class RecommendHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivRecommendBg;
        private TextView mTvTitle;
        private TextView mTvType;

        public RecommendHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }

        private void initView(View itemView) {
            mRivRecommendBg = (RoundedImageView) itemView.findViewById(R.id.riv_recommend_bg);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvType = (TextView) itemView.findViewById(R.id.tv_type);
        }
    }
}
