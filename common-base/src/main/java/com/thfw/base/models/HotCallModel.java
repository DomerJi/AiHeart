package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.LogUtil;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Author:pengs
 * Date: 2022/1/26 11:08
 * Describe:Todo
 */
public class HotCallModel implements IModel {


    /**
     * id : 1
     * letter : A
     * province : 安徽省心理援助热线
     * area : 安徽
     * title : 安徽省
     * phone : 0551-63666903
     * time : 周一到周日，24小时
     */

    @SerializedName("id")
    public int id;
    @SerializedName("letter")
    public String letter;
    @SerializedName("province")
    public String province;
    @SerializedName("area")
    public String area;
    @SerializedName("title")
    public String title;
    @SerializedName("phone")
    public String phone;
    @SerializedName("time")
    public String time;


    private int azCode;
    private String azStr;
    private String azQpin;

    public void initAz() {
        String firstChar = province.substring(0, 1);
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] input = firstChar.trim().toCharArray();
        StringBuffer output = new StringBuffer("");


        try {
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output.append(temp[0]);
                    output.append(" ");
                } else
                    output.append(Character.toString(input[i]));
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        String pinyin = output.toString();
        this.azQpin = pinyin;
        LogUtil.d("HotCallModel", "output -> " + pinyin);
        this.azStr = pinyin.substring(0, 1).toUpperCase();
        if (azStr != null && azStr.matches("^[a-zA-Z]*")) {
            this.azCode = azStr.getBytes()[0];
        } else {
            this.azCode = 100;
        }

    }

    public String getAzStr() {
        return azStr;
    }

    public int getAzCode() {
        return azCode;
    }
}
