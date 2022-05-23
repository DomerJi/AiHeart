package com.thfw.base.models;

import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/7/29 15:24
 * Describe:状态实体类
 */
public class StatusEntity implements IModel {

    public static final int TYPE_GROUP = 0;
    public static final int TYPE_BODY = 1;
    public static final int TYPE_TOP = 2;

    public int type;
    public String tag;
    public int bodyPosition = -1;
    public MoodModel moodModel;

    public StatusEntity setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public StatusEntity setType(int type) {
        this.type = type;

        return this;
    }

    public StatusEntity setMoodModel(MoodModel moodModel) {
        this.moodModel = moodModel;
        return this;
    }

    public void setBodyPosition(int bodyPosition) {
        this.bodyPosition = bodyPosition;
    }
}
