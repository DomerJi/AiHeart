package com.thfw.robotheart.activitys.set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.thfw.base.api.MusicApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.WeatherDetailsModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LocationUtils;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.PinYinUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.WeatherUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.adapter.WeatherCityAdapter;
import com.thfw.robotheart.adapter.WeatherHourAdapter;
import com.thfw.robotheart.adapter.WeatherWeekAdapter;
import com.thfw.robotheart.util.IconUtil;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.MyRobotSearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WeatherActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.TextView mTvTemp;
    private android.widget.TextView mTvWeather;
    private android.widget.ImageView mIvWeather;
    private android.widget.LinearLayout mLlTodayDetails;
    private android.widget.TextView mTvPm25;
    private android.widget.TextView mTvWind;
    private android.widget.TextView mTvWindLevel;
    private android.widget.TextView mTvShidu;
    private android.widget.TextView mTvZiwaixian;
    private android.widget.TextView mTvSunTime;
    private androidx.recyclerview.widget.RecyclerView mRvTodayHour;
    private androidx.recyclerview.widget.RecyclerView mRvWeek;
    private TextView mTvAlarmsTitle;
    private TextView mTvAlarmsContent;
    private TextView mTvFuture;
    private android.view.View mVCenter;
    private TextView mTvTempUnit;
    private com.thfw.ui.widget.MyRobotSearchView mMySearch;
    private RecyclerView mRvCitySelect;
    private List<String> hotCityList;
    private androidx.constraintlayout.widget.ConstraintLayout mClSearch;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private TextView mHotCityTitle;
    private WeatherCityAdapter weatherCityAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_weather;
    }

    private static WeatherDetailsModel model;
    private static WeatherDetailsModel firstModel;

    public static WeatherDetailsModel getFirstModel() {
        return firstModel;
    }

    public static final int REQUEST_CODE = 10;

    public static void startActivity(Context mContext, WeatherDetailsModel model) {
        WeatherActivity.model = model;
        ((Activity) mContext).startActivityForResult(new Intent(mContext, WeatherActivity.class), REQUEST_CODE);
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        try {
            if (model == null || model.getValueBean() == null || model.getValueBean().getRealtime() == null) {
                finish();
                return;
            }
            mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
            mTvTemp = (TextView) findViewById(R.id.tv_temp);
            mTvWeather = (TextView) findViewById(R.id.tv_weather);
            mIvWeather = (ImageView) findViewById(R.id.iv_weather);
            mLlTodayDetails = (LinearLayout) findViewById(R.id.ll_today_details);
            mTvPm25 = (TextView) findViewById(R.id.tv_pm25);
            mTvWind = (TextView) findViewById(R.id.tv_wind);
            mTvWindLevel = (TextView) findViewById(R.id.tv_wind_level);
            mTvShidu = (TextView) findViewById(R.id.tv_shidu);
            mTvZiwaixian = (TextView) findViewById(R.id.tv_ziwaixian);
            mTvSunTime = (TextView) findViewById(R.id.tv_sun_time);
            mRvTodayHour = (RecyclerView) findViewById(R.id.rv_today_hour);
            mRvWeek = (RecyclerView) findViewById(R.id.rv_week);
            mTvFuture = (TextView) findViewById(R.id.tv_future);
            mVCenter = (View) findViewById(R.id.v_center);

            mTvTempUnit = (TextView) findViewById(R.id.tv_temp_unit);
            mMySearch = (MyRobotSearchView) findViewById(R.id.my_search);
            mRvCitySelect = (RecyclerView) findViewById(R.id.rv_city_select);


            mTitleRobotView.setCenterText(model.getValueBean().getCity() + "天气");

            Drawable rightDrawable = mContext.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24);
            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            mTitleRobotView.getTvTitle().setCompoundDrawables(null, null, rightDrawable, null);
            mTvPm25.setText(model.getMyPm25Number());
            mTvWind.setText(model.getValueBean().getRealtime().getWD());
            mTvWindLevel.setText(model.getValueBean().getRealtime().getWS());
            mTvZiwaixian.setText(model.getValueBean().getRealtime().getZiwaixian());
            mTvShidu.setText(model.getValueBean().getRealtime().getSD() + "%");

            GlideUtil.load(mContext, IconUtil.getWeatherIcon(model.getMyWeather()), R.mipmap.refresh_cloud, mIvWeather);
            mTvWeather.setText(model.getMyWeather());

            mTvTemp.setText(model.getMyTemp().replaceAll("℃", ""));

            String[] sunInOut = model.getSunInOut();
            if (sunInOut != null && sunInOut.length == 2) {
                mTvSunTime.setText("日出 " + sunInOut[0] + "  日落 " + sunInOut[1]);
            }

            mRvTodayHour.setLayoutManager(new LinearLayoutManager(mContext));
            mRvTodayHour.setAdapter(new WeatherHourAdapter(model.getValueBean().getWeatherDetailsInfo().getWeather3HoursDetailsInfos()));

            mRvWeek.setLayoutManager(new LinearLayoutManager(mContext));
            mRvWeek.setAdapter(new WeatherWeekAdapter(model.getValueBean().getWeathers()));

            mTvAlarmsTitle = (TextView) findViewById(R.id.tv_alarms_title);
            mTvAlarmsContent = (TextView) findViewById(R.id.tv_alarms_content);
            if (!EmptyUtil.isEmpty(model.getValueBean().getAlarms())) {
                mTvAlarmsTitle.setText(model.getValueBean().getAlarms().get(0).getAlarmTypeDesc()
                        + "/" + model.getValueBean().getAlarms().get(0).getAlarmLevelNoDesc());
                mTvAlarmsContent.setText(model.getValueBean().getAlarms().get(0).getAlarmContent());
            } else {
                mTvAlarmsTitle.setText("");
                mTvAlarmsContent.setText("");
            }
        } catch (Exception e) {
            finish();
        }

        mHotCityTitle = findViewById(R.id.tv_hot_city_title);
        mClSearch = (ConstraintLayout) findViewById(R.id.cl_search);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLoadingView.hide();
    }

    @Override
    public void initData() {

        if (hotCityList == null) {
            hotCityList = Arrays.asList(new String[]{LocationUtils.getCityName(), "北京", "上海", "广州", "深圳", "珠海",
                    "佛山", "南京", "苏州", "昆明", "南宁", "成都",
                    "长沙", "福州", "杭州", "西安", "太原", "石家庄"
                    , "沈阳", "重庆", "天津"});
            weatherCityAdapter = new WeatherCityAdapter(null);
            weatherCityAdapter.setOnRvItemListener(new OnRvItemListener<String>() {
                @Override
                public void onItemClick(List<String> list, int position) {
                    hideInput();
                    search(list.get(position), true);
                }
            });
            mRvCitySelect.setAdapter(weatherCityAdapter);
        }

        // 设置布局管理器
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
        // flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal。
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        // 主轴为水平方向，起点在左端。
        // flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列。
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        // 按正常方向换行
        // justifyContent 属性定义了项目在主轴上的对齐方式。
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);//交叉轴的起点对齐。
        mRvCitySelect.setLayoutManager(flexboxLayoutManager);

        mTitleRobotView.getTvTitle().setOnClickListener(v -> {
            if (mClSearch.getVisibility() == View.VISIBLE) {
                Drawable rightDrawable = mContext.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                mTitleRobotView.getTvTitle().setCompoundDrawables(null, null, rightDrawable, null);
                mClSearch.setVisibility(View.GONE);
            } else {
                Drawable rightDrawable = mContext.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                mTitleRobotView.getTvTitle().setCompoundDrawables(null, null, rightDrawable, null);
                mClSearch.setVisibility(View.VISIBLE);
                mMySearch.getEditeText().setText("");
                weatherCityAdapter.setDataListNotify(hotCityList);
            }
        });

        mMySearch.setOnSearchListener(new MyRobotSearchView.OnSearchListener() {

            final float MIN_SCORE = 0.4f;
            float maxScore = MIN_SCORE;
            String oldKey = "";

            @Override
            public void onSearch(String key, boolean clickSearch) {
                key = key.replaceAll(" ", "");
                if (EmptyUtil.isEmpty(key)) {
                    mHotCityTitle.setVisibility(View.VISIBLE);
                    weatherCityAdapter.setDataListNotify(hotCityList);
                } else {
                    if (clickSearch) {
                        hideInput();
                    }
                    if (key.length() < oldKey.length()) {
                        maxScore = key.length() * 0.1f;
                    } else {
                        if (maxScore > key.length() * 0.1f) {
                            maxScore = key.length() * 0.1f;
                        }
                    }
                    if (maxScore < MIN_SCORE) {
                        maxScore = MIN_SCORE;
                    } else if (weatherCityAdapter.getItemCount() == 0) {
                        maxScore = MIN_SCORE;
                    }
                    oldKey = key;
                    mHotCityTitle.setVisibility(View.GONE);
                    List<String> cityList = new ArrayList<>();
                    if (!EmptyUtil.isEmpty(WeatherUtil.getCityIdMap())) {
                        HashMap<String, String> cityIdMap = WeatherUtil.getCityIdMap();
                        Set<String> cityKeys = cityIdMap.keySet();
                        boolean isAbc = key.matches(RegularUtil.REGEX_ABC);
                        int keyLen = key.length();
                        boolean py9Key = false;
                        for (String city : cityKeys) {
                            // 纯字母
                            if (isAbc) {
                                String pinyin = PinYinUtil.getPinYin(city);

                                String[] pyArray = pinyin.split(" ");
                                int score = 0;
                                for (int i = 0; i < pyArray.length; i++) {
                                    if ((i < keyLen && pyArray[i].startsWith(key.substring(i, i + 1)))) {
                                        score++;
                                    } else {
                                        break;
                                    }
                                }
                                if (score >= keyLen) {
                                    py9Key = true;
                                    if (keyLen == pyArray.length) {
                                        cityList.add(0, city);
                                    } else {
                                        cityList.add(city);
                                    }

                                } else {
                                    String newPinyin = pinyin.replace(" ", "");
                                    float tempScore;
                                    if (score > 0 && keyLen >= 3 && newPinyin.startsWith(key)) {
                                        if ((tempScore = PinYinUtil.levenshtein(newPinyin, key)) >= maxScore) {
                                            maxScore = tempScore;
                                            if (py9Key) {
                                                cityList.add(city);
                                            } else {
                                                cityList.add(0, city);
                                            }
                                        } else {
                                            cityList.add(city);
                                        }
                                    } else if ((tempScore = PinYinUtil.levenshtein(newPinyin, key)) >= maxScore) {
                                        maxScore = tempScore;
                                        if (py9Key) {
                                            cityList.add(city);
                                        } else {
                                            cityList.add(0, city);
                                        }
                                    }
                                }
                                // 汉字对比
                            } else {
                                if (city.contains(key)) {
                                    cityList.add(city);
                                }
                            }
                        }
                    }
                    weatherCityAdapter.setDataListNotify(cityList);
                }

            }

            @Override
            public void onClick(View view) {

            }
        });

        search(LocationUtils.getCityName(), false);
    }


    private void search(String cityName, boolean showLoading) {
        if (showLoading) {
            mLoadingView.showLoading();
            mClSearch.setVisibility(View.GONE);
        }
        MusicApi.requestWeather(WeatherUtil.getWeatherCityId(cityName), new MusicApi.WeatherCallback() {
            @Override
            public void onFailure(int code, String msg) {
                if (EmptyUtil.isEmpty(WeatherActivity.this)) {
                    return;
                }
                runOnUiThread(() -> {
                    if (showLoading) {
                        mLoadingView.hide();
                    }
                    ToastUtil.show("天气更新失败,请检查网络连接");
                });
            }

            @Override
            public void onResponse(WeatherDetailsModel weatherInfoModel) {
                if (EmptyUtil.isEmpty(WeatherActivity.this)) {
                    return;
                }
                runOnUiThread(() -> {
                    if (showLoading) {
                        mLoadingView.hide();
                    }
                    if (weatherInfoModel != null) {
                        WeatherActivity.model = weatherInfoModel;
                        if (firstModel == null) {
                            firstModel = weatherInfoModel;
                        }
                    }
                    initView();
                });

            }
        });
        LogUtil.i(TAG, "----------- search weather -------------");
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}