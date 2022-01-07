package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class VideoTypeModel implements IModel {

    public String name;
    public int id = 0;

    @SerializedName("root_type")
    public int rootType = -1;

    public VideoTypeModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @SerializedName("children")
    public List<VideoTypeModel> list;

    public List<VideoTypeModel> getList() {
        return list;
    }
}
