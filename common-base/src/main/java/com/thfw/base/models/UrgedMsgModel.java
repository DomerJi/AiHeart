package com.thfw.base.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/7/12 16:40
 * Describe:Todo
 */
public class UrgedMsgModel implements IModel {


    /**
     * display : 0
     * id : 0
     */

    @SerializedName("display")
    private int display;
    @SerializedName("id")
    private int id;
    @SerializedName("content")
    private String content;

    public boolean isDisplay() {
        return display == 1;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return TextUtils.isEmpty(content) ? "您有一个未完成任务，请尽快在截止时间前完成哦~" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
