package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author:pengs
 * Date: 2022/4/15 10:24
 * Describe:Todo
 */
public class MoodModel implements Serializable {

    /**
     * id : 1
     * path : https://resource.soulbuddy.cn/public/uploads/tianhe/pic/jingxi.png
     * score : 10
     * tag : 正
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    private String mName;
    @SerializedName("path")
    private String path;
    @SerializedName("score")
    private float score;
    @SerializedName("tag")
    private String tag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if (mName == null) {
            String[] words = name.split("\\\\");
            mName = words[0];
        }
        return mName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public float getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
