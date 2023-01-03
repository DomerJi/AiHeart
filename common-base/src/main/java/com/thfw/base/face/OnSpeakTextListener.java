package com.thfw.base.face;

/**
 * Author:pengs
 * Date: 2022/12/31 13:07
 * Describe:Todo
 */
public interface OnSpeakTextListener<T> {

    int TYPE_SPEAK_TEXT = 0;
    int TYPE_SPEAK_ORDER = 1;

    String getText(int position, int type);

    void onSpeakItemClick(int position);

}
