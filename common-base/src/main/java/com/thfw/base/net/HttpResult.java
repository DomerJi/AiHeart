package com.thfw.base.net;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.base.IResult;

/**
 *
 */
public class HttpResult<T> implements IResult, IModel {

    private static final int CODE_SUCCESS = 1;

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
        private boolean achieve;

        public boolean isAchieve() {
            return achieve;
        }
    }

}
