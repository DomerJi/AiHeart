package com.thfw.base.models;

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

    private String name;
    private int azCode;
    private String azStr;
    private String azQpin;

    public HotCallModel(String name) {
        this.name = name;
        String firstChar = name.substring(0, 1);
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
        this.azCode = azStr.getBytes()[0];
    }

    public String getName() {
        return name;
    }

    public String getAzStr() {
        return azStr;
    }

    public int getAzCode() {
        return azCode;
    }
}
