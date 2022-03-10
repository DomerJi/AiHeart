package com.thfw.base.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/3/7 17:22
 * Describe:Todo
 */
public class SystemDetailModel implements IModel {


    /**
     * id : 147
     * msg_type : 12
     * user_id : 100010
     * time : 2022-03-10 11:42:49
     * msg_id : uatf1kg164688376988701
     * title : 3-10系统消息
     * content : 123
     * read_status : 1
     * long_content : null
     * content_id : 0
     * turn_page :
     */

    @SerializedName("id")
    private int id;
    @SerializedName("msg_type")
    private int msgType;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("time")
    private String time;
    @SerializedName("msg_id")
    private String msgId;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("read_status")
    private int readStatus;
    @SerializedName("long_content")
    private String longContent;
    @SerializedName("content_id")
    private String contentId;
    @SerializedName("turn_page")
    private String turnPage;

    public int getId() {
        return id;
    }

    public int getMsgType() {
        return msgType;
    }

    public int getUserId() {
        return userId;
    }

    public String getTime() {
        return time;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public String getLongContent() {
        return TextUtils.isEmpty(longContent) ? "内容跑丢了" : longContent;
    }

    public String getContentId() {
        return contentId;
    }

    public String getTurnPage() {
        return turnPage;
    }
}
