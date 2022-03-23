package com.thfw.net;

import androidx.annotation.Nullable;

import com.thfw.base.IResult;

public class ResponeThrowable extends Throwable implements IResult {
    public int code;
    public String message;

    public ResponeThrowable(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public ResponeThrowable(int code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }


    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return message;
    }
}