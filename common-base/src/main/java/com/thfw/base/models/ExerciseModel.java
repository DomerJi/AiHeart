package com.thfw.base.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.io.Serializable;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/8 15:33
 * Describe:Todo
 */
public class ExerciseModel implements IModel {


    /**
     * id : 1
     * title : 工具包1
     * pic : http://resource.soulbuddy.cn/public/uploads/videoPic/qtxsza.png
     * count : 10
     * collected : 0
     * history_count : 0
     */

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("pic")
    private String pic;
    @SerializedName("count")
    private int count;
    @SerializedName("collected")
    private int collected;
    @SerializedName("history_count")
    private int historyCount;
    @SerializedName("intr")
    private String desc;
    @SerializedName("link")
    private List<LinkModel> linkList;

    public List<LinkModel> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<LinkModel> linkList) {
        this.linkList = linkList;
    }

    public String getDesc() {
        return TextUtils.isEmpty(desc) ? "暂无" : desc;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }

    public int getHistoryCount() {
        return historyCount;
    }

    public void setHistoryCount(int historyCount) {
        this.historyCount = historyCount;
    }

    public static class LinkModel implements Serializable {

        /**
         * dialog_id : 2016
         * title : 测试---工具包1
         * status : 0
         */

        @SerializedName("dialog_id")
        private int dialogId;
        @SerializedName("title")
        private String title;
        @SerializedName("status")
        private int status;
        // used=true 弹框询问用户是否继续上一次训练
        @SerializedName("used")
        private boolean used;

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public int getDialogId() {
            return dialogId;
        }

        public void setDialogId(int dialogId) {
            this.dialogId = dialogId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
