package com.thfw.base.models;

import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/13 14:37
 * Describe:Todo
 */
public class TalkModel implements IModel {

    /**
     * AI树洞
     */
    public static final int TYPE_AI = 0;

    /**
     * 主题对话
     */
    public static final int TYPE_THEME = 1;

    private int type;
    private String title;

    public TalkModel(int type) {
        this.type = type;
        switch (type) {
            case TYPE_AI:
                title = "AI树洞";
                break;
            default:
                title = "主题对话";
                break;
        }
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }
}
