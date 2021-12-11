package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/8 11:41
 * Describe:Todo
 */
public class TestResultModel implements IModel {
    @SerializedName("result_id")
    private int resultId;

    public int getResultId() {
        return resultId;
    }
}
