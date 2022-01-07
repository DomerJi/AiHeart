package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class BookStudyTypeModel implements IModel {

    public String name;
    public int id = 0;

    @SerializedName("sort")
    public int sort;

    public BookStudyTypeModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public BookStudyTypeModel() {
    }

    @SerializedName("children")
    public List<BookStudyTypeModel> list;

    public List<BookStudyTypeModel> getList() {
        return list;
    }

}
