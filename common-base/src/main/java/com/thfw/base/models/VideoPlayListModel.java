package com.thfw.base.models;

/**
 * Author:pengs
 * Date: 2021/10/14 11:45
 * Describe:Todo
 */
public class VideoPlayListModel {

    public static final int TYPE_TOP = 0;
    public static final int TYPE_ECT = 1;
    public static final int TYPE_GROUP = 2;
    public static final int TYPE_BODY = 3;

    public int type;

    public String headName;

    public VideoPlayListModel setHeadName(String headName) {
        this.headName = headName;
        return this;
    }

    public VideoPlayListModel(int type) {
        this.type = type;
    }

}
