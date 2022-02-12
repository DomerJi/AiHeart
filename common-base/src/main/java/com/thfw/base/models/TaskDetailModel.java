package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.io.Serializable;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/2/10 15:07
 * Describe:Todo
 */
public class TaskDetailModel implements IModel {


    /**
     * task_type : 1
     * count : 3
     * finish : 0
     * deadline : 2022-05-24 10:40
     * title : 测评任务
     * content_list : [{"id":1905,"title":"社交恐惧心理测试","type":2,"status":0},{"id":1906,"title":"社交心理成熟度","type":2,"status":0},{"id":1907,"title":"自我防御方式问卷","type":5,"status":0}]
     */

    @SerializedName("task_type")
    private int taskType;
    @SerializedName("count")
    private int count;
    @SerializedName("finish")
    private int finish;
    @SerializedName("deadline")
    private String deadline;
    @SerializedName("title")
    private String title;
    @SerializedName("content_list")
    private List<ContentListBean> contentList;

    public int getTaskType() {
        return taskType;
    }

    public String getTaskTypeStr() {
        /**
         * 1-测评 2-音频 3-话术
         */
        switch (taskType) {
            case 1:
                return "测评问卷";
            case 2:
                return "正念冥想";
            case 3:
                return "主题对话";
        }
        return "未知";
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ContentListBean> getContentList() {
        return contentList;
    }

    public void setContentList(List<ContentListBean> contentList) {
        this.contentList = contentList;
    }

    public static class ContentListBean implements Serializable {
        /**
         * id : 1905
         * title : 社交恐惧心理测试
         * type : 2
         * status : 0
         */

        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("sfile")
        private String sfile;
        @SerializedName("type")
        private int type;
        @SerializedName("status")
        private int status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setSfile(String sfile) {
            this.sfile = sfile;
        }

        public String getSfile() {
            return sfile;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
