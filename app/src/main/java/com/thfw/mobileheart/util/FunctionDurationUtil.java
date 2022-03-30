package com.thfw.mobileheart.util;

import android.app.Activity;

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
import com.thfw.mobileheart.activity.test.TestBeginActivity;
import com.thfw.mobileheart.activity.test.TestProgressIngActivity;
import com.thfw.mobileheart.activity.test.TestReportActivity;
import com.thfw.mobileheart.activity.test.TestingActivity;
import com.thfw.mobileheart.activity.video.VideoHomeActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;

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

    public static final int FUNCTION_APP = -1;
    public static final int FUNCTION_THEME_TALK = 0;
    public static final int FUNCTION_AI_TALK = 1;
    public static final int FUNCTION_TEST = 2;
    public static final int FUNCTION_AUDIO = 3;
    public static final int FUNCTION_VIDEO = 4;
    public static final int FUNCTION_TOOL = 5;
    public static final int FUNCTION_BOOK = 6;
    public static final int FUNCTION_IDEO_BOOK = 7;

    private static HashMap<Integer, List<String>> sparseArray;

    static {
        sparseArray = new HashMap<>();
        sparseArray.put(FUNCTION_THEME_TALK, Arrays.asList(ChatActivity.class.getCanonicalName()));
        sparseArray.put(FUNCTION_AI_TALK, Arrays.asList(ChatActivity.class.getCanonicalName()));
        sparseArray.put(FUNCTION_TEST, Arrays.asList(TestBeginActivity.class.getCanonicalName(),
                TestingActivity.class.getCanonicalName(), TestProgressIngActivity.class.getCanonicalName(),
                TestReportActivity.class.getCanonicalName()));

        sparseArray.put(FUNCTION_AUDIO, Arrays.asList(AudioHomeActivity.class.getCanonicalName(),
                AudioPlayerActivity.class.getCanonicalName()));
        sparseArray.put(FUNCTION_VIDEO, Arrays.asList(VideoHomeActivity.class.getCanonicalName(),
                VideoPlayActivity.class.getCanonicalName()));

        sparseArray.put(FUNCTION_TOOL, Arrays.asList(ExerciseActivity.class.getCanonicalName(),
                ExerciseIngActivity.class.getCanonicalName(), ExerciseDetailActivity.class.getCanonicalName()));

        sparseArray.put(FUNCTION_BOOK, Arrays.asList(ReadHomeActivity.class.getCanonicalName(),
                BookDetailActivity.class.getCanonicalName()));

        sparseArray.put(FUNCTION_IDEO_BOOK, Arrays.asList(StudyHomeActivity.class.getCanonicalName(),
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

    public static String getFunctionName(int type) {
        switch (type) {
            case FUNCTION_THEME_TALK:
                return "主题对话";
            case FUNCTION_AI_TALK:
                return "倾诉吐槽";
            case FUNCTION_AUDIO:
                return "正念冥想";
            case FUNCTION_VIDEO:
                return "视频集锦";
            case FUNCTION_TEST:
                return "测评问卷";
            case FUNCTION_TOOL:
                return "成长训练";
            case FUNCTION_BOOK:
                return "心理文章";
            case FUNCTION_IDEO_BOOK:
                return "思政文章";
            default:
                return "APP";
        }

    }

}
