package com.thfw.robotheart.util;

import android.util.Log;

import java.util.Arrays;


/**
 * Author:pengs
 * Date: 2021/8/26 11:00
 * Describe:根据云端解析指令条状到对应页面
 */
public class PageJumpUtils {

    public static final int JUMP_TEXT = 1;
    public static final int JUMP_MUSIC = 2;
    public static final int JUMP_AI_HOME = 3;
    private static final String TAG = PageJumpUtils.class.getSimpleName();

    /**
     * @param href pages/text/text=>心理测评
     *             pages/music/music_album=>冥想音频
     *             pages/ai_home/treate_list=>主题对话
     * @return
     */
    public static boolean jump(String href, OnJumpListener onJumpListener) {
        Log.d(TAG, "href = " + href);
        if (href.startsWith("pages")) {
            String[] hrefArray = href.split("/");
            Log.d(TAG, "hrefArray = " + Arrays.toString(hrefArray));
            if (hrefArray != null && hrefArray.length >= 3) {
                if ("text".equals(hrefArray[1])) {
                    onJumpListener.onJump(JUMP_TEXT, null);
                    return true;
                } else if ("music".equals(hrefArray[1])) {
                    onJumpListener.onJump(JUMP_MUSIC, null);
                    return true;
                } else if ("ai_home".equals(hrefArray[1])) {
                    onJumpListener.onJump(JUMP_AI_HOME, null);
                    return true;
                } else {
                    Log.d(TAG, "href = else 不匹配");
                }
            }
        }
        return false;
    }

    public interface OnJumpListener {
        void onJump(int type, Object obj);
    }

}
