package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.CollectModel;
import com.thfw.base.utils.HourUtil;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class CommonProblemAdapter extends BaseAdapter<CollectModel, CommonProblemAdapter.ProblemHolder> {


    public CommonProblemAdapter(List<CollectModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ProblemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ProblemHolder(inflate(R.layout.item_common_problem, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProblemHolder holder, int position) {
        holder.mTvTime.setText(HourUtil.getYYMMDD_HHMM(System.currentTimeMillis()));
        holder.mTvTitle.setText("Title_" + position);
    }

    @Override
    public int getItemCount() {
        return 30;
    }

    public class ProblemHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivDot;
        private TextView mTvTitle;
        private TextView mTvTimeTime;
        private TextView mTvTime;
        private View mVLine;

        public ProblemHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mRivDot = (RoundedImageView) itemView.findViewById(R.id.riv_dot);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvTimeTime = (TextView) itemView.findViewById(R.id.tv_time_time);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mVLine = (View) itemView.findViewById(R.id.v_line);
        }
    }
}