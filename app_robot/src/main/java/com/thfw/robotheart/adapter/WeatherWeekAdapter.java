package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.WeatherDetailsModel;
import com.thfw.base.utils.HourUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.util.IconUtil;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/9/16 13:22
 * Describe:Todo
 */
public class WeatherWeekAdapter extends BaseAdapter<WeatherDetailsModel.ValueBean.WeathersBean, WeatherWeekAdapter.WeatherWeekHolder> {


    public WeatherWeekAdapter(List<WeatherDetailsModel.ValueBean.WeathersBean> dataList) {
        super(dataList);
        Collections.sort(dataList, new Comparator<WeatherDetailsModel.ValueBean.WeathersBean>() {
            @Override
            public int compare(WeatherDetailsModel.ValueBean.WeathersBean o1, WeatherDetailsModel.ValueBean.WeathersBean o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public WeatherWeekHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new WeatherWeekHolder(inflate(R.layout.layout_weather_week_imp, parent));
    }

    /**
     * "date": "2022-09-15",
     * "img": "8",
     * "sun_down_time": "17:54",
     * "sun_rise_time": "05:28",
     * "temp_day_c": "19",
     * "temp_day_f": "66.2",
     * "temp_night_c": "16",
     * "temp_night_f": "60.8",
     * "wd": "",
     * "weather": "中雨",
     * "week": "星期四",
     * "ws": ""
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull WeatherWeekHolder holder, int position) {
        WeatherDetailsModel.ValueBean.WeathersBean weathersBean = mDataList.get(position);
        String weather = weathersBean.getWeather();
        GlideUtil.load(mContext, IconUtil.getWeatherIcon(weather), R.mipmap.refresh_cloud, holder.mIvWeather);
        holder.mTvWeather.setText(weathersBean.getTempDayC() + "°/" + weathersBean.getTempNightC() + "°");
        if (HourUtil.getYYMMDD(System.currentTimeMillis() - HourUtil.LEN_DAY).equals(weathersBean.getDate())) {
            holder.mTvTime.setText("昨天 · " + weather);
        } else if (HourUtil.getYYMMDD(System.currentTimeMillis()).equals(weathersBean.getDate())) {
            holder.mTvTime.setText("今天 · " + weather);
        } else if (HourUtil.getYYMMDD(System.currentTimeMillis() + HourUtil.LEN_DAY).equals(weathersBean.getDate())) {
            holder.mTvTime.setText("明天 · " + weather);
        } else {
            holder.mTvTime.setText(weathersBean.getWeek().replaceAll("星期", "周") + " · " + weather);
        }
    }


    public class WeatherWeekHolder extends RecyclerView.ViewHolder {

        private TextView mTvTime;
        private ImageView mIvWeather;
        private TextView mTvWeather;

        public WeatherWeekHolder(@NonNull @NotNull View itemView) {
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

