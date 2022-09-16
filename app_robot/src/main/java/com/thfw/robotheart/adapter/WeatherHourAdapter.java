package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.WeatherDetailsModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.util.IconUtil;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/9/16 13:22
 * Describe:Todo
 */
public class WeatherHourAdapter extends BaseAdapter<WeatherDetailsModel.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean, WeatherHourAdapter.HourHolder> {


    public WeatherHourAdapter(List<WeatherDetailsModel.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public HourHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new HourHolder(inflate(R.layout.layout_weather_hour_imp, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HourHolder holder, int position) {
        String weather = mDataList.get(position).getWeather();
        GlideUtil.load(mContext, IconUtil.getWeatherIcon(weather), R.mipmap.refresh_cloud, holder.mIvWeather);
        holder.mTvWeather.setText(weather + " " + mDataList.get(position).getHighestTemperature() + "℃");
        holder.mTvTime.setText(mDataList.get(position).getStartTime().split(" ")[1].substring(0, 5));
    }

    public class HourHolder extends RecyclerView.ViewHolder {

        private TextView mTvTime;
        private ImageView mIvWeather;
        private TextView mTvWeather;

        public HourHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mIvWeather = (ImageView) itemView.findViewById(R.id.iv_weather);
            mTvWeather = (TextView) itemView.findViewById(R.id.tv_weather);
        }
    }

}

