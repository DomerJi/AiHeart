package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/3/4 11:12
 * Describe:Todo
 */
public class MsgCountModel implements IModel {

    @SerializedName("task_new_msg_num")
    public int numTask;

    @SerializedName("system_new_msg_num")
    public int numSystem;

    @SerializedName("task_msg_list")
    public List<String> task_msg_list;

    @SerializedName("system_msg_list")
    public List<String> system_msg_list;

    @Override
    public String toString() {
        return "MsgCountModel{" +
                "numTask=" + numTask +
                ", numSystem=" + numSystem +
                '}';
    }
}
