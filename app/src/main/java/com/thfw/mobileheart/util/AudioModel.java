package com.thfw.mobileheart.util;

import com.thfw.mobileheart.R;

/**
 * Author:pengs
 * Date: 2021/8/3 11:10
 * Describe:Todo
 */
public class AudioModel {

    public interface Cmd {
        int PAUSE = 0;
        int PLAY = 1;
        int LAST = 2;
        int NEXT = 3;
    }


    public int getPicId() {
        return R.mipmap.ic_launcher;
    }


}
