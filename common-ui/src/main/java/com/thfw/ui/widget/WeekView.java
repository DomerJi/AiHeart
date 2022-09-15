package com.thfw.ui.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.thfw.base.utils.HourUtil;

/**
 * Author:pengs
 * Date: 2021/11/22 15:17
 * Describe:Todo
 */
public class WeekView extends TextView {

    public WeekView(Context context) {
        super(context);
        init();
    }

    public WeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        String ymd = HourUtil.getYYMMDD(System.currentTimeMillis()).replaceAll("-", "/");
        setGravity(Gravity.RIGHT);
        setText(ymd + "  " + HourUtil.getWeek(System.currentTimeMillis()));
        initTimeReceiver();
    }


    private void initTimeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        getContext().registerReceiver(broadcastReceiver, filter);
        // 广播的注册，其中Intent.ACTION_TIME_CHANGED代表时间设置变化的时候会发出该广播
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
                String ymd = HourUtil.getYYMMDD(System.currentTimeMillis()).replaceAll("-", "/");
                setText(ymd + "  " + HourUtil.getWeek(System.currentTimeMillis()));
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(broadcastReceiver);
    }
}
