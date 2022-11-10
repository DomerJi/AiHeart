package com.thfw.base.models;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.GsonUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class BookStudyTypeModel implements IModel {

    public String name;
    public int id = 0;

    @SerializedName("sort")
    public int sort;
    @SerializedName("children")
    public List<BookStudyTypeModel> list;

    @SerializedName("selected_color")
    public String selectedColorStr;

    @SerializedName("un_selected_color")
    public String unSelectedColorStr;

    @SerializedName("fire")
    public int fire;

    public boolean isChangedColor() {
        refreshColorName();
        return !TextUtils.isEmpty(selectedColorStr) || !TextUtils.isEmpty(unSelectedColorStr);
    }

    public boolean isUnSelectedChange() {
        refreshColorName();
        return !TextUtils.isEmpty(unSelectedColorStr);
    }

    private void refreshColorName() {
        if (TextUtils.isEmpty(unSelectedColorStr) && name != null && name.contains("二十大")) {
            unSelectedColorStr = "#FF0000";
        }
        if (TextUtils.isEmpty(selectedColorStr) && name != null && name.contains("二十大")) {
            selectedColorStr = "#FF0000";
        }
    }

    public int getUnSelectedColor() {
        refreshColorName();
        try {
            return Color.parseColor(unSelectedColorStr);
        } catch (Exception e) {
            return Color.parseColor("#333333");
        }
    }

    public int getSelectedColor() {
        refreshColorName();
        try {
            return Color.parseColor(selectedColorStr);
        } catch (Exception e) {
            return Color.parseColor("#00A871");
        }
    }

    public boolean isSelectedChange() {
        return !TextUtils.isEmpty(unSelectedColorStr);
    }

    public BookStudyTypeModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public BookStudyTypeModel() {
    }

    public List<BookStudyTypeModel> getList() {
        sort(list);
        return list;
    }

    public static void sort(List<BookStudyTypeModel> list) {
        if (list != null) {
            boolean up = new Random().nextBoolean();
            Collections.sort(list, new Comparator<BookStudyTypeModel>() {
                @Override
                public int compare(BookStudyTypeModel o1, BookStudyTypeModel o2) {
                    if (o1.sort == o2.sort) {
                        return 0;
                    }
//                    if (up) {
                    return o1.sort > o2.sort ? -1 : 1;
//                    } else {
//                        return o1.sort > o2.sort ? 1 : -1;
//                    }
                }
            });

            Log.d("BookStudy", GsonUtil.toJson(list));
        }
    }

}
