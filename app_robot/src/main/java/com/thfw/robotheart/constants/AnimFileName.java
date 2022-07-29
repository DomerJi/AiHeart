package com.thfw.robotheart.constants;

import com.thfw.robotheart.port.ActionParams;

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
    public static final String EMOJI_GUANJI = "emoji_guanji.svga";

    /**
     * 人物形象
     */
    public static final String FACE_BG = "home_ip_bg.svga";
    public static final String FACE_FACE = "home_ip_face.svga";


    /**
     * 倾诉表情
     */
    public static final String TALK_BALING = "talk_baling.svga";
    public static final String TALK_FANZAO = "talk_fanzao.svga";
    public static final String TALK_JINZHANG = "talk_jinzhang.svga";
    public static final String TALK_MANZU = "talk_manzu.svga";
    public static final String TALK_MIMANG = "talk_mimang.svga";
    public static final String TALK_NANGUO = "talk_nanguo.svga";
    public static final String TALK_PINGJING = "talk_pingjing.svga";
    public static final String TALK_QINGSONG = "talk_qingsong.svga";
    public static final String TALK_SHENGQI = "talk_shengqi.svga";
    public static final String TALK_SHILIAN = "talk_shilian.svga";
    public static final String TALK_SHIMIAN = "talk_shimian.svga";
    public static final String TALK_WANGYIN = "talk_wangyin.svga";
    public static final String TALK_XINGFENG = "talk_xingfen.svga";
    public static final String TALK_YAOJINYAGUAN = "talk_yaojinyaguan.svga";
    public static final String TALK_YIYU = "talk_yiyu.svga";
    public static final String TALK_ZENGHENG = "talk_zengheng.svga";
    // 首页随机
    public static final int HOME_IP_ANIM_TIME = 4000;
    // 出场动画
    public static final String TRANSITION_WELCOM = "login_welcom.svga";
    private static HashMap<String, String> mNameHintMap;
    private static HashMap<String, String> mNameEmojiMap;

    /**
     * 网瘾、网赌、毒瘾、性瘾	上瘾容易戒瘾难	摇头	上瘾容易戒瘾难
     * 失恋	抱抱你		抱抱你
     * 你值得更好的		你值得更好的
     * 失眠	睡不好太烦躁！		睡不好太烦躁！
     * 焦虑	深呼吸 平常心哦		深呼吸 平常心哦
     * 校园霸凌、家暴、性侵	保护好自己哦		保护好自己哦
     * 兴奋/惊喜、自豪、开心/愉快	有什么好事分享一下呀	转动	有什么好事分享一下呀
     * 满足	咿呀咿呀哟	转动	咿呀咿呀哟
     * 轻松	呐，小心心给你	点头	呐，小心心给你
     * 平静、凑合	我很冷静~	点头	我很冷静~
     * 迷茫	思考ing		思考ing
     * 烦躁不安/紧张	深呼吸哦		深呼吸哦
     * 生气	别生气哦	摇头	别生气哦
     * 难过	抱抱你		抱抱你
     * 抑郁	会过去的		会过去的
     * 憎恨	这很难受吧	摇头	这很难受吧
     * 痛苦	咬紧牙关		咬紧牙关
     */
    private static void initEmojiMap() {
        if (mNameEmojiMap == null) {
            mNameEmojiMap = new HashMap<>();
            mNameEmojiMap.put("校园霸凌、家暴、性侵", TALK_BALING);
            mNameEmojiMap.put("烦躁不安/紧张", TALK_FANZAO);
            mNameEmojiMap.put("焦虑", TALK_JINZHANG);
            mNameEmojiMap.put("满足", TALK_MANZU);
            mNameEmojiMap.put("迷茫", TALK_MIMANG);
            mNameEmojiMap.put("失恋", TALK_SHILIAN);
            mNameEmojiMap.put("失眠", TALK_SHIMIAN);
            mNameEmojiMap.put("难过", TALK_NANGUO);
            mNameEmojiMap.put("平静、凑合", TALK_PINGJING);
            mNameEmojiMap.put("轻松", TALK_QINGSONG);
            mNameEmojiMap.put("生气", TALK_SHENGQI);
            mNameEmojiMap.put("网瘾、网赌、毒瘾、性瘾", TALK_WANGYIN);
            mNameEmojiMap.put("兴奋/惊喜、自豪、开心/愉快", TALK_XINGFENG);
            mNameEmojiMap.put("痛苦", TALK_YAOJINYAGUAN);
            mNameEmojiMap.put("抑郁", TALK_YIYU);
            mNameEmojiMap.put("憎恨", TALK_ZENGHENG);
        }
    }

    private static void initHintMap() {
        if (mNameHintMap == null) {
            mNameHintMap = new HashMap<>();
            mNameHintMap.put(TRANSITION_TEST, "小密准备了专业心理测试，\n帮你更好的了解当下的心态哦~");
            mNameHintMap.put(TRANSITION_THEME, "小密会按照专业心理咨询的步骤，\n帮你梳理情绪哦~");
            mNameHintMap.put(TRANSITION_TALK, "小密会倾听你的心事，跟我聊聊吧~");
            mNameHintMap.put(TRANSITION_VIDEO, "小密搜集了很多经典心理视频和满满正能量视频，\n一起观看吧~");
            mNameHintMap.put(TRANSITION_AUDIO, "静生百慧，小密准备了专业冥想课程，\n一起练习吧~");
            mNameHintMap.put(TRANSITION_BOOK, "学点心理学，惊艳所有人，\n一起学习吧~");
            mNameHintMap.put(TRANSITION_IDEO, "提升思想境界和认知维度，\n从根上解决心理问题~");
            mNameHintMap.put(TRANSITION_TOOL, "小密准备了专业心理训练工具，\n一起见证心灵的成长吧~");
//            mNameHintMap.put(EMOJI_GUANJI, "[关机]即将关机");
            mNameHintMap.put(EMOJI_GUANJI, "即将关机");
//            mNameHintMap.put(EMOJI_SHIWANG, "[失望]请重新登录哦");
            mNameHintMap.put(EMOJI_SHIWANG, "请重新登录哦");
//            mNameHintMap.put(EMOJI_CHUMO, "[害羞]您好");
            mNameHintMap.put(EMOJI_CHUMO, "您好");
//            mNameHintMap.put(EMOJI_XUANYUN, "[眩晕]请尽快把我放到固定位置哦");
            mNameHintMap.put(EMOJI_XUANYUN, "请尽快把我放到固定位置哦");
            mNameHintMap.put(EMOJI_WELCOM, "欢迎");
//            mNameHintMap.put(EMOJI_KAIJI, "[开机]你好，我是小密，给你最贴心的心理服务");
            mNameHintMap.put(EMOJI_KAIJI, "你好，我是小密，给你最贴心的心理服务");
        }
    }

    public static String getHint(String fileName) {
        if (mNameHintMap == null) {
            initHintMap();
        }
        return mNameHintMap.get(fileName);
    }

    public static String getTalkEmojiBySentiment(String sentiment) {
        if (mNameEmojiMap == null) {
            initEmojiMap();
        }
        for (String key : mNameEmojiMap.keySet()) {
            if (key.contains(sentiment)) {
                return mNameEmojiMap.get(key);
            }
        }
        return null;
    }

    public static RobotOutPutInfo getRobotOutPutInfo(String animFile) {
        if (mNameEmojiMap == null) {
            initEmojiMap();
        }
        switch (animFile) {
            case TALK_WANGYIN: {
                return new RobotOutPutInfo("上瘾容易戒瘾难", ActionParams.getNormalNod());
            }
            case TALK_XINGFENG: {
                return new RobotOutPutInfo("有什么好事分享一下呀", ActionParams.getNormalRotate());
            }
            case TALK_MANZU: {
                return new RobotOutPutInfo("咿呀咿呀哟", ActionParams.getNormalRotate());
            }
            case TALK_QINGSONG: {
                return new RobotOutPutInfo("呐，小心心给你", ActionParams.getNormalNod());
            }
            case TALK_PINGJING: {
                return new RobotOutPutInfo("我很冷静~", ActionParams.getNormalNod());
            }
            case TALK_SHENGQI: {
                return new RobotOutPutInfo("别生气哦~", ActionParams.getNormalShake());
            }
            case TALK_ZENGHENG: {
                return new RobotOutPutInfo("这很难受吧", ActionParams.getNormalShake());
            }
            case TALK_SHILIAN: {
                return new RobotOutPutInfo("抱抱你你值得更好的", null);
            }
            case TALK_SHIMIAN: {
                return new RobotOutPutInfo("睡不好太烦躁！", null);
            }
            case TALK_JINZHANG: {
                return new RobotOutPutInfo("深呼吸 平常心哦", null);
            }
            case TALK_BALING: {
                return new RobotOutPutInfo("保护好自己哦", null);
            }
            case TALK_MIMANG: {
                return new RobotOutPutInfo("思考ing", null);
            }
            case TALK_FANZAO: {
                return new RobotOutPutInfo("深呼吸哦", null);
            }
            case TALK_NANGUO: {
                return new RobotOutPutInfo("抱抱你", null);
            }
            case TALK_YIYU: {
                return new RobotOutPutInfo("会过去的", null);
            }
            default:
                return null;
        }
    }

    public static class RobotOutPutInfo {
        public String tts;
        public ActionParams actionParams;

        public RobotOutPutInfo(String tts, ActionParams actionParams) {
            this.tts = tts;
            this.actionParams = actionParams;
        }

        @Override
        public String toString() {
            return "RobotOutPutInfo{" +
                    "tts='" + tts + '\'' +
                    ", actionParams=" + (actionParams != null ? actionParams.toString() : "null") +
                    '}';
        }
    }

    /**
     * 动画出场时间/频率
     */
    public static class Frequency {
        public static final String KEY_FREQUENCY = "key.frequency";
        public static final int EVERY_TIME = 0;
        public static final int DAY_TIME = 1;
        public static final int WEEK_TIME = 2;
    }
}
