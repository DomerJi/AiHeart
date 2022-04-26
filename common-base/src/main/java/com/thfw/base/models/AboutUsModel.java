package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/2/25 10:48
 * Describe:Todo
 */
public class AboutUsModel implements IModel {


    /**
     * content : 密友Ai深知个人信息对于用户的重要性。……
     * update_time : 2022-02-24 18:26:57
     */

    @SerializedName("url")
    private String companyUrl;
    @SerializedName("content")
    private String content;
    @SerializedName("update_time")
    private String updateTime;

    public String getCompanyUrl() {
        return companyUrl;
    }

    public String getContent() {
        return content;
    }
}
