package com.thfw.mobileheart.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.SparseLongArray;

import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * 应用内统计功能使用时长
 */
public class ActivityLifeCycle implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "ActivityLifeCycle_jsp";
    /**
     * 上次检查时间，用于在运行时作为基准获取用户时间
     */
    public static long lastCheckTime = 0;
    /**
     * 前台Activity数量
     **/
    private int foregroundActivityCount = 0;
    /**
     * Activity是否在修改配置，
     */
    private boolean isChangingConfigActivity = false;
    /**
     * 应用将要切换到前台
     */
    private boolean willSwitchToForeground = false;
    /**
     * 当前是否在前台
     */
    private boolean isForegroundNow = false;
    /**
     * 上次暂停的Activity信息
     */
    private String lastPausedActivityName;
    private int lastPausedActivityHashCode;
    private long lastPausedTime;
    private long appUseReduceTime = 0;
    /**
     * 每次有Activity启动时的开始时间点
     */
    private long appStartTime = 0L;

    /**
     * 本次统计时，App运行的时间
     */
    private long runTimeThisDay = 0L;

    private SparseLongArray mFunctionTime = new SparseLongArray();

    /**
     * 获取今日0点的时间点，/1000*1000保证每次取值相同。
     *
     * @return
     */
    public static long getTodayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long time = calendar.getTimeInMillis() / 1000 * 1000;
        LogUtil.d(TAG, "getTodayStartTime -> " + HourUtil.getYYMMDD_HHMMSS(time));
        LogUtil.d(TAG, "getYesterdayStartTime -> " + HourUtil.getYYMMDD_HHMMSS(time - HourUtil.LEN_DAY));
        return time;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        LogUtil.d(TAG, "onActivityCreated -> " + getActivityName(activity));
    }

    @Override
    public void onActivityStarted(Activity activity) {

        LogUtil.d(TAG, "onActivityStarted -> " + getActivityName(activity) + " " + foregroundActivityCount);
        //前台没有Activity，说明新启动或者将从从后台恢复
        if (foregroundActivityCount == 0 || !isForegroundNow) {
            willSwitchToForeground = true;
        } else {
            //应用已经在前台，此时保存今日运行的时间。
            runTimeThisDay = System.currentTimeMillis() - appStartTime;
            lastCheckTime = System.currentTimeMillis();
            saveTodayPlayTime(runTimeThisDay);
        }
        appStartTime = System.currentTimeMillis();
        if (isChangingConfigActivity) {
            isChangingConfigActivity = false;
            return;
        }

        foregroundActivityCount += 1;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogUtil.d(TAG, "onActivityResumed -> " + getActivityName(activity));
        // 在这里更新检查时间点，是为了保证从后台恢复到前台，持续计时的准确性。
        lastCheckTime = System.currentTimeMillis();
        addAppUseReduceTimeIfNeeded(activity);
        if (willSwitchToForeground && isInteractive(activity)) {
            isForegroundNow = true;
            LogUtil.d("switch to foreground");
        }
        if (isForegroundNow) {
            willSwitchToForeground = false;
        }
        activityResumeOrPaused(activity, true);
    }

    private void activityResumeOrPaused(Activity activity, boolean resume) {
        int function = FunctionDurationUtil.getFunction(activity);
        if (function == -1) {
            return;
        }
        LogUtil.d(TAG, "function = " + function + " ; resume[" + resume + "] -> " + getActivityName(activity));
        if (resume) {
            mFunctionTime.put(function, System.currentTimeMillis());
        } else {
            if (mFunctionTime.indexOfKey(function) == -1) {
                return;
            }
            long functionTime = System.currentTimeMillis() - mFunctionTime.get(function, -1);
            LogUtil.d(TAG, "activityResumeOrPaused -> functionTime" + functionTime);
            if (functionTime > 0) {
                saveTodayPlayTime(function, functionTime);
            }
        }

    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogUtil.d(TAG, "onActivityPaused -> " + getActivityName(activity));
        lastPausedActivityName = getActivityName(activity);
        lastPausedActivityHashCode = activity.hashCode();
        lastPausedTime = System.currentTimeMillis();
        activityResumeOrPaused(activity, false);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //如果这个Activity实在修改配置，如旋转等，则不保存时间直接返回
        if (activity.isChangingConfigurations()) {
            isChangingConfigActivity = true;
            return;
        }
        foregroundActivityCount -= 1;
        LogUtil.d(TAG, "onActivityStopped -> " + getActivityName(activity) + " " + foregroundActivityCount);
        addAppUseReduceTimeIfNeeded(activity);
        //该Activity要进入后台，前台Activity数量-1。

        //当前已经是最后的一个Activity，代表此时应用退出了，保存时间。
        // 如果跨天了，则从新一天的0点开始计时
        if (foregroundActivityCount == 0) {
            saveAppUseTime();
            isForegroundNow = false;
        }
    }

    public boolean isForegroundNow() {
        return isForegroundNow;
    }

    public void saveAppUseTime() {
        LogUtil.d(TAG, "switch to background (reduce time[" + appUseReduceTime + "])");
        if (getTodayStartTime() > appStartTime) {
            runTimeThisDay = System.currentTimeMillis() - getTodayStartTime();
        } else {
            runTimeThisDay = System.currentTimeMillis() - appStartTime;
        }
        appStartTime = System.currentTimeMillis();
        saveTodayPlayTime(runTimeThisDay);
        lastCheckTime = System.currentTimeMillis();
        LogUtil.d(TAG, "run time  :" + runTimeThisDay);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        LogUtil.d(TAG, "onActivitySaveInstanceState -> " + getActivityName(activity));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtil.d(TAG, "onActivityDestroyed -> " + getActivityName(activity));
    }

    private void addAppUseReduceTimeIfNeeded(Activity activity) {
        if (getActivityName(activity).equals(lastPausedActivityName) && activity.hashCode() == lastPausedActivityHashCode) {
            long now = System.currentTimeMillis();
            if (now - lastPausedTime > 1000) {
                appUseReduceTime += now - lastPausedTime;
            }
        }
        lastPausedActivityHashCode = -1;
        lastPausedActivityName = null;
        lastPausedTime = 0;
    }

    private boolean isInteractive(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    private String getActivityName(final Activity activity) {
        return activity.getClass().getCanonicalName();
    }

    private void saveTodayPlayTime(long time) {
        saveTodayPlayTime(FunctionDurationUtil.FUNCTION_APP, time);
    }

    /**
     * 保存今日运行时间，以今日0点的时间作为Key值。
     * todo 上传时长和活跃天数，上传策略机制，及保证不重复上传。
     * todo 及时上传（实时上传可能性不大，需考虑误差多久）
     *
     * @param time
     */
    private void saveTodayPlayTime(int type, long time) {
        String key = getTodayStartTime() + "_" + type;
        long todayTime = SharePreferenceUtil.getLong(key, 0);

        LogUtil.d(TAG, "today :" + key + " : time : " + todayTime);
        LogUtil.d(TAG, "today totalTime:" + time + " :" + (todayTime + time));
        SharePreferenceUtil.setLong(key, todayTime + time);
        String yesterdayKey = (getTodayStartTime() - HourUtil.LEN_DAY) + "_" + type;
        long yesterdayTime = SharePreferenceUtil.getLong(yesterdayKey, 0);
        LogUtil.d(TAG, "yesterdayKey :" + yesterdayKey + " ->  key :" + key);

        LogUtil.d(TAG, "【" + FunctionDurationUtil.getFunctionName(type) + "】今日累计时长:"
                + HourUtil.getLimitTimeALl(SharePreferenceUtil.getLong(key, 0)));
        //清除前一天的值
        if (yesterdayTime > 0) {
            SharePreferenceUtil.removeKey(yesterdayKey);
        }
    }
}