package com.thfw.base.models;

import com.thfw.base.api.TalkApi;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/13 14:37
 * Describe:Todo
 */
public class TalkModel implements IModel {

    /**
     * 倾诉吐槽
     */
    public static final int TYPE_AI = TalkApi.JOIN_TYPE_AI;

    /**
     * 咨询助理
     */
    public static final int TYPE_THEME = TalkApi.JOIN_TYPE_GUIDANCE;

    /**
     * 主题对话
     */
    public static final int TYPE_SPEECH_CRAFT = TalkApi.JOIN_TYPE_SPEECH_CRAFT;

    private int type;
    private String title;
    private int id;
    private boolean collected;

    public TalkModel(int type) {
        this.type = type;
        switch (type) {
            case TYPE_AI:
                title = "倾诉吐槽";
                break;
            case TYPE_THEME:
                title = "咨询助理";
                break;
            default:
                title = "主题对话";
                break;
        }
    }

    public String getTitle() {
        return title;
    }

    public TalkModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getId() {
        return id;
    }

    public TalkModel setId(int id) {
        this.id = id;
        return this;
    }

    public boolean isCollected() {
        return collected;
    }

    public TalkModel setCollected(boolean collected) {
        this.collected = collected;
        return this;
    }

    public int getType() {
        return type;
    }
}
