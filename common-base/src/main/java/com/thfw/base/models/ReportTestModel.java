package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/2/10 11:06
 * Describe:Todo
 */
public class ReportTestModel implements IModel {


    /**
     * id : 100
     * rid : 531
     * user_id : 100010
     * add_time : 2022-01-25 14:31:37
     * status : 1
     * spend_time : 23
     * valid : 0
     * title : 心理健康问卷
     */

    @SerializedName("id")
    private int id;
    @SerializedName("rid")
    private int rid;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("add_time")
    private String addTime;
    @SerializedName("status")
    private int status;
    @SerializedName("spend_time")
    private int spendTime;
    @SerializedName("valid")
    private int valid;
    @SerializedName("title")
    private String title;
    @SerializedName("hide")
    private int hide;

    public boolean isHide() {
        return hide == 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(int spendTime) {
        this.spendTime = spendTime;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
