package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/4/21 16:19
 * Describe:Todo
 */
public class FeedBackModel implements IModel {


    /**
     * id : 1
     * user_id : 100010
     * content : 16630007656
     * pic : https://resource.soulbuddy.cn/public/uploads/tianhe/pic/220421044912feedback_pic.jpeg
     * add_time : 2022-04-21 16:49:12
     * status : 1
     * mobile : 16630007656
     */

    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("content")
    private String content;
    @SerializedName("pic")
    private String pic;
    @SerializedName("add_time")
    private String addTime;
    @SerializedName("status")
    private int status;
    @SerializedName("mobile")
    private String mobile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
