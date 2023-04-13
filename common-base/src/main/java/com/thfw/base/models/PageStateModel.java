package com.thfw.base.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.HourUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Author:pengs
 * Date: 2022/11/18 14:57
 * Describe:Todo
 */
public class PageStateModel implements IModel {


    @SerializedName("black_color")
    public boolean blackColor;
    @SerializedName("holidays")
    public HolidaysBean holidays;
    @SerializedName("initialization_page")
    public List<String> initializationPage;

    @SerializedName("red_star")
    public boolean redStar;


    public boolean isHomeBlack() {
        return blackColor;
    }

    /**
     * 是否隐藏小红旗
     *
     * @return
     */
    public boolean isHideRedFlag() {
        return !redStar;
    }


    public static class HolidaysBean implements Serializable {
        @SerializedName("start")
        public String start;

        private long startLong;
        @SerializedName("end")
        public String end;

        private long endLong;
        @SerializedName("color")
        public String color;

        public long getEnd() {
            if (endLong == 0) {
                endLong = HourUtil.getYYMMDDbyLong(end);
            }
            Log.i("HolidaysBean", "endLong -> " + endLong);
            return endLong;
        }

        public long getStart() {
            if (startLong == 0) {
                startLong = HourUtil.getYYMMDDbyLong(start);
            }
            Log.i("HolidaysBean", "startLong -> " + startLong);
            return startLong;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageStateModel that = (PageStateModel) o;
        return blackColor == that.blackColor && redStar == that.redStar && Objects.equals(holidays, that.holidays) && Objects.equals(initializationPage, that.initializationPage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blackColor, holidays, initializationPage, redStar);
    }

}
