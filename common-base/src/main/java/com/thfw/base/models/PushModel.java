package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.RegularUtil;

/**
 * Author:pengs
 * Date: 2022/2/28 14:46
 * Describe:Todo
 */
public class PushModel implements IModel {


    /**
     * user_id : 100010
     * time : 2022-03-07 15:24:01
     * title : 标题
     * msg_type : 2
     * content : 内容
     * read_status : 0
     * long_content : null
     * turn_page : testing
     * id : 1
     */

    @SerializedName("user_id")
    private int userId;
    @SerializedName("time")
    private String time;
    @SerializedName("title")
    private String title;
    @SerializedName("msg_type")
    private int msgType;
    @SerializedName("content")
    private String content;

    @SerializedName("task_type")
    private int taskType;

    @SerializedName("read_status")
    private int readStatus;
    @SerializedName("long_content")
    private Object longContent;
    @SerializedName("turn_page")
    private String turnPage;
    // 标记已读未读
    @SerializedName("id")
    private int id;
    // 内容id
    @SerializedName("content_id")
    private String contentId;
    // 友盟msgId
    @SerializedName("msg_id")
    private String msgId;

    public int getTaskType() {
        return taskType;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public Object getLongContent() {
        return longContent;
    }

    public void setLongContent(Object longContent) {
        this.longContent = longContent;
    }

    public String getTurnPage() {
        return turnPage;
    }

    public void setTurnPage(String turnPage) {
        this.turnPage = turnPage;
    }

    public int getContentId() {
        if (RegularUtil.isNumber(contentId)) {
            return Integer.parseInt(contentId);
        } else {
            return -1;
        }
    }

    public void setContentId(String id) {
        this.contentId = id;
    }
}
