package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.utils.FunctionType;

import java.io.Serializable;

/**
 * Author:pengs
 * Date: 2022/4/18 13:02
 * Describe:Todo
 */
public class MoodActiveModel implements Serializable {

    @SerializedName("time")
    public String time;
    @SerializedName("active_time")
    public long activeTime;
    @SerializedName("testing_time")
    public long testingTime;
    @SerializedName("article_time")
    public long articleTime;
    @SerializedName("music_time")
    public long musicTime;
    @SerializedName("video_time")
    public long videoTime;
    @SerializedName("topic_dialog_time")
    public long topicDialogTime;
    @SerializedName("ideology_article_time")
    public long ideologyArticleTime;
    @SerializedName("tool_package_time")
    public long toolPackageTime;
    @SerializedName("hole_dialog_time")
    public long holeDialogTime;
    @SerializedName("mood_value")
    public float moodValue;
    @SerializedName("mood_tag")
    public String moodName;
    @SerializedName("mood_pic")
    public String moodPic;
    /**
     * id : 5
     * user_id : 100010
     * time : 2022-04-18
     * active_time : 4727645
     * testing_time : 1537
     * article_time : 0
     * music_time : 0
     * video_time : 149104
     * topic_dialog_time : 21502
     * ideology_article_time : 59163
     * tool_package_time : 0
     * hole_dialog_time : 4992
     * mood_value : 10
     */


    @SerializedName("id")
    private long id;
    @SerializedName("user_id")
    private long userId;
    private boolean setTime;
    private String timeMd;
    private String mName;

    public String getTime() {
        return time;
    }

    public MoodActiveModel setTime(String time) {
        this.time = time;
        this.setTime = true;
        return this;
    }

    public boolean isSetTime() {
        return setTime;
    }

    public String getTimeMD() {
        if (timeMd == null) {
            if (time != null && time.length() == 10) {
                timeMd = time.substring(5, 10);
            } else {
                timeMd = "-";
            }
        }
        return timeMd;
    }

    public String getMoodPic() {
        return moodPic;
    }

    public String getMoodName() {
        if (mName == null) {
            if (moodName != null) {
                String[] words = moodName.split("\\\\");
                mName = words[0];
            } else {
                mName = "未打卡";
            }
        }
        return mName;
    }

    public String getOriginName() {
        return moodName;
    }


    public float getMoodValue() {
        return moodValue;
    }

    public long getActiveTime(int type) {

        switch (type) {
            case FunctionType.FUNCTION_THEME_TALK:
                return topicDialogTime;
            case FunctionType.FUNCTION_AI_TALK:
                return holeDialogTime;
            case FunctionType.FUNCTION_AUDIO:
                return musicTime;
            case FunctionType.FUNCTION_VIDEO:
                return videoTime;
            case FunctionType.FUNCTION_TEST:
                return testingTime;
            case FunctionType.FUNCTION_TOOL:
                return toolPackageTime;
            case FunctionType.FUNCTION_BOOK:
                return articleTime;
            case FunctionType.FUNCTION_IDEO_BOOK:
                return ideologyArticleTime;
            case FunctionType.FUNCTION_APP:
                return activeTime;
        }
        return 0;
    }

    public int getActiveTimeMinute(CharType type) {
        long time = getActiveTime(type);
        if (time == 0) {
            return 0;
        } else if (time < 60000) {
            return 1;
        } else {
            return (int) (time / 60000);
        }
    }

    public boolean isEmpty(CharType type) {
        if (type == CharType.MOOD) {
            return moodValue <= 0;
        }
        return getActiveTime(type) <= 0;
    }

    public long getActiveTime(CharType type) {

        switch (type) {
            case TALK:
                return topicDialogTime;
            case AI_TALK:
                return holeDialogTime;
            case AUDIO:
                return musicTime;
            case VIDEO:
                return videoTime;
            case TEST:
                return testingTime;
            case TOOL:
                return toolPackageTime;
            case BOOK:
                return articleTime;
            case IDEO_BOOK:
                return ideologyArticleTime;
            case APP:
                return activeTime;
        }
        return 0;
    }


    public MoodImp createType(CharType type) {
        return new MoodImp(type, this);
    }

    @Override
    public String toString() {
        return "MoodActiveModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", time='" + time + '\'' +
                ", timeMd='" + timeMd + '\'' +
                ", activeTime=" + activeTime +
                ", testingTime=" + testingTime +
                ", articleTime=" + articleTime +
                ", musicTime=" + musicTime +
                ", videoTime=" + videoTime +
                ", topicDialogTime=" + topicDialogTime +
                ", ideologyArticleTime=" + ideologyArticleTime +
                ", toolPackageTime=" + toolPackageTime +
                ", holeDialogTime=" + holeDialogTime +
                ", moodValue=" + moodValue +
                ", moodName='" + moodName + '\'' +
                ", moodPic='" + moodPic + '\'' +
                ", mName='" + mName + '\'' +
                '}';
    }

    public static class MoodImp {
        public CharType type;
        public MoodActiveModel model;

        public MoodImp(CharType type, MoodActiveModel model) {
            this.type = type;
            this.model = model;
        }
    }
}
