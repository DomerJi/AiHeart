package com.thfw.robotheart.util;

import android.content.Context;

import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.activitys.set.DormantActivity;
import com.thfw.robotheart.robot.RobotUtil;

/**
 * 休眠控制
 */
public class Dormant {


    public static final String KEY_DORMANT_SWITCH = "key.dormant.switch";
    public static final String KEY_DORMANT_MINUTE = "key.dormant.minute";

    /**
     * 【N】分后无操作进入休眠
     */
    private static final int MINUTE_FREE_COUNT = 5;

    /**
     * 休眠后，【N分钟】未唤醒进入关机或退出登录
     */
    private static final int SLEEP_TIME_COUNT = 10;
    private static final String TAG_DORMANT = "DORMANT_TIME";
    private static final String TAG_RESET = "reset +++++++++++++++++++++++++";

    private static int MINUTE_FREE = 0;

    private static int SLEEP_TIME = 0;
    private static long lastAddTime;
    private static MinuteChangeListener mMinuteChangeListener;

    public static int getSleepTime() {
        return SLEEP_TIME;
    }

    public static int getMinuteFree() {
        return MINUTE_FREE;
    }

    public static void reset() {
        if (MINUTE_FREE == 0 && SLEEP_TIME == 0) {
            return;
        }
        LogUtil.d(TAG_DORMANT, TAG_RESET);
        MINUTE_FREE = 0;
        SLEEP_TIME = 0;

    }

    public static void addMinute(Context context) {

        LogUtil.d(TAG_DORMANT, "addMinute start");
        if (System.currentTimeMillis() - lastAddTime < 300) {
            // 原因多个广播回调所致
            LogUtil.d(TAG_DORMANT, "addMinute 间隔不足一分钟+++++++++++++++++++++++++");
            return;
        }
        lastAddTime = System.currentTimeMillis();
        boolean mDormantSwitch = SharePreferenceUtil.getBoolean(KEY_DORMANT_SWITCH, true);

        if (!mDormantSwitch) {
            LogUtil.d(TAG_DORMANT, "addMinute mDormantSwitch = " + mDormantSwitch);
            reset();
            return;
        }
        if (ExoPlayerFactory.isPlaying()) {
            reset();
            LogUtil.d(TAG_DORMANT, "addMinute isPlaying = true");
            return;
        }
        if (isCanDormant()) {
            SLEEP_TIME++;
            LogUtil.d(TAG_DORMANT, "addMinute SLEEP_TIME = " + SLEEP_TIME);
            if (mMinuteChangeListener != null) {
                mMinuteChangeListener.onChange();
            }
            if (isCanShutdown()) {
                // todo 睡眠超时，关机或退出登录
                LogUtil.d(TAG_DORMANT, "addMinute 【关机】！！！睡眠超时，关机或退出登录");
                if (RobotUtil.isInstallRobot()) {
                    RobotUtil.shutdown();
                    LogUtil.d(TAG_DORMANT, "isInstallRobot true 【关机】");
                }
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
        return MINUTE_FREE > SharePreferenceUtil.getInt(KEY_DORMANT_MINUTE, MINUTE_FREE_COUNT);
    }

    public static boolean isCanShutdown() {
        return SLEEP_TIME >= SLEEP_TIME_COUNT;
    }

    public static int getShutDownDuration() {
        return SLEEP_TIME_COUNT - SLEEP_TIME;
    }

    public static void setMinuteChangeListener(MinuteChangeListener minuteChangeListener) {
        mMinuteChangeListener = minuteChangeListener;
    }

    public interface MinuteChangeListener {
        void onChange();
    }
}