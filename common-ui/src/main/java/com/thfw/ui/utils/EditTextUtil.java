package com.thfw.ui.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import com.thfw.base.utils.RegularUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author:pengs
 * Date: 2022/4/25 11:37
 * Describe:Todo
 */
public class EditTextUtil {

    private static final String ABC_123 = "0123456789abcdefghijklmnopqrstuvwxyz";


    /**
     * 禁止EditText输入空格
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText) {

        InputFilter filter = new InputFilter() {
            @Override

            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }

            }

        };

        if (editText.getFilters() != null) {
            int len = editText.getFilters().length;
            int newLen = len + 1;
            InputFilter[] filters = new InputFilter[newLen];
            for (int i = 0; i < len; i++) {
                filters[i] = editText.getFilters()[i];
            }
            filters[newLen - 1] = filter;
            editText.setFilters(filters);
        } else {
            editText.setFilters(new InputFilter[]{filter});
        }

    }


    /**
     * 禁止EditText输入特殊字符
     *
     * @param editText
     */

    public static void setEditTextInhibitInputSpeChat(EditText editText) {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

                Pattern pattern = Pattern.compile(speChat);

                Matcher matcher = pattern.matcher(source.toString());

                if (matcher.find()) {
                    return "";
                } else {
                    return null;
                }

            }

        };

        if (editText.getFilters() != null) {
            int len = editText.getFilters().length;
            int newLen = len + 1;
            InputFilter[] filters = new InputFilter[newLen];
            for (int i = 0; i < len; i++) {
                filters[i] = editText.getFilters()[i];
            }
            filters[newLen - 1] = filter;
            editText.setFilters(filters);
        } else {
            editText.setFilters(new InputFilter[]{filter});
        }

    }

    /**
     * 禁止EditText输入特殊字符和空格
     *
     * @param editText
     */

    public static void setEditTextInhibitInputSpeChatAndSpace(EditText editText) {

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                }

                String speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

                Pattern pattern = Pattern.compile(speChat);

                Matcher matcher = pattern.matcher(source.toString());

                if (matcher.find()) {
                    return "";
                } else {
                    return null;
                }

            }

        };
        if (editText.getFilters() != null) {
            int len = editText.getFilters().length;
            int newLen = len + 1;
            InputFilter[] filters = new InputFilter[newLen];
            for (int i = 0; i < len; i++) {
                filters[i] = editText.getFilters()[i];
            }
            filters[newLen - 1] = filter;
            editText.setFilters(filters);
        } else {
            editText.setFilters(new InputFilter[]{filter});
        }


    }

    /**
     * EditText只可以输入字母和数字
     *
     * @param editText
     */

    public static void setEditTextOnlyAbc(EditText editText) {

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                }
                if (!source.toString().matches(RegularUtil.REGEX_ABC)) {
                    return "";
                } else {
                    return source.toString().toLowerCase();
                }

            }

        };
        if (editText.getFilters() != null) {
            int len = editText.getFilters().length;
            int newLen = len + 1;
            InputFilter[] filters = new InputFilter[newLen];
            for (int i = 0; i < len; i++) {
                filters[i] = editText.getFilters()[i];
            }
            filters[newLen - 1] = filter;
            editText.setFilters(filters);
        } else {
            editText.setFilters(new InputFilter[]{filter});
        }
    }


}
