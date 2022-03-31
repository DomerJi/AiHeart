package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.ExerciseModel;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/8 15:04
 * Describe:
 */
public class ExerciseAdapter extends BaseAdapter<ExerciseModel, ExerciseAdapter.ExercisHolder> {

    public ExerciseAdapter(List<ExerciseModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ExercisHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ExercisHolder(inflate(R.layout.item_exercise_layout, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExercisHolder holder, int position) {
        ExerciseModel model = mDataList.get(position);
        holder.mTvTitle.setText(model.getTitle());
        holder.mTvHint.setText(model.getDesc());
        GlideUtil.load(mContext, model.getPic(), holder.mRivBg);
        if (model.getHistoryCount() == model.getCount()) {
            holder.mTvHour.setText("已完成");
            holder.mTvHour.setTextColor(mContext.getResources().getColor(R.color.text_yellow));
        } else if (model.getHistoryCount() > 0) {
            holder.mTvHour.setTextColor(mContext.getResources().getColor(R.color.text_green));
            String mHour = "已完成 <font color='" + UIConfig.COLOR_HOUR + "'>" + model.getHistoryCount()
                    + "</font>/" + model.getCount() + "课时";
            holder.mTvHour.setText(HtmlCompat.fromHtml(mHour, HtmlCompat.FROM_HTML_MODE_LEGACY));
        } else {
            holder.mTvHour.setTextColor(mContext.getResources().getColor(R.color.text_green));
            holder.mTvHour.setText(model.getCount() + "课时");
        }
    }

    public class ExercisHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivBg;
        private TextView mTvTitle;
        private TextView mTvHint;
        private TextView mTvHour;

        public ExercisHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);

            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }

        private void initView(View itemView) {
            mRivBg = (RoundedImageView) itemView.findViewById(R.id.riv_bg);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvHint = (TextView) itemView.findViewById(R.id.tv_hint);
            mTvHour = (TextView) itemView.findViewById(R.id.tv_hour);
        }
    }
}
