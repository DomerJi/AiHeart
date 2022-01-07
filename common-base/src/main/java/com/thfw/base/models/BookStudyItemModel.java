package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/9 14:34
 * Describe:Todo
 */
public class BookStudyItemModel implements IModel {


    /**
     * id : 1
     * title : 测试文章
     * pic : http://resource.soulbuddy.cn/public/images/gamble.png
     * sort : 0
     * type : 1
     * num : 1
     * collected : 0
     */

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("pic")
    private String pic;
    @SerializedName("sort")
    private int sort;
    @SerializedName("type")
    private int type;
    @SerializedName("num")
    private int num;
    @SerializedName("collected")
    private int collected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }
}
