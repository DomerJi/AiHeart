package com.thfw.mobileheart.util;

import android.app.Activity;

import com.thfw.base.utils.FunctionType;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.activity.audio.AudioHomeActivity;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseDetailActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseIngActivity;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.activity.read.BookIdeoDetailActivity;
import com.thfw.mobileheart.activity.read.ReadHomeActivity;
import com.thfw.mobileheart.activity.read.StudyHomeActivity;
import com.thfw.mobileheart.activity.talk.ChatActivity;
import com.thfw.mobileheart.activity.talk.ThemeListActivity;
import com.thfw.mobileheart.activity.test.TestBeginActivity;
import com.thfw.mobileheart.activity.test.TestProgressIngActivity;
import com.thfw.mobileheart.activity.test.TestReportActivity;
import com.thfw.mobileheart.activity.test.TestingActivity;
import com.thfw.mobileheart.activity.video.VideoHomeActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.user.login.UserManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:pengs
 * Date: 2022/3/30 15:04
 * Describe:Todo
 */
public class FunctionDurationUtil {


    private static HashMap<Integer, List<String>> sparseArray;

    static {
        sparseArray = new HashMap<>();
        sparseArray.put(FunctionType.FUNCTION_THEME_TALK, Arrays.asList(ChatActivity.class.getCanonicalName(), ThemeListActivity.class.getCanonicalName()));
        sparseArray.put(FunctionType.FUNCTION_AI_TALK, Arrays.asList(ChatActivity.class.getCanonicalName()));
        sparseArray.put(FunctionType.FUNCTION_TEST, Arrays.asList(TestBeginActivity.class.getCanonicalName(),
                TestingActivity.class.getCanonicalName(), TestProgressIngActivity.class.getCanonicalName(),
                TestReportActivity.class.getCanonicalName()));

        sparseArray.put(FunctionType.FUNCTION_AUDIO, Arrays.asList(AudioHomeActivity.class.getCanonicalName(),
                AudioPlayerActivity.class.getCanonicalName()));
        sparseArray.put(FunctionType.FUNCTION_VIDEO, Arrays.asList(VideoHomeActivity.class.getCanonicalName(),
                VideoPlayActivity.class.getCanonicalName()));

        sparseArray.put(FunctionType.FUNCTION_TOOL, Arrays.asList(ExerciseActivity.class.getCanonicalName(),
                ExerciseIngActivity.class.getCanonicalName(), ExerciseDetailActivity.class.getCanonicalName()));

        sparseArray.put(FunctionType.FUNCTION_BOOK, Arrays.asList(ReadHomeActivity.class.getCanonicalName(),
                BookDetailActivity.class.getCanonicalName()));

        sparseArray.put(FunctionType.FUNCTION_IDEO_BOOK, Arrays.asList(StudyHomeActivity.class.getCanonicalName(),
                BookIdeoDetailActivity.class.getCanonicalName()));

    }

    public static int getFunction(Activity activity) {

        for (Map.Entry<Integer, List<String>> entry : sparseArray.entrySet()) {
            if (entry.getValue().contains(activity.getClass().getCanonicalName())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public static long getFunctionTime(int type) {
        return SharePreferenceUtil.getLong(ActivityLifeCycle.getKey(type), FunctionType.FUNCTION_APP == type ? MoodLivelyHelper.getTodayActiveTime() : 0);
    }

    public static synchronized void setFunctionTime(int type, long time) {
        SharePreferenceUtil.setLong(ActivityLifeCycle.getKey(type), time);
    }

    public static String getFunctionTimeHour(int type) {
        if (UserManager.getInstance().isTrueLogin()) {
            return String.valueOf(getFunctionTime(type) / (60 * 1000));
        }
        return "";
    }

    public static String getFunctionName(int type) {
        switch (type) {
            case FunctionType.FUNCTION_THEME_TALK:
                return "主题对话";
            case FunctionType.FUNCTION_AI_TALK:
                return "倾诉吐槽";
            case FunctionType.FUNCTION_AUDIO:
                return "正念冥想";
            case FunctionType.FUNCTION_VIDEO:
                return "视频集锦";
            case FunctionType.FUNCTION_TEST:
                return "测评问卷";
            case FunctionType.FUNCTION_TOOL:
                return "成长训练";
            case FunctionType.FUNCTION_BOOK:
                return "心理文章";
            case FunctionType.FUNCTION_IDEO_BOOK:
                return "思政文章";
            default:
                return "APP";
        }

    }

}
