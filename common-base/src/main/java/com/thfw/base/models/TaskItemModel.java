package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/2/10 15:07
 * Describe:Todo
 */
public class TaskItemModel implements IModel {


    /**
     * id : 4
     * user_id : 100010
     * status : 0
     * deadline : 2022-05-24 10:40
     * title : 测评任务
     * count : 3
     * finish_count : 0
     */

    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("status")
    private int status;
    @SerializedName("task_type")
    private int taskType;
    @SerializedName("deadline")
    private String deadline;
    @SerializedName("finish_time")
    private String finishTime;
    @SerializedName("create_time")
    private String createTime;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("title")
    private String title;
    @SerializedName("count")
    private int count;
    @SerializedName("finish_count")
    private int finishCount;
    // start ================== 任务消息参数
    @SerializedName("task_id")
    private int taskId;
    @SerializedName("read_status")
    private int readStatus;
    @SerializedName("turn_page")
    private String turnPage;

    public String getCreateTime() {
        return createTime;
    }

    public String getStartTime() {
        return startTime;
    }
    // end ================== 任务消息参数

    public int getTaskId() {
        return taskId;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public String getTurnPage() {
        return turnPage;
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

    public int getTaskType() {
        return taskType;
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

    /**
     * 0-未完成 1-已完成 2-过期 3-已作废
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getVisibleTime(int type) {
        return type == 1 ? finishTime : deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFinishCount() {
        return finishCount;
    }

    public void setFinishCount(int finishCount) {
        this.finishCount = finishCount;
    }
}
