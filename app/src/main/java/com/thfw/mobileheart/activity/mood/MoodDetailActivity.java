package com.thfw.mobileheart.activity.mood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NumberUtil;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.constants.CharType;
import com.thfw.mobileheart.view.ChartMarkerView;
import com.thfw.ui.widget.TitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 心情活跃详情/历史
 */
public class MoodDetailActivity extends BaseActivity {

    private static final float CHART_TEXTSIZE = 7.5f;
    /**
     * 展示最近三天
     */
    private static final int COUNT = 30;
    protected final String[] months = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
    protected final String[] parties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };
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
    private int schemeColor;
    private String mCurrentMonth;
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
    private com.github.mikephil.charting.charts.CombinedChart chart;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MoodDetailActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_mood_detail;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

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
        mLlTimeMinute = (LinearLayout) findViewById(R.id.ll_time_minute);
        mTvTimeMinute = (TextView) findViewById(R.id.tv_time_minute);
        mLlTimeDay = (LinearLayout) findViewById(R.id.ll_time_day);
        mTvTimeDay = (TextView) findViewById(R.id.tv_time_day);
        mLlTimeContinuationDay = (LinearLayout) findViewById(R.id.ll_time_continuation_day);
        mTvTimeContinuation = (TextView) findViewById(R.id.tv_time_continuation);
        mClChart = (ConstraintLayout) findViewById(R.id.cl_chart);
        chart = (CombinedChart) findViewById(R.id.combinedChart);
        initChart();
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

        schemeColor = Color.parseColor("#FF7764");
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

    private void setMonthView(int year, int month) {

        mTvYearMonth.setText(year + " - " + String.format("%02d", month));
        onMonthHasDay(year + "-" + String.format("%02d", month));
    }

    public boolean canSelected(int day) {
        if (mAllHasDayMap.containsKey(mCurrentMonth)) {
            return mAllHasDayMap.get(mCurrentMonth).contains(String.format("%02d", day));
        }
        return true;
        // todo test
//        return false;
    }

    /**
     * 根据月份获取，该月份下的有数据的天
     *
     * @param month
     */
    private void onMonthHasDay(String month) {
        mCurrentMonth = month;
        mCalendarView.clearSchemeDate();
        // 有月份下数据
        if (mAllHasDayMap.containsKey(month)) {
            List<Calendar> calendars = mCalendarView.getCurrentMonthCalendars();
            for (Calendar calendar : calendars) {
                if (calendar.isCurrentMonth() && canSelected(calendar.getDay())) {
                    calendar.setSchemeColor(schemeColor);
                    mCalendarView.addSchemeDate(calendar);
                }
            }
            return;
        }
        // 没有去请求数据
//        new TalkPresenter<>(new TalkPresenter.TalkUi<List<String>>() {
//            @Override
//            public LifecycleProvider getLifecycleProvider() {
//                return TalkHistoryActivity.this;
//            }
//
//            @Override
//            public void onSuccess(List<String> data) {
//                if (data != null) {
//
//                    HashSet<String> mHasDays = new HashSet<>();
//
//                    for (String day : data) {
//                        if (!TextUtils.isEmpty(day)) {
//                            String[] ymd = day.split("-");
//                            if (ymd.length == 3) {
//                                mHasDays.add(ymd[2]);
//                            }
//                        }
//                    }
//                    LogUtil.d(TAG, "mHasDays = " + GsonUtil.toJson(mHasDays));
//                    mAllHasDayMap.put(mCurrentMonth, mHasDays);
//                    onMonthHasDay(mCurrentMonth);
//
//                }
//            }
//
//            @Override
//            public void onFail(ResponeThrowable throwable) {
//
//            }
//        }).onMonthHasDay(scene, month);
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

        // Y轴右侧
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setGridColor(Color.parseColor("#CCCCCC"));
        rightAxis.setDrawGridLines(true);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        // Y轴左侧
        YAxis leftAxis = chart.getAxisLeft();

        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
//        leftAxis.setLabelCount(10);
//        leftAxis.setGridColor(Color.parseColor("#8CFC5262"));
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setDrawTopYLabelEntry(true);
        leftAxis.setTextColor(Color.parseColor("#FC5262"));
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf(Math.round(value / 5));
            }
        });
        // X轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "第" + Math.round(value + 1) + "天";
            }
        });


        CombinedData data = new CombinedData();
        data.setDrawValues(Util.isPad(mContext));
        data.setData(generateLineData());
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                if (entry.getData() instanceof CharType && (CharType) entry.getData() == CharType.MOOD) {
                    return NumberUtil.getPeople(entry.getY() / 5);
                }
                return super.getPointLabel(entry);
            }
        });
        data.setData(generateBarData());
//        data.setData(generateBubbleData());
//        data.setData(generateScatterData());
//        data.setData(generateCandleData());
        data.setValueTypeface(tfLight);
        data.setDrawValues(false);
        xAxis.setAxisMaximum(data.getXMax() + 0.5f);
        LogUtil.d(TAG, "getAxisMaximum = " + xAxis.getAxisMaximum());
        chart.setData(data);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);

        chart.setVisibleXRange(0, 3);
        chart.moveViewTo(data.getXMax(), 0, YAxis.AxisDependency.LEFT);
        chart.invalidate();
        chart.setMarker(new ChartMarkerView(this, R.layout.item_chart_indicator));
//        chart.invalidate();
    }

    /**
     * 情绪数值 #FC5262
     *
     * @return
     */
    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();


        for (int index = 0; index < COUNT; index++) {
            Entry entry = new Entry(index + 0.5f, getRandom(50, 0));
            entry.setData(CharType.MOOD);
            entries.add(entry);
        }

        LineDataSet set = new LineDataSet(entries, "情绪数值");
        set.setColor(Color.parseColor("#FC5262"));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.parseColor("#FC5262"));
        set.setCircleRadius(0f);
        set.setFillColor(Color.WHITE);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(CHART_TEXTSIZE);
        set.setValueTextColor(Color.parseColor("#FC5262"));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
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
            BarEntry barEntry1 = new BarEntry(0, getRandom(35, 15));
            barEntry1.setData(CharType.TALK);
            entries1.add(barEntry1);

            BarEntry barEntry2 = new BarEntry(0, getRandom(35, 15));
            barEntry2.setData(CharType.AI_TALK);
            entries2.add(barEntry2);

            BarEntry barEntry3 = new BarEntry(0, getRandom(35, 15));
            barEntry3.setData(CharType.TEST);
            entries3.add(barEntry3);

            BarEntry barEntry4 = new BarEntry(0, getRandom(35, 15));
            barEntry4.setData(CharType.AUDIO);
            entries4.add(barEntry4);

            BarEntry barEntry5 = new BarEntry(0, getRandom(35, 15));
            barEntry5.setData(CharType.VIDEO);
            entries5.add(barEntry5);


            BarEntry barEntry6 = new BarEntry(0, getRandom(35, 15));
            barEntry6.setData(CharType.TOOL);
            entries6.add(barEntry6);

            BarEntry barEntry7 = new BarEntry(0, getRandom(35, 15));
            barEntry7.setData(CharType.BOOK);
            entries7.add(barEntry7);

            BarEntry barEntry8 = new BarEntry(0, getRandom(35, 15));
            barEntry8.setData(CharType.IDEO_BOOK);
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

}