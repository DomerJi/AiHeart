package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class VideoEtcModel implements IModel {


    /**
     * id : 406
     * title : 抑郁症动画暖心小故事
     * pic : http://resource.soulbuddy.cn/public/uploads/videoPic/21011911060660064c9ed5ee7.jpg
     * num : 1000
     * collected : 0
     * have_seen : false
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
    @SerializedName("have_seen")
    private boolean haveSeen;

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

    public boolean isHaveSeen() {
        return haveSeen;
    }

    public void setHaveSeen(boolean haveSeen) {
        this.haveSeen = haveSeen;
    }
}
