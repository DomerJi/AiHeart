package com.thfw.base.models;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class VideoTypeModel {

    public String name;
    public int id;


    public VideoTypeModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public List<VideoTypeModel> list;
}
