package com.thfw.base;

import java.io.Serializable;

public interface IResult extends Serializable {

    public boolean isSuccessful();

    public abstract int getCode();

    public abstract String getMsg();
}
