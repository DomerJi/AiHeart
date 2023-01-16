package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.HomeEntity;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * 自定义布局，下面是常见的图片样式，更多实现可以看demo，可以自己随意发挥
 */
public class ImageAdapter extends BannerAdapter<HomeEntity.BannerModel, ImageAdapter.BannerViewHolder> {

    private final int width;
    private final int height;

    public ImageAdapter(List<HomeEntity.BannerModel> mDatas) {
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        super(mDatas);

        width = (int) (Util.getScreenWidth(MyApplication.getApp()) / 1.5f);
        height = (int) (width / 36 * 21 + 0.5);
    }

    protected View inflate(int layoutId, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new BannerViewHolder(inflate(R.layout.item_banner_image, parent));
    }

    @Override
    public void onBindView(BannerViewHolder holder, HomeEntity.BannerModel data, int position, int size) {
        GlideUtil.load(holder.itemView.getContext(), data.imageUrl, holder.imageView, width, height);
    }

    protected class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_banner);
        }
    }
}