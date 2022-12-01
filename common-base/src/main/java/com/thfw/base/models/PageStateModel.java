package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.StringUtil;

import java.util.Objects;

/**
 * Author:pengs
 * Date: 2022/11/18 14:57
 * Describe:Todo
 */
public class PageStateModel implements IModel{

    private static final String TRUE_FLAG = "1";

    @SerializedName("home_black")
    private String homeBlack;

    @SerializedName("hide_red_flag")
    private String hideRedFlag;

    public void setHomeBlack(String homeBlack) {
        this.homeBlack = homeBlack;
    }

    public boolean isHomeBlack() {
        return TRUE_FLAG.equals(homeBlack);
    }

    public void setHideRedFlag(String hideRedFlag) {
        this.hideRedFlag = hideRedFlag;
    }

    public boolean isHideRedFlag() {
        return TRUE_FLAG.equals(hideRedFlag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageStateModel that = (PageStateModel) o;
        return StringUtil.contentEquals(homeBlack,that.homeBlack) &&
                StringUtil.contentEquals(hideRedFlag,that.hideRedFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeBlack, hideRedFlag);
    }
}
