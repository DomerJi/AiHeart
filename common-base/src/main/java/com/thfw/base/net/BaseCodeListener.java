package com.thfw.base.net;

/**
 * Author:pengs
 * Date: 2022/3/25 11:40
 * Describe:Todo
 */
public interface BaseCodeListener {
    int LOGOUT = -1;

    void onCode(int code);
}
