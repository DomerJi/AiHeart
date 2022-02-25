package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/2/25 10:48
 * Describe:Todo
 */
public class VoiceInstructionModel implements IModel {


    /**
     * id : 1
     * content : 你好，请吩咐
     * remark : 唤醒后回应
     * device : 机器人
     */

    @SerializedName("id")
    private int id;
    @SerializedName("content")
    private String content;
    @SerializedName("remark")
    private String remark;
    @SerializedName("device")
    private String device;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
