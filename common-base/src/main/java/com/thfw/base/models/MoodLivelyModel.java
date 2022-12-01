package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author:pengs
 * Date: 2022/4/15 12:01
 * Describe:Todo
 */
public class MoodLivelyModel implements Serializable {

    /**
     * login_days : 1
     * continue_days : 1
     * today_active_time : 8836312
     * user_mood : {"path":"https://resource.soulbuddy.cn/public/uploads/tianhe/pic/yiyu.png","name":"抑郁\\憎恨","score":1.5,"tag":"负"}
     */

    @SerializedName("login_days")
    private int loginDays;
    @SerializedName("continue_days")
    private int continueDays;
    @SerializedName("today_active_time")
    private long todayActiveTime;
    @SerializedName("user_mood")
    private MoodModel userMood;

    public int getLoginDays() {
        return loginDays;
    }

    public void setLoginDays(int loginDays) {
        this.loginDays = loginDays;
    }

    public int getContinueDays() {
        return continueDays;
    }

    public void setContinueDays(int continueDays) {
        this.continueDays = continueDays;
    }

    public long getTodayActiveTime() {
        return todayActiveTime;
    }

    public void setTodayActiveTime(long todayActiveTime) {
        this.todayActiveTime = todayActiveTime;
    }

    public MoodModel getUserMood() {
        return userMood;
    }

    public void setUserMood(MoodModel userMood) {
        this.userMood = userMood;
    }
}
