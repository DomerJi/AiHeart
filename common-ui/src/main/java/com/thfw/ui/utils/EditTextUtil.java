package com.thfw.ui.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author:pengs
 * Date: 2022/4/25 11:37
 * Describe:Todo
 */
public class EditTextUtil {

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

        editText.setFilters(new InputFilter[]{filter});

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

        editText.setFilters(new InputFilter[]{filter});

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

        editText.setFilters(new InputFilter[]{filter});

    }


}
