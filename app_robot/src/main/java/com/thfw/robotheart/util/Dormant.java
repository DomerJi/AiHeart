package com.thfw.robotheart.util;

import android.content.Context;

import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.activitys.set.DormantActivity;

/**
 * 休眠控制
 */
public class Dormant {

    /**
     * 【N】分后无操作进入休眠
     */
    private static final int MINUTE_FREE_COUNT = 5;

    /**
     * 休眠后，【N分钟】未唤醒进入关机或退出登录
     */
    private static final int SLEEP_TIME_COUNT = 10;
    private static final String TAG_DORMANT = "DORMANT_TIME";

    private static int MINUTE_FREE = 0;

    private static int SLEEP_TIME = 0;


    public static int getSleepTime() {
        return SLEEP_TIME;
    }

    public static int getMinuteFree() {
        return MINUTE_FREE;
    }

    public static void reset() {
        if (MINUTE_FREE == 0 && SLEEP_TIME == 0) {
//            LogUtil.d(TAG_DORMANT, "reset MINUTE_FREE == 0 && SLEEP_TIME == 0");
            return;
        }
        LogUtil.d(TAG_DORMANT, "reset +++++++++++++++++++++++++");
        MINUTE_FREE = 0;
        SLEEP_TIME = 0;

    }

    public static void addMinute(Context context) {
        LogUtil.d(TAG_DORMANT, "addMinute start");

        if (ExoPlayerFactory.isPlaying()) {
            reset();
            LogUtil.d(TAG_DORMANT, "addMinute isPlaying = true");
            return;
        }
        if (MINUTE_FREE >= MINUTE_FREE_COUNT) {
            SLEEP_TIME++;
            LogUtil.d(TAG_DORMANT, "addMinute SLEEP_TIME = " + SLEEP_TIME);
            if (mMinuteChangeListener != null) {
                mMinuteChangeListener.onChange();
            }
            if (isCanShutdown()) {
                // todo 睡眠超时，关机或退出登录
                LogUtil.d(TAG_DORMANT, "addMinute 【关机】！！！睡眠超时，关机或退出登录");
                ToastUtil.show("【关机】！！！睡眠超时，关机或退出登录");
            }
            return;
        }

        MINUTE_FREE++;
        LogUtil.d(TAG_DORMANT, "addMinute MINUTE_FREE = " + MINUTE_FREE);
        if (mMinuteChangeListener != null) {
            mMinuteChangeListener.onChange();
        }
        if (isCanDormant()) {
            // todo 休眠或待机进入低功耗
            LogUtil.d(TAG_DORMANT, "addMinute 【休眠】！！！休眠或待机进入低功耗");
            DormantActivity.startActivity(context);
        }
    }

    public static boolean isCanDormant() {
        return MINUTE_FREE >= MINUTE_FREE_COUNT;
    }

    public static boolean isCanShutdown() {
        return SLEEP_TIME >= SLEEP_TIME_COUNT;
    }

    private static MinuteChangeListener mMinuteChangeListener;

    public static void setMinuteChangeListener(MinuteChangeListener minuteChangeListener) {
        mMinuteChangeListener = minuteChangeListener;
    }

    public interface MinuteChangeListener {
        void onChange();
    }
}