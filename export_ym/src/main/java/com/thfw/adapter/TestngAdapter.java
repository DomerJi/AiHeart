package com.thfw.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.OnRvItemListener;
import com.thfw.export_ym.R;
import com.thfw.models.TestDetailModel;
import com.thfw.util.LogUtil;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/6 11:30
 * Describe:Todo
 */
public class TestngAdapter extends BaseAdapter<TestDetailModel.SubjectListBean, TestngAdapter.TestngHolder> {

    OnLastNextListener onLastNextListener;

    public TestngAdapter(List<TestDetailModel.SubjectListBean> dataList) {
        super(dataList);
    }

    @NonNull
    @Override
    public TestngHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TestngHolder(inflate(R.layout.item_test_ing_ym, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull TestngHolder holder, int position) {
        holder.mTvCurrent.setText(String.valueOf(position + 1));
        holder.mTvFooterCurrent.setText(String.valueOf(position + 1));
        holder.mTvCount.setText(String.valueOf(getItemCount()));
        holder.mTvFooterCount.setText(String.valueOf(getItemCount()));
        TestDetailModel.SubjectListBean subjectListBean = mDataList.get(position);
        holder.mTvTitle.setText(subjectListBean.getDes());
        TestSelectAdapter selectAdapter = new TestSelectAdapter(subjectListBean.getOptionArray());
        selectAdapter.setSelectedIndex(subjectListBean.getSelectedIndex());
        holder.mRvSelects.setAdapter(selectAdapter);
        selectAdapter.setOnRvItemListener(new OnRvItemListener<TestDetailModel.SubjectListBean>() {
            @Override
            public void onItemClick(List<TestDetailModel.SubjectListBean> list, int position) {
                subjectListBean.setSelectedIndex(position);
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), holder.getBindingAdapterPosition());
                }
            }
        });

        holder.mRlLast.setAlpha(position == 0 ? 0.3f : 1f);
        LogUtil.d("JJJ index = " + subjectListBean.getSelectedIndex());
        if (subjectListBean.getSelectedIndex() != -1) {
            holder.mRlNext.setAlpha(1f);
        } else {
            holder.mRlNext.setAlpha(0.3f);
        }

        holder.mRlNext.setOnClickListener(v -> {
            if (position == getItemCount() - 1 || subjectListBean.getSelectedIndex() == -1) {
                return;
            }
            if (onLastNextListener != null) {
                onLastNextListener.onClick(1);
            }
        });
    }

    public void setOnLastNextListener(OnLastNextListener onLastNextListener) {
        this.onLastNextListener = onLastNextListener;
    }

    public interface OnLastNextListener {
        void onClick(int lastOrNext);
    }

    public class TestngHolder extends RecyclerView.ViewHolder {


        private LinearLayout mLlTitle;
        private TextView mTvCurrent;
        private TextView mTvCount;
        private TextView mTvTitle;
        private RecyclerView mRvSelects;
        private LinearLayout mLlPageFooter;
        private RelativeLayout mRlLast;
        private TextView mTvLast;
        private TextView mTvFooterCurrent;
        private TextView mTvFooterCount;
        private RelativeLayout mRlNext;
        private TextView mTvNext;

        public TestngHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
            mRlLast.setOnClickListener(v -> {
                if (getBindingAdapterPosition() == 0) {
                    return;
                }
                if (onLastNextListener != null) {
                    onLastNextListener.onClick(-1);
                }
            });

            mRlNext.setOnClickListener(v -> {
                if (getBindingAdapterPosition() == getItemCount() - 1) {
                    return;
                }
                if (onLastNextListener != null) {
                    onLastNextListener.onClick(1);
                }
            });
        }

        private void initView(View itemView) {
            mLlTitle = (LinearLayout) itemView.findViewById(R.id.ll_title);
            mTvCurrent = (TextView) itemView.findViewById(R.id.tv_current);
            mTvCount = (TextView) itemView.findViewById(R.id.tv_count);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mRvSelects = (RecyclerView) itemView.findViewById(R.id.rv_selects);
            mRvSelects.setLayoutManager(new LinearLayoutManager(mContext));
            mLlPageFooter = (LinearLayout) itemView.findViewById(R.id.ll_page_footer);
            mRlLast = (RelativeLayout) itemView.findViewById(R.id.rl_last);
            mTvLast = (TextView) itemView.findViewById(R.id.tv_last);
            mTvFooterCurrent = (TextView) itemView.findViewById(R.id.tv_footer_current);
            mTvFooterCount = (TextView) itemView.findViewById(R.id.tv_footer_count);
            mRlNext = (RelativeLayout) itemView.findViewById(R.id.rl_next);
            mTvNext = (TextView) itemView.findViewById(R.id.tv_next);
        }
    }
}
