package com.thfw.base.net;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.base.IResult;

/**
 *
 */
public class HttpResult<T> implements IResult, IModel {


    /**
     * 0 => "请求失败"
     * 1 => "成功"
     * 2 => "验证码错误"
     * 3 => "密码错误"
     * 4 => "请输入密码或验证码"
     * 5 => "无效登录凭证"
     * 6 => "原密码错误"
     * 7 => "缺少关键参数"
     * 8 => "查找不到对应内容"
     * 9 => "对话-按钮已选择"
     * 10 => "该手机号已存在"
     * 11 => "用户所在机构与该设备不同"
     * 12 => "未找到用户所在机构"
     */
    public static final int FAIL = 0;
    private static final int CODE_SUCCESS = 1;
    public static final int FAIL_CODE = 2;
    public static final int FAIL_PASSWORD = 3;
    public static final int FAIL_PASSWORD_CODE = 4;
    public static final int FAIL_TOKEN = 5;
    public static final int FAIL_ORIGIN_PASSWORD = 6;
    public static final int FAIL_PARAMETER = 7;
    public static final int FAIL_NO_CONTENT = 8;
    public static final int FAIL_RESELECT = 9;
    public static final int FAIL_PHONE_EXISTS = 10;
    public static final int FAIL_ROBOT_ID = 11;
    public static final int FAIL_NO_ORGAN = 12;

    public static boolean isOrganValid(int code) {
        return code == FAIL_ROBOT_ID || code == FAIL_NO_ORGAN;
    }

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
            case FAIL:
                return "请求失败";
            case CODE_SUCCESS:
                return "成功";
            case FAIL_CODE:
                return "验证码错误";
            case FAIL_PASSWORD:
                return "密码错误";
            case FAIL_PASSWORD_CODE:
                return "请输入密码或验证码";
            case FAIL_TOKEN:
                return "无效登录凭证";
            case FAIL_ORIGIN_PASSWORD:
                return "原密码错误";
            case FAIL_PARAMETER:
                return "缺少关键参数";
            case FAIL_NO_CONTENT:
                return "查找不到对应内容";
            case FAIL_RESELECT:
                return "对话-按钮已选择";
            case FAIL_PHONE_EXISTS:
                return "该手机号已存在";
            case FAIL_ROBOT_ID:
                return "用户所在机构与该设备不同";
            case FAIL_NO_ORGAN:
                return "未找到用户所在机构";
        }
        return msg;
    }

    @Override
    public boolean isSuccessful() {
        return code == CODE_SUCCESS;
    }

    @SerializedName("ext")
    private Ext ext;

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

        private boolean achieve;

        public boolean isAchieve() {
            return achieve;
        }
    }

}
