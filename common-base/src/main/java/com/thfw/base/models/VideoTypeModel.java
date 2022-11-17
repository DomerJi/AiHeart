package com.thfw.base.models;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.ContextApp;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class VideoTypeModel implements IModel {

    public String name;
    public int id = 0;

    @SerializedName("root_type")
    public int rootType = 0;
    @SerializedName("sort")
    public int sort;
    @SerializedName("children")
    public List<VideoTypeModel> list;

    public VideoTypeModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public List<VideoTypeModel> getList() {
        sort(list);
        return list;
    }


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
//        if (TextUtils.isEmpty(unSelectedColorStr) && sort < 0 && String.valueOf(sort).endsWith("7")) {
//            unSelectedColorStr = "#FF0000";
//        }
//        if (TextUtils.isEmpty(selectedColorStr) && sort < 0 && String.valueOf(sort).endsWith("7")) {
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
                    return Color.parseColor("#91dff3");
                default:
                    return Color.parseColor("#00A871");
            }
        }
        try {
            return Color.parseColor(selectedColorStr);
        } catch (Exception e) {
            switch (ContextApp.getDeviceType()) {
                case ContextApp.DeviceType.ROBOT:
                    return Color.parseColor("#91dff3");
                default:
                    return Color.parseColor("#00A871");
            }
        }
    }

    public boolean isSelectedChange() {
        return !TextUtils.isEmpty(unSelectedColorStr);
    }


    public static void resetList(List<VideoTypeModel> list) {
        if (list != null) {
            for (VideoTypeModel model : list) {
                if (!EmptyUtil.isEmpty(model.list)) {
                    model.list = resetNewList(model.list);
                }
            }
        }
    }

    public static List<VideoTypeModel> resetNewList(List<VideoTypeModel> list) {
        List<VideoTypeModel> newList = new ArrayList<>();
        if (list != null) {
            int size = list.size();

            for (int i = 0; i < size; i++) {
                VideoTypeModel model = list.get(i);
                if (model.sort < 0 && model.list == null) {

                    int startLimit = model.sort * 1000;
                    int endLimit = (model.sort - 1) * 1000;
                    Log.i("DD", "startLimit -> " + GsonUtil.toJson(list));

                    for (VideoTypeModel type : list) {
                        Log.i("DD", "startLimit -> " + startLimit + " ; endLimit -> " + endLimit);
                        if (type.sort <= startLimit && type.sort > endLimit) {
                            if (model.list == null) {
                                model.list = new ArrayList<>();
                            }
                            model.list.add(type);
                        }
                    }
                    if (model.list != null) {
                        newList.add(model);
                    }
                } else {
                    newList.add(model);
                }
            }
        }

        return newList;
    }

    public static void sort(List<VideoTypeModel> list) {

        if (list != null) {

            Collections.sort(list, new Comparator<VideoTypeModel>() {
                @Override
                public int compare(VideoTypeModel o1, VideoTypeModel o2) {
                    try {
                        if (Math.abs(o1.sort) == Math.abs(o2.sort)) {
                            return 0;
                        }
                        return Math.abs(o1.sort) > Math.abs(o2.sort) ? -1 : 1;
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });

            Log.d("VideoType", GsonUtil.toJson(list));
        }
    }
}
