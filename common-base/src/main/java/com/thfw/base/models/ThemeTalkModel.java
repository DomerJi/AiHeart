package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/13 14:31
 * Describe:Todo
 */
public class ThemeTalkModel implements IModel {

    public static final int TYPE_TOP = 1;
    public static final int TYPE_BODY = 0;

    @SerializedName("m_item_type")
    private int itemType;
    /**
     * id : 459
     * title : 新兵常见困扰
     * pic : http://resource.soulbuddy.cn/public/uploads/testing/210901044748612f3e3417ece.jpg
     * num : 108
     * collected : 0
     */

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("pic")
    private String pic;
    @SerializedName("num")
    private int num;
    @SerializedName("collected")
    private int collected;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

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
