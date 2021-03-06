package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.MTestDetailAdapterModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/3/11 11:48
 * Describe:Todo
 */
public class TestDetailAdapter extends BaseAdapter<MTestDetailAdapterModel, RecyclerView.ViewHolder> {

    public TestDetailAdapter(List<MTestDetailAdapterModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MTestDetailAdapterModel.TYPE_TOP:
                return new TestDetailTopHolder(inflate(R.layout.item_test_detail_top_layout, parent));
            case MTestDetailAdapterModel.TYPE_HINT:
                return new TestDetailHintHolder(inflate(R.layout.item_test_detail_hint, parent));
            default:
                return new TestDetailHolder(inflate(R.layout.item_test_detail_body, parent));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        MTestDetailAdapterModel model = mDataList.get(position);
        if (holder instanceof TestDetailTopHolder) {
            TestDetailTopHolder topHolder = (TestDetailTopHolder) holder;
            GlideUtil.load(mContext, model.getTestDetailModel().getPsychtestInfo().getPic(), topHolder.mIvBanner);
            topHolder.mTvTitle.setText(model.getTestDetailModel().getPsychtestInfo().getTitle());
            topHolder.mTvContent.setText(model.getTestDetailModel().getPsychtestInfo().getIntr());
            topHolder.mTvNumber.setText(model.getTestDetailModel().getPsychtestInfo().getNum() + "人测过");
        } else if (holder instanceof TestDetailHolder) {
            TestDetailHolder detailHolder = (TestDetailHolder) holder;
            detailHolder.mTvTitle.setText(model.getHintBean().title);
            detailHolder.mTvContent.setText(model.getHintBean().des);
        } else {
            TestDetailHintHolder detailHintHolder = (TestDetailHintHolder) holder;
            detailHintHolder.mTvHint.setText(model.getHintBean().title);
            detailHintHolder.mTvHintContent.setText(model.getHintBean().des);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getType();
    }

    public class TestDetailHolder extends RecyclerView.ViewHolder {


        private View mVFlag;
        private TextView mTvTitle;
        private TextView mTvContent;

        public TestDetailHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }


        private void initView(View itemView) {
            mVFlag = (View) itemView.findViewById(R.id.v_flag);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    public class TestDetailTopHolder extends RecyclerView.ViewHolder {

        private ImageView mIvBanner;
        private TextView mTvTitle;
        private TextView mTvNumber;
        private TextView mTvContent;

        public TestDetailTopHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mIvBanner = (ImageView) itemView.findViewById(R.id.iv_banner);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    public class TestDetailHintHolder extends RecyclerView.ViewHolder {

        private TextView mTvHint;
        private TextView mTvHintContent;

        public TestDetailHintHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mTvHint = (TextView) itemView.findViewById(R.id.tv_hint);
            mTvHintContent = (TextView) itemView.findViewById(R.id.tv_hint_content);
        }
    }
}
