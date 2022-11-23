package com.thfw.base.models;

import android.graphics.Color;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.ContextApp;
import com.thfw.base.base.IModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class BookTypeModel extends HashMap<String, String> implements IModel {


    private List<BookTypeImpModel> mList;


    public List<BookTypeImpModel> getList() {
        if (mList == null) {
            mList = new ArrayList<>();

            Set<Entry<String, String>> set = entrySet();
            for (Entry<String, String> entry : set) {
                mList.add(new BookTypeImpModel(entry.getKey(), entry.getValue()));
            }
            sort(mList);
            mList.add(0, new BookTypeImpModel("0", "全部"));
        }
        return mList;
    }

    public static class BookTypeImpModel implements IModel {
        public String key;
        public String value;
        public int id;

        public BookTypeImpModel(String key, String value) {
            this.key = key;
            this.value = value;
            try {
                this.id = Integer.parseInt(key);
            } catch (Exception e) {

            }

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
//            if (TextUtils.isEmpty(unSelectedColorStr) && value != null && value.contains("关系")) {
//                unSelectedColorStr = "#FF0000";
//            }
//            if (TextUtils.isEmpty(selectedColorStr) && value != null && value.contains("关系")) {
//                selectedColorStr = "#FF0000";
//                fire = 1;
//            }
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


    }

    public static void sort(List<BookTypeModel.BookTypeImpModel> list) {
        if (list != null) {
            Collections.sort(list, new Comparator<BookTypeModel.BookTypeImpModel>() {
                @Override
                public int compare(BookTypeModel.BookTypeImpModel o1, BookTypeModel.BookTypeImpModel o2) {
                    if (o1.sort == o2.sort) {
                        return 0;
                    }

                    return o1.sort > o2.sort ? -1 : 1;
                }
            });
        }
    }

}
