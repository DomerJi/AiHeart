package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.WeatherDetailsModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/9/16 13:22
 * Describe:Todo
 */
public class WeatherAlarmsAdapter extends BaseAdapter<WeatherDetailsModel.ValueBean.AlarmsBean, WeatherAlarmsAdapter.WeatherWeekHolder> {


    public WeatherAlarmsAdapter(List<WeatherDetailsModel.ValueBean.AlarmsBean> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public WeatherWeekHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new WeatherWeekHolder(inflate(R.layout.layout_weather_alarms_imp, parent));
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

        WeatherDetailsModel.ValueBean.AlarmsBean alarmsBean = mDataList.get(position);
        holder.mTvAlarmsTitle.setText(alarmsBean.getAlarmTypeDesc()
                + "/" + alarmsBean.getAlarmLevelNoDesc());
        holder.mTvAlarmsContent.setText(alarmsBean.getAlarmContent());
    }


    public class WeatherWeekHolder extends RecyclerView.ViewHolder {


        private TextView mTvAlarmsTitle;
        private TextView mTvAlarmsContent;

        public WeatherWeekHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mTvAlarmsTitle = (TextView) itemView.findViewById(R.id.tv_alarms_title);
            mTvAlarmsContent = (TextView) itemView.findViewById(R.id.tv_alarms_content);
        }
    }

}

