package com.thfw.base.models;

import com.thfw.base.base.IModel;
import com.thfw.base.utils.HourUtil;

public class ChatEntity implements IModel {


    public static final int TYPE_TO = 1; // 1：用户输入
    public static final int TYPE_FROM_NORMAL = 2; // 2：机器人正常对话回复
    public static final int TYPE_FROM_SELECT = 3; // 单选题
    public static final int TYPE_INPUT = 4; // 4：自由输入题目
    public static final int TYPE_RECOMMEND_TEST = 5; // 测评推荐
    public static final int TYPE_RECOMMEND_VIDEO = 6;// 视频推荐
    public static final int TYPE_RECOMMEND_TEXT = 7; // 文章推荐
    public static final int TYPE_RECOMMEND_AUDIO = 8; // 音频推荐
    public static final int TYPE_RECOMMEND_AUDIO_ETC = 9; // 音频合集推荐
    public static final int TYPE_END_SERVICE = 13;
    public static final int TYPE_FEEDBACK = 14;

    public static final int TYPE_SELECT = 15;
    public static final int TYPE_TIME = 16;

    public int type;
    public String talk;
    public long time = System.currentTimeMillis();

    public int getType() {
        return type;
    }

    public String getTalk() {
        return talk == null ? "" : talk;
    }

    public boolean isFrom() {
        return type == TYPE_FROM_NORMAL || type == TYPE_FROM_SELECT || type == TYPE_INPUT;
    }

    private DialogTalkModel talkModel;


    public DialogTalkModel getTalkModel() {
        return talkModel;
    }

    public ChatEntity() {

    }

    public ChatEntity(DialogTalkModel dialogTalkModel) {
        this.talkModel = dialogTalkModel;
        this.type = dialogTalkModel.getType();
        this.talk = dialogTalkModel.getQuestion();
        this.time = System.currentTimeMillis();
    }

    public ChatEntity(int type, DialogTalkModel dialogTalkModel) {
        this.talkModel = dialogTalkModel;
        this.type = type;
        this.talk = dialogTalkModel.getQuestion();
        this.time = System.currentTimeMillis();
    }


    public ChatEntity(int type, String talk) {
        this.type = type;
        this.talk = talk;
        this.time = System.currentTimeMillis();
    }


    public static ChatEntity createTime() {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.type = ChatEntity.TYPE_TIME;
        chatEntity.time = System.currentTimeMillis();
        chatEntity.talk = HourUtil.getYYMMDD_HHMMSS(chatEntity.time);
        return chatEntity;
    }

    public String getRecommendType() {
        switch (type) {
            case ChatEntity.TYPE_RECOMMEND_TEST: // 测评
                return "测评问卷";
            case ChatEntity.TYPE_RECOMMEND_VIDEO: // 视频
                return "视频";
            case ChatEntity.TYPE_RECOMMEND_TEXT: // 文章
                return "文章";
            case ChatEntity.TYPE_RECOMMEND_AUDIO: // 音频
            case ChatEntity.TYPE_RECOMMEND_AUDIO_ETC:// 音频合集
                return "正念冥想";
            default:
                return "";
        }
    }
}
