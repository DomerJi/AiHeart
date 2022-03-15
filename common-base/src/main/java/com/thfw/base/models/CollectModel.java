package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/10 13:35
 * Describe:Todo
 */
public class CollectModel implements IModel {


    /**
     * id : 380
     * title : 军人心理应激自评问卷
     * collected : 1
     */

    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String title;
    @SerializedName("collected")
    public int collected;
    @SerializedName("add_time")
    public String duration;
    @SerializedName("type")
    public String type;


}
