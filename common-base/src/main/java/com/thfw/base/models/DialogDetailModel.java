package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/23 16:51
 * Describe:Todo
 */
public class DialogDetailModel implements IModel {


    /**
     * id : 432
     * title : 新兵适应困难
     * pic : http://resource.soulbuddy.cn/public/uploads/testing/210901044839612f3e67c6099.webp
     * intr :
     * num : 118
     * collected : 0
     */

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("pic")
    private String pic;
    @SerializedName("intr")
    private String intr;
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

    public String getIntr() {
        return intr;
    }

    public void setIntr(String intr) {
        this.intr = intr;
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
