package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.base.MsgType;

/**
 * Author:pengs
 * Date: 2022/3/7 11:35
 * Describe:Todo
 */
public class PushMsgModel implements IModel {


    /**
     * @link MsgType
     */
    @SerializedName("type")
    private int type;

    /**
     * 内容上新 id
     */
    @SerializedName("id")
    private int id;

    /**
     * 通知标题
     */
    @SerializedName("title")
    private String title;

    /**
     * 通知简介
     */
    @SerializedName("content")
    private String content;

    /**
     * 对应type字段
     * <p>
     * msg_type为7的时候
     * turn_page里面直接放H5的链接
     */
    @SerializedName("turn_page")
    private String turnPage;

    /**
     * 消息类别
     */
    @SerializedName("display_type")
    private String displayType;

    @SerializedName("msg_id")
    private String msgId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTurnPage() {
        return turnPage;
    }

    public void setTurnPage(String turnPage) {
        this.turnPage = turnPage;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTicker() {
        switch (type) {
            case MsgType.TASK:
                return "您有新的任务消息，待查收";
            case MsgType.TESTING:
                return "[测评问卷]内容上新，待查收";
            case MsgType.TOOL_PACKAGE:
                return "[成长训练]内容上新，待查收";
            case MsgType.TOPIC_DIALOG:
                return "[主题对话]内容上新，待查收";
            case MsgType.MUSIC:
                return "[正念冥想]内容上新，待查收";
            case MsgType.VIDEO:
                return "[视频集锦]内容上新，待查收";
            case MsgType.BOOK:
                return "[心理文库]内容上新，待查收";
            case MsgType.IDEO_BOOK:
                return "[思政文库]内容上新，待查收";
            case MsgType.H5:
            case MsgType.SYSTEM:
            case MsgType.COMMON_PROBLEM:
            case MsgType.VOICE_COMMAND:
            case MsgType.ABOUT_US:
                return "您有一条新消息，待查收";
            default:
                return "您有一条新消息，待查收";
        }
    }


    public PushModel toPushModel() {
        PushModel pushModel = new PushModel();
        pushModel.setMsgId(msgId);
        pushModel.setContent(String.valueOf(id));
        pushModel.setMsgType(type);
        pushModel.setTitle(title);
        pushModel.setContent(title);
        return pushModel;
    }
}
