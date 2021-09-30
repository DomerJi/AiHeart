package com.thfw.base.net;

import com.thfw.base.base.IModel;
import com.thfw.base.base.IResult;

/**
 *
 */
public class HttpResult<T> implements IResult, IModel {

    private static final int CODE_SUCCESS = 0;

    /**
     * 返回状态值
     * 0表示成功
     */
    private int errno;

    /**
     * 返回结果提示
     */
    private String err_msg;

    /**
     * 返回结果数据
     */
    private T data;

    public T getData() {
        return data;
    }

    @Override
    public int getCode() {
        return errno;
    }

    @Override
    public String getMsg() {
        return err_msg;
    }

    @Override
    public boolean isSuccessful() {
        return errno == CODE_SUCCESS;
    }
}
