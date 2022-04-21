package com.thfw.mobileheart.activity.mood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.thfw.base.models.CharType;
import com.thfw.base.models.MoodActiveModel;
import com.thfw.base.models.MoodLivelyModel;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.MobilePresenter;
import com.thfw.base.utils.FunctionType;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.util.FunctionDurationUtil;
import com.thfw.mobileheart.util.MoodLivelyHelper;
import com.thfw.mobileheart.view.ChartMarkerView;
import com.thfw.mobileheart.view.MeCombinedChart;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 心情活跃详情/历史
 */
public class MoodDetailActivity extends BaseActivity<MobilePresenter> implements MobilePresenter.MobileUi<List<MoodActiveModel>> {

    private static final float CHART_TEXTSIZE = 7.5f;
    /**
     * 展示最近三天
     */
    private static int COUNT = 0;

    protected Typeface tfRegular;
    protected Typeface tfLight;
    private com.thfw.ui.widget.TitleView mTitleView;
    private androidx.constraintlayout.widget.ConstraintLayout mClCalendar;
    private android.widget.LinearLayout mLlYearMonth;
    private android.widget.ImageView mIvLeftMonth;
    private android.widget.TextView mTvYearMonth;
    private android.widget.ImageView mIvRightMonth;
    private com.haibin.calendarview.CalendarLayout mCalendarLayout;
    private com.haibin.calendarview.CalendarView mCalendarView;
    private HashMap<String, HashSet<String>> mAllHasDayMap = new HashMap<>();
    private HashMap<String, MoodActiveModel> mAllDataMap = new HashMap<>();
    private List<MoodActiveModel> mMainDataList = new ArrayList<>();
    private int schemeColor;
    private int mCurrentYear;
    private int mCurrentMonth;
    private String mCurrentMonthStr;
    private ConstraintLayout mClBrisk;
    private LinearLayout mLlStatus;
    private TextView mTvStatus;
    private LinearLayout mLlTimeMinute;
    private TextView mTvTimeMinute;
    private LinearLayout mLlTimeDay;
    private TextView mTvTimeDay;
    private LinearLayout mLlTimeContinuationDay;
    private TextView mTvTimeContinuation;
    private ConstraintLayout mClChart;
    private MeCombinedChart chart;
    private ImageView mIvMoodStatus;
    private TextView mTvTimeMinuteTitle;
    private CombinedData data;
    private LoadingView mLoadingView;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MoodDetailActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_mood_detail;
    }

    @Override
    public MobilePresenter onCreatePresenter() {
        return new MobilePresenter(this);
    }

    @Override
    public void initView() {
        mLoadingView = findViewById(R.id.loadingView);
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mClCalendar = (ConstraintLayout) findViewById(R.id.cl_calendar);
        mLlYearMonth = (LinearLayout) findViewById(R.id.ll_year_month);
        mIvLeftMonth = (ImageView) findViewById(R.id.iv_left_month);
        mTvYearMonth = (TextView) findViewById(R.id.tv_year_month);
        mIvRightMonth = (ImageView) findViewById(R.id.iv_right_month);
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mClBrisk = (ConstraintLayout) findViewById(R.id.cl_brisk);
        mLlStatus = (LinearLayout) findViewById(R.id.ll_status);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mIvMoodStatus = findViewById(R.id.iv_mood_status);
        mLlTimeMinute = (LinearLayout) findViewById(R.id.ll_time_minute);
        mTvTimeMinute = (TextView) findViewById(R.id.tv_time_minute);
        mTvTimeMinuteTitle = (TextView) findViewById(R.id.tv_active_time_title);
        mLlTimeDay = (LinearLayout) findViewById(R.id.ll_time_day);
        mTvTimeDay = (TextView) findViewById(R.id.tv_time_day);
        mLlTimeContinuationDay = (LinearLayout) findViewById(R.id.ll_time_continuation_day);
        mTvTimeContinuation = (TextView) findViewById(R.id.tv_time_continuation);
        mClChart = (ConstraintLayout) findViewById(R.id.cl_chart);
        chart = (MeCombinedChart) findViewById(R.id.combinedChart);
        chart.setNoDataText("");
        chart.setNoDataTextColor(Color.GRAY);
        setToday();
    }

    private void setToday() {
        if (null != MoodLivelyHelper.getModel()) {
            MoodLivelyModel data = MoodLivelyHelper.getModel();
            mTvTimeMinute.setText(FunctionDurationUtil.getFunctionTimeHour(FunctionType.FUNCTION_APP));
            mTvTimeContinuation.setText(String.valueOf(data.getContinueDays()));
            mTvTimeDay.setText(String.valueOf(data.getLoginDays()));
            mTvTimeMinuteTitle.setText("今日活跃");
            if (data.getUserMood() != null) {
                GlideUtil.load(mContext, data.getUserMood().getPath(), mIvMoodStatus);
                mTvStatus.setText(data.getUserMood().getName());
            }
        }
    }

    @Override
    public void initData() {
        mCalendarView.setOnCalendarInterceptListener(new CalendarView.OnCalendarInterceptListener() {
            @Override
            public boolean onCalendarIntercept(Calendar calendar) {
                if (!calendar.isCurrentMonth() || !canSelected(calendar.getDay())) {
                    return true;
                }
                return false;
            }

            @Override
            public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {

            }
        });

        schemeColor = Color.parseColor("#FFAE00");
        mCalendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                setMonthView(year, month);
                mCalendarView.clearSingleSelect();
            }
        });

        mCalendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                if (calendar.getDay() == mCalendarView.getCurDay()
                        && calendar.getMonth() == mCalendarView.getCurMonth()
                        && calendar.getYear() == mCalendarView.getCurYear()) {
                    setToday();
                } else {
                    String selectDay = calendar.getYear()
                            + "-" + String.format("%02d", calendar.getMonth())
                            + "-" + String.format("%02d", calendar.getDay());
                    if (mAllDataMap.containsKey(selectDay)) {
                        MoodActiveModel model = mAllDataMap.get(selectDay);
                        long minute = model.getActiveTime(FunctionType.FUNCTION_APP) / (60 * 1000);
                        mTvTimeMinute.setText(String.valueOf(minute));
                        if (!TextUtils.isEmpty(model.getMoodPic()) && !TextUtils.isEmpty(model.getOriginName())) {
                            GlideUtil.load(mContext, model.getMoodPic(), mIvMoodStatus);
                            mTvStatus.setText(model.getMoodName());
                        }
                    }
                    String monthDay = String.format("%02d", calendar.getMonth())
                            + "-" + String.format("%02d", calendar.getDay());

                    mTvTimeMinuteTitle.setText(monthDay + "：活跃");
                }
                if (data != null) {
                    chart.post(new Runnable() {
                        @Override
                        public void run() {
                            chart.moveViewTo(data.getXMax() / COUNT * (calendar.getDay() - 1), 0, YAxis.AxisDependency.LEFT);
                        }
                    });
                }
            }
        });

        mIvLeftMonth.setOnClickListener(v -> {
            mCalendarView.scrollToPre(true);
        });

        mIvRightMonth.setOnClickListener(v -> {
            mCalendarView.scrollToNext(true);
        });

        mCalendarView.setOnViewChangeListener(new CalendarView.OnViewChangeListener() {
            @Override
            public void onViewChange(boolean isMonthView) {
                setMonthView(mCalendarView.getCurYear(), mCalendarView.getCurMonth());
            }
        });

    }

    private NetParams getNetParams(String startTime, String endTime) {
        return NetParams.crete().add("start_time", startTime)
                .add("end_time", endTime);
    }

    private void setMonthView(int year, int month) {
        mTvYearMonth.setText(year + " - " + String.format("%02d", month));
        onMonthHasDay(year, month);

    }

    public boolean canSelected(int day) {
        if (mAllHasDayMap.containsKey(mCurrentMonthStr)) {
            return mAllHasDayMap.get(mCurrentMonthStr).contains(String.format("%02d", day));
        }
        return false;
    }

    /**
     * 根据月份获取，该月份下的有数据的天
     *
     * @param month
     */
    private void onMonthHasDay(int year, int month) {
        String startTime;
        String endTime;
        if (month == 12) {
            startTime = year + "-" + String.format("%02d", month) + "-01";
            endTime = year + 1 + "-" + String.format("%02d", 1) + "-01";
        } else {
            startTime = year + "-" + String.format("%02d", month) + "-01";
            endTime = year + "-" + String.format("%02d", month + 1) + "-01";
        }
        this.mCurrentYear = year;
        this.mCurrentMonth = month;
        mCurrentMonthStr = year + "-" + String.format("%02d", month);
        mCalendarView.clearSchemeDate();
        // 有月份下数据
        if (mAllHasDayMap.containsKey(mCurrentMonthStr)) {
            List<Calendar> calendars = mCalendarView.getCurrentMonthCalendars();
            for (Calendar calendar : calendars) {
                if (calendar.isCurrentMonth() && canSelected(calendar.getDay())) {
                    calendar.setSchemeColor(schemeColor);
                    mCalendarView.addSchemeDate(calendar);
                }
            }
            setChartData();
            mLoadingView.hide();
            return;
        }
        chart.setData(null);
        chart.invalidate();
        mLoadingView.showLoading();
        mPresenter.onGetUserList(getNetParams(startTime, endTime));
    }

    private void setChartData() {
        int size = getDaysOfMonth(HourUtil.getYYMMDDbyLong(mCurrentMonthStr + "-01"));
        COUNT = size;
        LogUtil.d(TAG, "setChartData COUNT -> " + COUNT);
        mMainDataList.clear();
        for (int i = 0; i < size; i++) {
            String ymd = mCurrentMonthStr + String.format("-%02d", i + 1);
            LogUtil.d(TAG, "setChartData ymd -> " + ymd);
            if (mAllDataMap.containsKey(ymd)) {
                mMainDataList.add(mAllDataMap.get(ymd));
            } else {
                mMainDataList.add(new MoodActiveModel().setTime(ymd));
            }
        }
        initChart();
    }

    //获取一个月天数
    public static int getDaysOfMonth(long time) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
    }


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        tfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
    }

    private void initChart() {


        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        // 图例
        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextSize(CHART_TEXTSIZE);


        // Y轴左侧
        YAxis leftAxis = chart.getAxisLeft();

        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
//        leftAxis.setLabelCount(10);
//        leftAxis.setGridColor(Color.parseColor("#8CFC5262"));
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setDrawTopYLabelEntry(true);
        leftAxis.setTextColor(Color.parseColor("#FC5262"));

        // X轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (mMainDataList.size() > (int) value && value >= 0) {
                    return mMainDataList.get((int) value).getTimeMD();
                }
                return "value:" + value;
            }
        });


        // Y轴右侧
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setGridColor(Color.parseColor("#CCCCCC"));
        rightAxis.setDrawGridLines(true);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setLabelCount(6, true);
        leftAxis.setLabelCount(6, true);


        data = new CombinedData();
        data.setData(generateBarData());
        float rightAxisMax = getRightAxisMax();
        data.setData(generateLineData(rightAxisMax));

        leftAxis.setAxisMaximum(rightAxisMax);
        rightAxis.setAxisMaximum(rightAxisMax);
        xAxis.setAxisMaximum(data.getXMax() + 0.5f);
//        data.setData(generateBubbleData());
//        data.setData(generateScatterData());
//        data.setData(generateCandleData());
        data.setValueTypeface(tfLight);
        data.setDrawValues(false);
        // 情绪数值
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 按照10 比 右侧最大时间比例计算
                return String.valueOf(Math.round(value * 10 / leftAxis.getAxisMaximum()));
            }
        });
        chart.setData(data);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setVisibleXRange(0, Util.isPad(mContext) ? 3.5f : 2.5f);
        chart.invalidate();

        ChartMarkerView chartMarkerView = new ChartMarkerView(this, R.layout.item_chart_indicator);
        chartMarkerView.setMaxHeight(chart.getHeight());
        chart.setMarker(chartMarkerView);
    }

    public float getRightAxisMax() {
        int yMax = (int) (data.getYMax() + 0.5f);
        float lineMax = 0f;
        if (yMax > 100) {
            int scale = yMax / 25;
            if (yMax % 25 != 0) {
                lineMax = (scale + 1) * 25;
            } else {
                lineMax = scale * 25;
            }
        } else {
            if (yMax < 10) {
                lineMax = 10;
            } else {
                int scale = yMax / 5;
                if (yMax % 5 != 0) {
                    lineMax = (scale + 1) * 5;
                } else {
                    lineMax = scale * 5;
                }
            }
        }

        return lineMax;
    }

    /**
     * 情绪数值 #FC5262
     *
     * @return
     */
    private LineData generateLineData(float yMax) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();


        for (int index = 0; index < COUNT; index++) {
            MoodActiveModel model = mMainDataList.get(index);
            float y = model.getMoodValue() * Math.round(yMax) / 10;
            Entry entry = new Entry(index, y);
            entry.setData(model.createType(CharType.MOOD));
            entries.add(entry);
        }

        LineDataSet set = new LineDataSet(entries, "情绪数值");
        set.setColor(Color.parseColor("#FC5262"));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.parseColor("#FC5262"));
        set.setCircleRadius(0f);
        set.setFillColor(Color.WHITE);
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(CHART_TEXTSIZE);
        set.setValueTextColor(Color.parseColor("#FC5262"));
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        d.addDataSet(set);
        return d;
    }

    /**
     * 使用时长
     * 主题对话 #FFB83D
     * 倾诉吐槽 #A0A0A0
     * 测评问卷 #8bf497
     * 正念冥想 #38CEC0
     * 视频集锦 #6792F4
     * 训练工具包 #CF66E3
     * 心理文章 #00AC55
     * 思政文章 #603A86
     *
     * @return
     */
    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();
        ArrayList<BarEntry> entries3 = new ArrayList<>();
        ArrayList<BarEntry> entries4 = new ArrayList<>();
        ArrayList<BarEntry> entries5 = new ArrayList<>();
        ArrayList<BarEntry> entries6 = new ArrayList<>();
        ArrayList<BarEntry> entries7 = new ArrayList<>();
        ArrayList<BarEntry> entries8 = new ArrayList<>();
        for (int index = 0; index < COUNT; index++) {
            MoodActiveModel model = mMainDataList.get(index);
            BarEntry barEntry1 = new BarEntry(0, model.getActiveTimeMinute(CharType.TALK));
            barEntry1.setData(model.createType(CharType.TALK));
            entries1.add(barEntry1);

            BarEntry barEntry2 = new BarEntry(0, model.getActiveTimeMinute(CharType.AI_TALK));
            barEntry2.setData(model.createType(CharType.AI_TALK));
            entries2.add(barEntry2);

            BarEntry barEntry3 = new BarEntry(0, model.getActiveTimeMinute(CharType.TEST));
            barEntry3.setData(model.createType(CharType.TEST));
            entries3.add(barEntry3);

            BarEntry barEntry4 = new BarEntry(0, model.getActiveTimeMinute(CharType.AUDIO));
            barEntry4.setData(model.createType(CharType.AUDIO));
            entries4.add(barEntry4);

            BarEntry barEntry5 = new BarEntry(0, model.getActiveTimeMinute(CharType.VIDEO));
            barEntry5.setData(model.createType(CharType.VIDEO));
            entries5.add(barEntry5);


            BarEntry barEntry6 = new BarEntry(0, model.getActiveTimeMinute(CharType.TOOL));
            barEntry6.setData(model.createType(CharType.TOOL));
            entries6.add(barEntry6);

            BarEntry barEntry7 = new BarEntry(0, model.getActiveTimeMinute(CharType.BOOK));
            barEntry7.setData(model.createType(CharType.BOOK));
            entries7.add(barEntry7);

            BarEntry barEntry8 = new BarEntry(0, model.getActiveTimeMinute(CharType.IDEO_BOOK));
            barEntry8.setData(model.createType(CharType.IDEO_BOOK));
            entries8.add(barEntry8);
        }


        BarDataSet set1 = new BarDataSet(entries1, "主题对话");
        set1.setColor(Color.parseColor("#FFB83D"));
        set1.setValueTextColor(Color.parseColor("#FFB83D"));
        set1.setValueTextSize(CHART_TEXTSIZE);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set2 = new BarDataSet(entries2, "倾诉吐槽");
        set2.setColor(Color.parseColor("#A0A0A0"));
        set2.setValueTextColor(Color.parseColor("#A0A0A0"));
        set2.setValueTextSize(CHART_TEXTSIZE);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set3 = new BarDataSet(entries3, "测评问卷");
        set3.setColor(Color.parseColor("#8bf497"));
        set3.setValueTextColor(Color.parseColor("#8bf497"));
        set3.setValueTextSize(CHART_TEXTSIZE);
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set4 = new BarDataSet(entries4, "正念冥想");
        set4.setColor(Color.parseColor("#38CEC0"));
        set4.setValueTextColor(Color.parseColor("#38CEC0"));
        set4.setValueTextSize(CHART_TEXTSIZE);
        set4.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set5 = new BarDataSet(entries5, "视频集锦");
        set5.setColor(Color.parseColor("#6792F4"));
        set5.setValueTextColor(Color.parseColor("#6792F4"));
        set5.setValueTextSize(CHART_TEXTSIZE);
        set5.setAxisDependency(YAxis.AxisDependency.LEFT);


        BarDataSet set6 = new BarDataSet(entries6, "训练工具包");
        set6.setColor(Color.parseColor("#CF66E3"));
        set6.setValueTextColor(Color.parseColor("#CF66E3"));
        set6.setValueTextSize(CHART_TEXTSIZE);
        set6.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set7 = new BarDataSet(entries7, "心理文章");
        set7.setColor(Color.parseColor("#00AC55"));
        set7.setValueTextColor(Color.parseColor("#00AC55"));
        set7.setValueTextSize(CHART_TEXTSIZE);
        set7.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set8 = new BarDataSet(entries8, "思政文章");
        set8.setColor(Color.parseColor("#603A86"));
        set8.setValueTextColor(Color.parseColor("#603A86"));
        set8.setValueTextSize(CHART_TEXTSIZE);
        set8.setAxisDependency(YAxis.AxisDependency.LEFT);

//        float groupSpace = 0.06f;
//        float barSpace = 0.02f; // x2 dataset
//        float barWidth = 0.45f; // x2 dataset
//        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        float groupSpace = 0.15f; //   = 0.15
        float barSpace = 0.04f; // x8 dataset 0.32
        float barWidth = 0.06625f; // x8 dataset 0.53

        BarData d = new BarData(set1, set2, set3, set4, set5, set6, set7, set8);
        d.setBarWidth(barWidth);
        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0
        return d;
    }

    private ScatterData generateScatterData() {

        ScatterData d = new ScatterData();

        ArrayList<Entry> entries = new ArrayList<>();

        for (float index = 0; index < COUNT; index += 0.5f)
            entries.add(new Entry(index + 0.25f, getRandom(10, 55)));

        ScatterDataSet set = new ScatterDataSet(entries, "Scatter DataSet");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setScatterShapeSize(7.5f);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        d.addDataSet(set);

        return d;
    }

    private CandleData generateCandleData() {

        CandleData d = new CandleData();

        ArrayList<CandleEntry> entries = new ArrayList<>();

        for (int index = 0; index < COUNT; index += 2)
            entries.add(new CandleEntry(index + 1f, 90, 70, 85, 75f));

        CandleDataSet set = new CandleDataSet(entries, "Candle DataSet");
        set.setDecreasingColor(Color.rgb(142, 150, 175));
        set.setShadowColor(Color.DKGRAY);
        set.setBarSpace(0.3f);
        set.setValueTextSize(10f);
        set.setDrawValues(false);
        d.addDataSet(set);

        return d;
    }

    private BubbleData generateBubbleData() {

        BubbleData bd = new BubbleData();

        ArrayList<BubbleEntry> entries = new ArrayList<>();

        for (int index = 0; index < COUNT; index++) {
            float y = getRandom(10, 105);
            float size = getRandom(100, 105);
            entries.add(new BubbleEntry(index + 0.5f, y, size));
        }

        BubbleDataSet set = new BubbleDataSet(entries, "Bubble DataSet");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.WHITE);
        set.setHighlightCircleWidth(1.5f);
        set.setDrawValues(true);
        bd.addDataSet(set);

        return bd;
    }

    protected float getRandom(float range, float start) {
        return (float) (Math.random() * range) + start;
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return MoodDetailActivity.this;
    }


    @Override
    public void onSuccess(List<MoodActiveModel> data) {
        if (data != null) {
            HashSet<String> mHasDays = new HashSet<>();
            String today = HourUtil.getYYMMDD(System.currentTimeMillis());
            boolean isToDayMonth = today.startsWith(mCurrentMonthStr);

            for (MoodActiveModel model : data) {
                if (!TextUtils.isEmpty(model.getTime())) {
                    if (isToDayMonth && today.equals(model.getTime())) {
                        model = getToday();
                    }
                    String[] ymd = model.getTime().split("-");
                    if (ymd.length == 3) {
                        mHasDays.add(ymd[2]);
                    }
                    mAllDataMap.put(model.getTime(), model);
                }
            }
            LogUtil.d(TAG, "mHasDays = " + GsonUtil.toJson(mHasDays));
            mAllHasDayMap.put(mCurrentMonthStr, mHasDays);

            onMonthHasDay(mCurrentYear, mCurrentMonth);
        } else {
            mLoadingView.showEmpty();
        }

    }

    private MoodActiveModel getToday() {

        MoodActiveModel model = new MoodActiveModel();
        model.time = HourUtil.getYYMMDD(System.currentTimeMillis());
        if (MoodLivelyHelper.getModel() != null && MoodLivelyHelper.getModel().getUserMood() != null) {
            model.moodValue = MoodLivelyHelper.getModel().getUserMood().getScore();
            model.moodName = MoodLivelyHelper.getModel().getUserMood().getName();
            model.moodPic = MoodLivelyHelper.getModel().getUserMood().getPath();
        }

        model.activeTime = FunctionDurationUtil.getFunctionTime(FunctionType.FUNCTION_APP);
        model.videoTime = FunctionDurationUtil.getFunctionTime(FunctionType.FUNCTION_VIDEO);
        model.musicTime = FunctionDurationUtil.getFunctionTime(FunctionType.FUNCTION_AUDIO);
        model.articleTime = FunctionDurationUtil.getFunctionTime(FunctionType.FUNCTION_BOOK);
        model.ideologyArticleTime = FunctionDurationUtil.getFunctionTime(FunctionType.FUNCTION_IDEO_BOOK);
        model.topicDialogTime = FunctionDurationUtil.getFunctionTime(FunctionType.FUNCTION_THEME_TALK);
        model.holeDialogTime = FunctionDurationUtil.getFunctionTime(FunctionType.FUNCTION_AI_TALK);
        model.testingTime = FunctionDurationUtil.getFunctionTime(FunctionType.FUNCTION_TEST);

        return model;
    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            onMonthHasDay(mCurrentYear, mCurrentMonth);
        });
    }

}