package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/1/13 10:50
 * Describe:Todo
 */
public class ChosenModel implements IModel {
    @SerializedName("chosen")
    public String chosen;

}
