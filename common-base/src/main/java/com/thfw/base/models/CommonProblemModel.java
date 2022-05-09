package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/2/25 10:48
 * Describe:Todo
 */
public class CommonProblemModel implements IModel {


    /**
     * id : 1
     * question : 如何修改个人昵称？
     * answer : 在首页点击【我的】，进入页面后点击【个人信息】，可找到【昵称】进行修改。
     * create_time : 2022-02-24 17:10:02
     * device : robot,phone,pad
     */

    @SerializedName("id")
    private int id;
    @SerializedName("question")
    private String question;
    @SerializedName("answer")
    private String answer;
    @SerializedName("answer_url")
    private String answerUrl;
    @SerializedName("create_time")
    private String createTime;
    @SerializedName("device")
    private String device;

    public String getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
