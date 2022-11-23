package com.thfw.base.models;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.ContextApp;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.GsonUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class AudioTypeModel implements IModel {

    private String name;
    private int key;

    public AudioTypeModel() {

    }

    public AudioTypeModel(String name, int key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }


    @SerializedName("sort")
    public int sort;

    @SerializedName("selected_color")
    public String selectedColorStr;

    @SerializedName("un_selected_color")
    public String unSelectedColorStr;

    @SerializedName("fire")
    public int fire;

    public String getSelectedColorStr() {
        refreshColorName();
        if (selectedColorStr == null) {
            switch (ContextApp.getDeviceType()) {
                case ContextApp.DeviceType.ROBOT:
                    return "#FFFFFF";
                default:
                    return "#333333";
            }
        }
        return selectedColorStr;
    }

    public String getUnSelectedColorStr() {
        refreshColorName();
        if (unSelectedColorStr == null) {
            switch (ContextApp.getDeviceType()) {
                case ContextApp.DeviceType.ROBOT:
                    return "#000000";
                default:
                    return "#00A871";
            }
        }
        return unSelectedColorStr;
    }

    public boolean isChangedColor() {
        refreshColorName();
        return !TextUtils.isEmpty(selectedColorStr) || !TextUtils.isEmpty(unSelectedColorStr);
    }

    public boolean isUnSelectedChange() {
        refreshColorName();
        return !TextUtils.isEmpty(unSelectedColorStr);
    }

    private void refreshColorName() {
//        if (TextUtils.isEmpty(unSelectedColorStr) && name != null && name.contains("睡眠")) {
//            unSelectedColorStr = "#FF0000";
//            fire = 1;
//        }
//        if (TextUtils.isEmpty(selectedColorStr) && name != null && name.contains("睡眠")) {
//            selectedColorStr = "#FF0000";
//        }
    }

    public int getUnSelectedColor() {
        refreshColorName();
        if (TextUtils.isEmpty(unSelectedColorStr)) {
            switch (ContextApp.getDeviceType()) {
                case ContextApp.DeviceType.ROBOT:
                    return Color.parseColor("#FFFFFF");
                default:
                    return Color.parseColor("#333333");
            }

        }
        try {
            return Color.parseColor(unSelectedColorStr);
        } catch (Exception e) {
            switch (ContextApp.getDeviceType()) {
                case ContextApp.DeviceType.ROBOT:
                    return Color.parseColor("#FFFFFF");
                default:
                    return Color.parseColor("#333333");
            }

        }
    }

    public int getSelectedColor() {
        refreshColorName();
        if (TextUtils.isEmpty(selectedColorStr)) {
            switch (ContextApp.getDeviceType()) {
                case ContextApp.DeviceType.ROBOT:
                    return Color.parseColor("#000000");
                default:
                    return Color.parseColor("#00A871");
            }
        }
        try {
            return Color.parseColor(selectedColorStr);
        } catch (Exception e) {
            switch (ContextApp.getDeviceType()) {
                case ContextApp.DeviceType.ROBOT:
                    return Color.parseColor("#000000");
                default:
                    return Color.parseColor("#00A871");
            }
        }
    }

    public boolean isSelectedChange() {
        return !TextUtils.isEmpty(unSelectedColorStr);
    }

    public static void sort(List<AudioTypeModel> list) {
        if (list != null) {
            Collections.sort(list, new Comparator<AudioTypeModel>() {
                @Override
                public int compare(AudioTypeModel o1, AudioTypeModel o2) {
                    if (o1.sort == o2.sort) {
                        return 0;
                    }
                    return o1.sort > o2.sort ? -1 : 1;
                }
            });

            Log.d("BookStudy", GsonUtil.toJson(list));
        }
    }

}
