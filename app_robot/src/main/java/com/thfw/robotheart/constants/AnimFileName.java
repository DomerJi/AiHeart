package com.thfw.robotheart.constants;

import java.util.HashMap;

/**
 * Author:pengs
 * Date: 2022/2/24 11:51
 * Describe:Todo
 */
public class AnimFileName {

    private static HashMap<String, String> mNameHintMap;

    // 出场动画
    public static String TRANSITION_WELCOM = "transition_welcom.svga";

    // 过场动画
    public static final String TRANSITION_AUDIO = "transition_audio.svga";
    public static final String TRANSITION_BOOK = "transition_book.svga";
    public static final String TRANSITION_IDEO = "transition_ideo.svga";
    public static final String TRANSITION_TALK = "transition_talk.svga";
    public static final String TRANSITION_TEST = "transition_test.svga";
    public static final String TRANSITION_THEME = "transition_theme.svga";
    public static final String TRANSITION_TOOL = "transition_tool.svga";
    public static final String TRANSITION_VIDEO = "transition_video.svga";


    // 测试 svga
    public static final String TEST_ANGEL = "angel.svga";
    public static final String TEST_ROSE = "rose.svga";

    static {
        mNameHintMap = new HashMap<>();
        mNameHintMap.put(TRANSITION_TEST, "小密准备了专业心理测试，\n帮你更好的了解当下的心态哦~");
        mNameHintMap.put(TRANSITION_THEME, "小密会按照专业心理咨询的步骤，\n帮你梳理情绪哦~");
        mNameHintMap.put(TRANSITION_TALK, "小密会倾听你的心事，跟我聊聊吧~");
        mNameHintMap.put(TRANSITION_VIDEO, "小密搜集了很多经典心理视频\n和满满正能量视频，\n一起观看吧~");
        mNameHintMap.put(TRANSITION_AUDIO, "静生百慧，小密准备了专业冥想课程，\n一起练习吧~");
        mNameHintMap.put(TRANSITION_BOOK, "学点心理学，惊艳所有人，\n一起学习吧~");
        mNameHintMap.put(TRANSITION_IDEO, "提升思想境界和认知维度，\n从根上解决心理问题~");
        mNameHintMap.put(TRANSITION_TOOL, "小密准备了专业心理训练工具，\n一起见证心灵的成长吧~");
    }

    public static String getHint(String fileName) {
        return mNameHintMap.get(fileName);
    }
}
