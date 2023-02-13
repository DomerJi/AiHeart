package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.FeedBackModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class FeedBackAdapter extends BaseAdapter<FeedBackModel, FeedBackAdapter.ProblemHolder> {


    public FeedBackAdapter(List<FeedBackModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ProblemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ProblemHolder(inflate(R.layout.item_me_common_problem, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProblemHolder holder, int position) {
        FeedBackModel model = mDataList.get(position);
        holder.mTvTime.setText(model.getAddTime());
        holder.mTvTitle.setText(model.getContent());

        if (EmptyUtil.isEmpty(model.getPic())) {
            holder.mIvPic.setVisibility(View.GONE);
        } else {
            holder.mIvPic.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(model.getPic())
                    .apply(GlideUtil.getRequestOptions())
                    .into(holder.mIvPic);
        }
    }

    public class ProblemHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private TextView mTvTime;
        private View mVLine;
        private ImageView mIvPic;

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
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mVLine = (View) itemView.findViewById(R.id.v_line);
            mIvPic = (ImageView) itemView.findViewById(R.id.iv_pic);
        }
    }

    @Override
    public String getText(int position, int type) {
        switch (type){
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                return mDataList.get(position).getContent();
        }

        return super.getText(position, type);
    }
}
