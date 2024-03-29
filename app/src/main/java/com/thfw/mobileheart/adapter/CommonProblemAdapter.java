package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.CommonProblemModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class CommonProblemAdapter extends BaseAdapter<CommonProblemModel, CommonProblemAdapter.ProblemHolder> {


    public CommonProblemAdapter(List<CommonProblemModel> dataList) {
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
        String question = mDataList.get(position).getQuestion();
        holder.mTvTitle.setText(question);
    }

    public class ProblemHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private View mVLine;

        public ProblemHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }

        private void initView(View itemView) {
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mVLine = (View) itemView.findViewById(R.id.v_line);
        }
    }

    @Override
    public String getText(int position, int type) {
        switch (type){
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                return mDataList.get(position).getQuestion();
        }

        return super.getText(position, type);
    }
}
