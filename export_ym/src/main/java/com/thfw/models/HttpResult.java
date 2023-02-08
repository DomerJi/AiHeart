package com.thfw.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.IModel;
import com.thfw.base.IResult;
import com.thfw.util.ToastUtil;
import com.thfw.util.YmActivityManager;

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
    public static final int FAIL_PUSH_ID_ERROR = 13;
    public static final int FAIL_USER_NO_FOUNT = 14;
    // 选择加入机构的时候，【您所在设备类型不能加入此机构】
    public static final int FAIL_DEVICE_NOT_JOIN_ORGAN_BY_AUTH = 15;
    public static final int FAIL_NO_ACCOUNT = 16;
    // 您所在的机构，购买的服务开始时间为：202X-XX-XX XX:XX:XX。请耐心等待或联系所在机构管理员（登录返回）
    public static final int SERVICE_TIME_NO_START_BY_LOGIN = 17;
    // 已达到机构使用人数上限，请联系所在机构管理员
    public static final int SERVICE_PEOPLE_MAX = 18;
    // 您所在的机构服务已过期，请联系所在机构管理员（登录返回）
    public static final int SERVICE_TIME_ALREADY_END_BY_LOGIN = 19;
    // 您所在的机构，购买的服务开始时间为：202X-XX-XX XX:XX:XX。请耐心等待或联系所在机构管理员
    public static final int SERVICE_TIME_NO_START = 20;
    // 您所在的机构服务已过期，请联系所在机构管理员(token验证)
    public static final int SERVICE_TIME_ALREADY_END = 21;
    // 您所在的机构，购买的服务开始时间为：202X-XX-XX XX:XX:XX。请耐心等待或联系所在机构管理员
    public static final int SERVICE_TIME_NO_START_BY_JOIN = 22;
    // 您所在的机构服务已过期，请联系所在机构管理员(token验证)
    public static final int SERVICE_TIME_ALREADY_END_BY_JOIN = 23;

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

    public static boolean isOrganValid(int code) {
        return code == FAIL_ROBOT_ID || code == FAIL_NO_ORGAN;
    }

    public static boolean isServerTimeNoValid(int code) {
        return code == SERVICE_TIME_ALREADY_END_BY_LOGIN || code == SERVICE_TIME_NO_START_BY_LOGIN;
    }

    public T getData() {
        return data;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        String tempMsg = getMsg(code);
        return tempMsg == null ? msg : tempMsg;
    }

    public static String getMsg(int code) {
        switch (code) {
            case FAIL:
                return "请求失败";
            case CODE_SUCCESS:
                return "成功";
            case FAIL_CODE:
                return "验证码错误";
            case FAIL_PASSWORD:
//                return "密码错误";
                return "账号或密码错误";
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
            case FAIL_PUSH_ID_ERROR:
                return "错误的推送ID";
            case FAIL_USER_NO_FOUNT:
                return "用户未找到";
            case FAIL_DEVICE_NOT_JOIN_ORGAN_BY_AUTH:
//                return "您所在设备类型不能加入此机构";
                return "您未购买该设备服务，请联系所在机构管理员";
            case FAIL_NO_ACCOUNT:
//                return "账号不存在，请创建账号";
                return "账号或密码错误";
            case SERVICE_PEOPLE_MAX:
                return "已达到机构使用人数上限，请联系所在机构管理员";
            case SERVICE_TIME_ALREADY_END_BY_LOGIN:
                return "您所在的机构服务已过期，请联系所在机构管理员";
            case SERVICE_TIME_ALREADY_END:
                return "您所在的机构服务已过期，请联系所在机构管理员";
            case SERVICE_TIME_ALREADY_END_BY_JOIN:
                return "该机构服务已过期，请联系所在机构管理员";
//            case SERVICE_TIME_NO_START_BY_JOIN:
//                return "该机构，购买的服务开始时间为：202X-XX-XX XX:XX:XX。请耐心等待或联系所在机构管理员";
//            case SERVICE_TIME_NO_START_BY_LOGIN:
//                return "所在的机构，购买的服务开始时间为：202X-XX-XX XX:XX:XX。请耐心等待或联系所在机构管理员";
//            case SERVICE_TIME_NO_START:
//                return "您所在的机构，购买的服务开始时间为：202X-XX-XX XX:XX:XX。请耐心等待或联系所在机构管理员";
        }
        return null;
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

    public static boolean isTokenFail(int code) {
        return code == HttpResult.FAIL_TOKEN;

    }

    public static boolean noTokenFailShowMsg(int code) {
        boolean isTokenFail = isTokenFail(code);
        if (!isTokenFail) {
            YmActivityManager.getActivityManager().removeAll();
            ToastUtil.showLong(getMsg(code));
        }
        return isTokenFail;
    }

    public static boolean isServiceFail(int code) {
        return code == HttpResult.SERVICE_TIME_NO_START || code == HttpResult.SERVICE_TIME_ALREADY_END || code == HttpResult.SERVICE_TIME_ALREADY_END_BY_LOGIN || code == HttpResult.SERVICE_TIME_ALREADY_END_BY_JOIN;
    }

}
