package com.thfw.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.IModel;
import com.thfw.base.IResult;


/**
 *
 */
public class HttpResult<T> implements IResult, IModel {

    private static final int CODE_SUCCESS = 1;
    public static final int FAIL_TOKEN = 5;

    /**
     * 返回状态值
     * 0表示成功
     */
    private int code;

    /**
     * 返回结果提示
     */
    private String msg;

    /**
     * 返回结果数据
     */
    private T data;
    @SerializedName("ext")
    private Ext ext;

    public T getData() {
        return data;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        switch (code) {
            case 0:
                return "请求失败";
            case 1:
                return "成功";
            case 2:
                return "验证码错误";
            case 3:
                return "密码错误";
            case 4:
                return "请输入密码或验证码";
            case 5:
                return "无效登录凭证";
            case 6:
                return "原密码错误";
            case 7:
                return "缺少关键参数";
            case 8:
                return "查找不到对应内容";
            case 9:
                return "对话-按钮已选择";
            case 10:
                return "该手机号已存在";

        }
        return msg;
    }

    @Override
    public boolean isSuccessful() {
        return code == CODE_SUCCESS;
    }

    public Ext getExt() {
        return ext;
    }

    public boolean isAchieve() {
        return ext != null && ext.achieve;
    }

    public class Ext {
        @SerializedName("turnPage")
        private String turnPage;
        // 进入深度对话， 咨询助理进入主题对话，进入主题对话
        @SerializedName("enter_deep_dialog")
        private boolean enterDeepDialog;
        // 表情
        @SerializedName("sentiment")
        private String sentiment;
        private boolean achieve;

        public String getSentiment() {
            return sentiment;
        }

        public boolean isSentiment() {
            return !TextUtils.isEmpty(sentiment);
        }

        public boolean isEnterDeepDialog() {
            return enterDeepDialog;
        }

        public boolean isJumpTree() {
            return "treeHolePage".equals(turnPage);
        }

        public boolean isJumpTheme() {
            return "proPage".equals(turnPage);
        }

        public boolean isAchieve() {
            return achieve;
        }
    }

}
