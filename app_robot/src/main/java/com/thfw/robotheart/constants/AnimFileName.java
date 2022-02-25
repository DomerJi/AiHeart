package com.thfw.robotheart.constants;

import java.util.HashMap;

/**
 * Author:pengs
 * Date: 2022/2/24 11:51
 * Describe:Todo
 */
public class AnimFileName {

    // 过场动画
    public static final String TRANSITION_AUDIO = "transition_audio.svga";
    public static final String TRANSITION_BOOK = "transition_book.svga";
    public static final String TRANSITION_IDEO = "transition_ideo.svga";
    public static final String TRANSITION_TALK = "transition_talk.svga";
    public static final String TRANSITION_TEST = "transition_test.svga";
    public static final String TRANSITION_THEME = "transition_theme.svga";
    public static final String TRANSITION_TOOL = "transition_tool.svga";
    public static final String TRANSITION_VIDEO = "transition_video.svga";

    /**
     * 失望表情	登录失败
     * 休眠表情	长时间无操作
     * 触摸表情	用户触摸机器人
     * 倾听表情	用户语音讲话
     * 打招呼表情	开机（你好，我是小密，给你最贴心的心理服务
     * 眩晕表情	离开充电底座或桌面
     * 关机表情	关机
     * 讲话表情	主题对话、吐槽等模块应用
     * 唤醒表情	待机状态下，用户点击屏幕
     * 欢迎表情	登录成功
     */
    public static final String EMOJI_CHUMO = "emoji_chumo.svga";
    public static final String EMOJI_HUANXING = "emoji_huanxing.svga";
    public static final String EMOJI_KAIJI = "emoji_kaiji.svga";
    public static final String EMOJI_QINGTING = "emoji_qingting.svga";
    public static final String EMOJI_SHIWANG = "emoji_shiwang.svga";
    public static final String EMOJI_SPEECH = "emoji_speech.svga";
    public static final String EMOJI_WELCOM = "emoji_welcom.svga";
    public static final String EMOJI_XIUMIAN = "emoji_xiumian.svga";
    public static final String EMOJI_XUANYUN = "emoji_xuanyun.svga";


    // 测试 svga
    public static final String TEST_ANGEL = "angel.svga";
    public static final String TEST_ROSE = "rose.svga";
    // 出场动画
    public static String TRANSITION_WELCOM = "transition_welcom.svga";
    private static HashMap<String, String> mNameHintMap;

    static {
        mNameHintMap = new HashMap<>();
        mNameHintMap.put(TRANSITION_TEST, "小密准备了专业心理测试，\n帮你更好的了解当下的心态哦~");
        mNameHintMap.put(TRANSITION_THEME, "小密会按照专业心理咨询的步骤，\n帮你梳理情绪哦~");
        mNameHintMap.put(TRANSITION_TALK, "小密会倾听你的心事，跟我聊聊吧~");
        mNameHintMap.put(TRANSITION_VIDEO, "小密搜集了很多经典心理视频和满满正能量视频，\n一起观看吧~");
        mNameHintMap.put(TRANSITION_AUDIO, "静生百慧，小密准备了专业冥想课程，\n一起练习吧~");
        mNameHintMap.put(TRANSITION_BOOK, "学点心理学，惊艳所有人，\n一起学习吧~");
        mNameHintMap.put(TRANSITION_IDEO, "提升思想境界和认知维度，\n从根上解决心理问题~");
        mNameHintMap.put(TRANSITION_TOOL, "小密准备了专业心理训练工具，\n一起见证心灵的成长吧~");
    }

    public static String getHint(String fileName) {
        return mNameHintMap.get(fileName);
    }
}
