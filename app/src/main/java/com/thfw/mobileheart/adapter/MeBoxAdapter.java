package com.thfw.mobileheart.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.HeartBoxEntity;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/9/17 13:19
 * Describe:Todo
 */
public class MeBoxAdapter extends BaseAdapter<HeartBoxEntity, MeBoxAdapter.MeBoxHolder> {

    public MeBoxAdapter(List<HeartBoxEntity> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public MeBoxAdapter.MeBoxHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MeBoxHolder(LayoutInflater.from(mContext).inflate(R.layout.item_me_box_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MeBoxAdapter.MeBoxHolder holder, int position) {
        HeartBoxEntity entity = mDataList.get(position);
        if (TextUtils.isEmpty(entity.content)) {
            holder.mTvContent.setText("");
            holder.mTvContent.setVisibility(View.GONE);
        } else {
            holder.mTvContent.setText(entity.content);
            holder.mTvContent.setVisibility(View.VISIBLE);
        }
        String[] times = entity.getTimeStr().split("-");
        if (times.length == 1) {
            holder.mTvTime.setText(times[0]);
            holder.mTvTime2.setVisibility(View.GONE);
        } else {
            holder.mTvTime.setText(times[0]);
            holder.mTvTime2.setText(times[1]);
            holder.mTvTime2.setVisibility(View.VISIBLE);
        }
        if (entity.images.size() == 1) {
            holder.mIvSingle.setVisibility(View.VISIBLE);
            holder.mRvImages.setVisibility(View.GONE);
            GlideUtil.load(mContext, entity.images.get(0), holder.mIvSingle);
        } else {
            holder.mIvSingle.setVisibility(View.GONE);
            holder.mRvImages.setAdapter(new BoxImageAdapter(entity.images));
        }

    }

    public class MeBoxHolder extends RecyclerView.ViewHolder {


        private ConstraintLayout mClTime;
        private TextView mTvTime;
        private TextView mTvTime2;
        private TextView mTvFlag;
        private TextView mTvContent;
        private ImageView mIvSingle;
        private RecyclerView mRvImages;

        public MeBoxHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mClTime = (ConstraintLayout) itemView.findViewById(R.id.cl_time);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvTime2 = (TextView) itemView.findViewById(R.id.tv_time2);
            mTvFlag = (TextView) itemView.findViewById(R.id.tv_flag);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
            mIvSingle = (ImageView) itemView.findViewById(R.id.iv_single);
            mRvImages = (RecyclerView) itemView.findViewById(R.id.rv_images);
            mRvImages.setLayoutManager(new GridLayoutManager(mContext, 3));
        }
    }

    public class BoxImageAdapter extends BaseAdapter<String, MeBoxAdapter.ImageHolder> {


        public BoxImageAdapter(List<String> dataList) {
            super(dataList);
        }

        @NonNull
        @NotNull
        @Override
        public ImageHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new ImageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_me_box_image_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ImageHolder holder, int position) {
            GlideUtil.load(mContext, mDataList.get(position), holder.mIvImage);
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder {

        private final ImageView mIvImage;

        public ImageHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mIvImage = itemView.findViewById(R.id.iv_image);
        }
    }
}
