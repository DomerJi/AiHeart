package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.TestModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.robotheart.lhxk.InstructScrollHelper;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.OrderView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 17:12
 * Describe:音频专辑列表
 */
public class TestListAdapter extends BaseAdapter<TestModel, TestListAdapter.AudioEdtListHolder> {

    public TestListAdapter(List<TestModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public AudioEdtListHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AudioEdtListHolder(inflate(R.layout.item_test_list, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AudioEdtListHolder holder, int position) {
        TestModel model = mDataList.get(position);
        String mHour = "<font color='" + UIConfig.COLOR_HOUR + "'>" + model.getNum() + "人</font>完成测试";
        holder.mOrderView.setOrder(position + 1);
        holder.mTvHour.setText(HtmlCompat.fromHtml(mHour, HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.mTvTitle.setText(model.getTitle());
        GlideUtil.load(mContext, model.getPic(), holder.mRivImage);

    }

    @Override
    public String getText(int position, int type) {
        switch (type) {
            case TYPE_SPEAK_TEXT:
                return mDataList.get(position).getTitle();
            case TYPE_SPEAK_ORDER:
                return InstructScrollHelper.speakNumber(position + 1);

        }
        return super.getText(position, type);
    }

    public class AudioEdtListHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
        private final TextView mTvHour;
        private final RoundedImageView mRivImage;
        private final OrderView mOrderView;

        public AudioEdtListHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRivImage = itemView.findViewById(R.id.riv_etc_bg);
            mTvHour = itemView.findViewById(R.id.tv_hour);
            mOrderView = itemView.findViewById(R.id.orderView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });
        }
    }

}
