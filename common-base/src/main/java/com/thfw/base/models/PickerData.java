package com.thfw.base.models;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/1/20 10:29
 * Describe:Todo
 */
public class PickerData implements IPickerViewData {

    private String name;
    private int type = -1;
    private boolean check;

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isCheck() {
        return check;
    }

    public int getType() {
        return type;
    }

    public PickerData(String name) {
        this.name = name;
    }

    public PickerData(String name, int type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getPickerViewText() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Factory {

        /**
         * 性别
         *
         * @return
         */
        public static List<PickerData> getSex() {
            List<PickerData> list = new ArrayList<>();
            list.add(new PickerData("男", 1));
            list.add(new PickerData("女", 2));
            return list;
        }

        public static String getSex(int sex) {
            List<PickerData> list = getSex();
            for (PickerData data : list) {
                if (data.type == sex) {
                    return data.name;
                }
            }
            return "";
        }

        /**
         * 民族
         *
         * @return
         */
        public static List<PickerData> getNation() {
            List<PickerData> list = new ArrayList<>();
            String[] nationArray = new String[]{
                    "汉族", "壮族", "满族", "回族", "苗族", "维吾尔族", "土家族", "彝族",
                    "蒙古族", "藏族", "布依族", "侗族", "瑶族", "朝鲜族", "白族", "哈尼族",
                    "哈萨克族", "黎族", "傣族", "畲族", "傈僳族", "仡佬族", "东乡族", "高山族",
                    "拉祜族", "水族", "佤族", "纳西族", "羌族", "土族", "仫佬族", "锡伯族",
                    "柯尔克孜族", "达斡尔族", "景颇族", "毛南族", "撒拉族", "布朗族", "塔吉克族",
                    "阿昌族", "普米族", "鄂温克族", "怒族", "京族", "基诺族", "德昂族", "保安族",
                    "俄罗斯族", "裕固族", "乌孜别克族", "门巴族", "鄂伦春族",
                    "独龙族", "塔塔尔族", "赫哲族", "珞巴族"};

            for (int i = 0; i < nationArray.length; i++) {
                list.add(new PickerData(nationArray[i], i));
            }
            return list;
        }

        /**
         * 政治面貌
         *
         * @return
         */
        public static List<PickerData> getFace() {
            List<PickerData> list = new ArrayList<>();
            list.add(new PickerData("党员(含预备党员)", 1));
            list.add(new PickerData("团员", 2));
            list.add(new PickerData("群众", 3));
            list.add(new PickerData("其他", 4));
            return list;
        }

        public static String getFace(int face) {
            List<PickerData> list = getFace();
            for (PickerData data : list) {
                if (data.type == face) {
                    return data.name;
                }
            }
            return "";
        }


        /**
         * 学历
         *
         * @return
         */
        public static List<PickerData> getSchool() {
            List<PickerData> list = new ArrayList<>();
            list.add(new PickerData("中专", 1));
            list.add(new PickerData("大专", 2));
            list.add(new PickerData("高中", 3));
            list.add(new PickerData("本科", 4));
            list.add(new PickerData("硕士", 5));
            list.add(new PickerData("博士", 6));
            return list;
        }

        public static String getSchool(int school) {
            List<PickerData> list = getSchool();
            for (PickerData data : list) {
                if (data.type == school) {
                    return data.name;
                }
            }
            return "";
        }

        /**
         * 职级
         *
         * @return
         */
        public static List<PickerData> getLevel() {
            List<PickerData> list = new ArrayList<>();
            list.add(new PickerData("普通", 0));
            list.add(new PickerData("中层", 1));
            list.add(new PickerData("高层", 2));
            return list;
        }

        /**
         * 婚姻
         *
         * @return
         */
        public static List<PickerData> getMarriage() {
            List<PickerData> list = new ArrayList<>();
            list.add(new PickerData("单身", 1));
            list.add(new PickerData("已婚", 2));
            list.add(new PickerData("离异", 3));
            list.add(new PickerData("丧偶", 4));
            return list;
        }

        public static String getMarriage(int m) {
            List<PickerData> list = getMarriage();
            for (PickerData data : list) {
                if (data.type == m) {
                    return data.name;
                }
            }
            return "";
        }

        /**
         * 职级
         *
         * @return
         */
        public static List<PickerData> getSon() {
            List<PickerData> list = new ArrayList<>();
            list.add(new PickerData("有", 1));
            list.add(new PickerData("无", 2));
            return list;
        }

        public static String getSon(int son) {
            List<PickerData> list = getSon();
            for (PickerData data : list) {
                if (data.type == son) {
                    return data.name;
                }
            }
            return "";
        }


        /**
         * 职级
         *
         * @return
         */
        public static List<PickerData> getLike() {
            List<PickerData> list = new ArrayList<>();
            list.add(new PickerData("阅读", 0));
            list.add(new PickerData("运动", 1));
            list.add(new PickerData("美食", 2));
            list.add(new PickerData("旅行", 3));
            list.add(new PickerData("音乐", 4));
            list.add(new PickerData("饮茶", 5));
            list.add(new PickerData("影视", 6));
            list.add(new PickerData("写作", 7));
            list.add(new PickerData("书画", 8));
            list.add(new PickerData("设计", 9));
            list.add(new PickerData("手工", 10));
            list.add(new PickerData("游戏", 11));
            list.add(new PickerData("收藏", 12));
            return list;
        }

        /**
         * 需要的支持
         *
         * @return
         */
        public static List<PickerData> getNeed() {
            List<PickerData> list = new ArrayList<>();
            list.add(new PickerData("倾听", 0));
            list.add(new PickerData("倾诉", 1));
            list.add(new PickerData("陪伴", 2));
            list.add(new PickerData("鼓励", 3));
            list.add(new PickerData("专业建议", 4));
            list.add(new PickerData("亲密关系", 5));
            list.add(new PickerData("认同", 6));
            list.add(new PickerData("社交", 7));
            return list;
        }


    }

}
