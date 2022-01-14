package com.thfw.robotheart.activitys.talk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ChatAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TalkHistoryActivity extends RobotBaseActivity<TalkPresenter> implements TalkPresenter.TalkUi<List<DialogTalkModel>> {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvHistory;
    private int scene;
    private ChatAdapter chatAdapter;
    private PageHelper<ChatEntity> pageHelper;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private com.haibin.calendarview.CalendarView mCalendarView;
    private android.widget.LinearLayout mLlCalendar;
    private android.widget.LinearLayout mLlYearMonth;
    private android.widget.ImageView mIvLeftMonth;
    private android.widget.TextView mTvYearMonth;
    private android.widget.ImageView mIvRightMonth;
    private android.widget.Button mBtConfirm;
    private int schemeColor;
    private String data;
    private HashMap<String, HashSet<String>> mAllHasDayMap = new HashMap<>();

    private String mCurrentMonth;
    private int requestLoad = -1;
    private int upId = 0;
    private int downId = 0;

    @Override
    public int getContentView() {
        return R.layout.activity_talk_history;
    }

    public static void startActivity(Context context, int scene) {
        context.startActivity(new Intent(context, TalkHistoryActivity.class)
                .putExtra(KEY_DATA, scene));
    }

    @Override
    public TalkPresenter onCreatePresenter() {
        return new TalkPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvHistory = (RecyclerView) findViewById(R.id.rv_history);
        mRvHistory.setLayoutManager(new LinearLayoutManager(mContext));
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                requestLoad = 1;
                mPresenter.onDialogHistory(scene, "", downId, "next");
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                requestLoad = 0;
                mPresenter.onDialogHistory(scene, "", upId, "prev");
            }
        });
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLoadingView.hide();
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mLlCalendar = (LinearLayout) findViewById(R.id.ll_calendar);
        mLlYearMonth = (LinearLayout) findViewById(R.id.ll_year_month);
        mIvLeftMonth = (ImageView) findViewById(R.id.iv_left_month);
        mTvYearMonth = (TextView) findViewById(R.id.tv_year_month);
        mIvRightMonth = (ImageView) findViewById(R.id.iv_right_month);
        mBtConfirm = (Button) findViewById(R.id.bt_confirm);

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
                mBtConfirm.setEnabled(mCalendarView.getSelectedCalendar() != null);
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

        mBtConfirm.setOnClickListener(v -> {
            Calendar calendar = mCalendarView.getSelectedCalendar();
            if (calendar != null) {
                mLoadingView.showLoading();
                data = HourUtil.getYYMMDD(calendar.getTimeInMillis());
                mPresenter.onDialogHistory(scene, data, 0, "");
                mBtConfirm.setVisibility(View.GONE);
                mLlCalendar.setVisibility(View.GONE);
            }

        });

    }

    private void setMonthView(int year, int month) {
        mBtConfirm.setEnabled(false);

        mTvYearMonth.setText(year + " - " + String.format("%02d", month));
        onMonthHasDay(year + "-" + String.format("%02d", month));
    }

    public boolean canSelected(int day) {
        if (mAllHasDayMap.containsKey(mCurrentMonth)) {
            return mAllHasDayMap.get(mCurrentMonth).contains(String.format("%02d", day));
        }
        return false;
    }

    @Override
    public void initData() {
        scene = getIntent().getIntExtra(KEY_DATA, 1);
        chatAdapter = new ChatAdapter(null);
        mRvHistory.setAdapter(chatAdapter);
        chatAdapter.setRecommendListener((type, recommendInfoBean) -> {
            TalkItemJumpHelper.onItemClick(mContext, type, recommendInfoBean);
        });
        pageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, chatAdapter);


    }

    private void onMonthHasDay(String month) {
        mCurrentMonth = month;
        mCalendarView.clearSchemeDate();
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
        new TalkPresenter<>(new TalkPresenter.TalkUi<List<String>>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return TalkHistoryActivity.this;
            }

            @Override
            public void onSuccess(List<String> data) {
                if (data != null) {

                    HashSet<String> mHasDays = new HashSet<>();

                    for (String day : data) {
                        if (!TextUtils.isEmpty(day)) {
                            String[] ymd = day.split("-");
                            if (ymd.length == 3) {
                                mHasDays.add(ymd[2]);
                            }
                        }
                    }
                    LogUtil.d(TAG, "mHasDays = " + GsonUtil.toJson(mHasDays));
                    mAllHasDayMap.put(mCurrentMonth, mHasDays);
                    onMonthHasDay(mCurrentMonth);

                }
            }

            @Override
            public void onFail(ResponeThrowable throwable) {

            }
        }).onMonthHasDay(scene, month);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<DialogTalkModel> data) {
        ChatEntity lastChatEntity = null;
        ChatEntity firstChatEntity = null;
        boolean end = false;
        List<ChatEntity> list = new ArrayList<>();
        if (data != null) {
            for (DialogTalkModel model : data) {
                ChatEntity chatEntity = new ChatEntity(model);
                chatEntity.setTime(model.getTimeMills());
                if (firstChatEntity == null) {
                    firstChatEntity = chatEntity;
                }
                if (lastChatEntity == null) {
                    list.add(ChatEntity.createTime(chatEntity.time));
                } else {
                    if (Math.abs((chatEntity.time - lastChatEntity.time)) > HourUtil.LEN_MINUTE) {
                        list.add(ChatEntity.createTime(chatEntity.time));
                    }
                }
                list.add(chatEntity);
                lastChatEntity = chatEntity;
            }
            // 根据加载还是刷新，重新赋值 最小upId 和最大downId
            // 控制加载数据的方式，向前或向后
            switch (requestLoad) {
                case -1:
                    upId = firstChatEntity.getTalkModel().getId();
                    downId = lastChatEntity.getTalkModel().getId();
                    pageHelper.onSuccess(list, false);
                    break;
                case 1:// 加载。以后的
                    if (EmptyUtil.isEmpty(list)) {
                        list.add(ChatEntity.createHint("没有更多数据了"));
                        end = true;
                    }
                    if (lastChatEntity != null) {
                        downId = lastChatEntity.getTalkModel().getId();
                    }
                    pageHelper.onSuccess(list, false);
                    if (end) {
                        mRefreshLayout.setEnableLoadMore(false);
                    }
                    break;
                case 0: // 刷新向上，以前的
                    if (EmptyUtil.isEmpty(list)) {
                        list.add(ChatEntity.createHint("没有更多数据了"));
                        end = true;
                    }
                    if (firstChatEntity != null) {
                        upId = firstChatEntity.getTalkModel().getId();
                    }
                    pageHelper.onSuccess(list, true);
                    if (end) {
                        mRefreshLayout.setEnableRefresh(false);
                    }
                    break;
            }
        }
        LogUtil.d(TAG, "requestLoad = " + requestLoad + " ; downId = " + downId + "; upId = " + upId);
        mLoadingView.hide();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        pageHelper.onFail(v -> {
            mPresenter.onDialogHistory(scene, data, 0, "");
        });
    }
}