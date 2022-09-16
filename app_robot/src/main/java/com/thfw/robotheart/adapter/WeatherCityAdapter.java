package com.thfw.robotheart.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.utils.LocationUtils;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/9/16 16:25
 * Describe:Todo
 */
public class WeatherCityAdapter extends BaseAdapter<String, WeatherCityAdapter.WeatherCityHodler> {


    public WeatherCityAdapter(List<String> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public WeatherCityHodler onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new WeatherCityHodler(inflate(R.layout.item_city_search_history, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull WeatherCityHodler holder, int position) {
        String city = mDataList.get(position);
        if (!TextUtils.isEmpty(LocationUtils.getCityName()) && LocationUtils.getCityName().equals(city)) {
            holder.mTvTitle.setText(city);
            Drawable rightDrawable = mContext.getResources().getDrawable(R.drawable.ic_baseline_location_on_24);
            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            holder.mTvTitle.setCompoundDrawables(null, null, rightDrawable, null);
            holder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.colorRobotFore));
        } else {
            holder.mTvTitle.setText(city);
            holder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.white_60));
            holder.mTvTitle.setCompoundDrawables(null, null, null, null);
        }

    }

    public class WeatherCityHodler extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;

        public WeatherCityHodler(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }
}
