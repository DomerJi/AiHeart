package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TestDetailModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/6 11:30
 * Describe:Todo
 */
public class TestngAdapter extends BaseAdapter<TestDetailModel.SubjectListBean, TestngAdapter.TestngHolder> {

    public TestngAdapter(List<TestDetailModel.SubjectListBean> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public TestngHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TestngHolder(inflate(R.layout.item_test_ing, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TestngHolder holder, int position) {
        holder.mTvPageCurrent.setText(String.valueOf(position + 1));
        holder.mTvPageSum.setText("/" + String.valueOf(getItemCount()));
        TestDetailModel.SubjectListBean subjectListBean = mDataList.get(position);
        holder.mTvTitle.setText(subjectListBean.getDes());
        TestSelectAdapter selectAdapter = new TestSelectAdapter(subjectListBean.getOptionArray());
        selectAdapter.setSelectedIndex(subjectListBean.getSelectedIndex());
        holder.mRvSelect.setAdapter(selectAdapter);
        selectAdapter.setOnRvItemListener(new OnRvItemListener<TestDetailModel.SubjectListBean>() {
            @Override
            public void onItemClick(List<TestDetailModel.SubjectListBean> list, int position) {
                subjectListBean.setSelectedIndex(position);
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), holder.getBindingAdapterPosition());
                }
            }
        });
        holder.mBtConfirm.setEnabled(position != 0);

    }

    public class TestngHolder extends RecyclerView.ViewHolder {

        private TextView mTvSelected;
        private TextView mTvPageCurrent;
        private TextView mTvPageSum;
        private View mVLine;
        private TextView mTvTitle;
        private Button mBtConfirm;
        private RecyclerView mRvSelect;

        public TestngHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mTvSelected = (TextView) itemView.findViewById(R.id.tv_selected);
            mTvPageCurrent = (TextView) itemView.findViewById(R.id.tv_page_current);
            mTvPageSum = (TextView) itemView.findViewById(R.id.tv_page_sum);
            mVLine = (View) itemView.findViewById(R.id.v_line);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mBtConfirm = (Button) itemView.findViewById(R.id.bt_confirm);
            mRvSelect = (RecyclerView) itemView.findViewById(R.id.rvSelect);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mRvSelect.getAdapter() != null
                            && mRvSelect.getAdapter().getItemCount() - 1 == position
                            && position % 2 == 0) {
                        return 2;
                    } else {
                        return 1;
                    }
                }
            });
            mRvSelect.setLayoutManager(gridLayoutManager);

            mBtConfirm.setOnClickListener(v -> {
                if (btnListener != null) {
                    btnListener.onBtnClick(0);
                }
            });
        }
    }

    OnBtnListener btnListener;

    public void setBtnListener(OnBtnListener btnListener) {
        this.btnListener = btnListener;
    }

    public interface OnBtnListener {
        void onBtnClick(int type);
    }
}
