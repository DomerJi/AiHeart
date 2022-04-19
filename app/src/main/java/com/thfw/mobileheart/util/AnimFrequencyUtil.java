package com.thfw.mobileheart.util;

import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.constants.AnimFileName;

/**
 * Author:pengs
 * Date: 2022/4/18 10:30
 * Describe:Todo
 */
public class AnimFrequencyUtil {

    public static int getAnimFrequency(){
       return SharePreferenceUtil.getInt(AnimFileName.Frequency.KEY_FREQUENCY, 0);
    }


    public static void setAnimFrequency(int frequency) {
        SharePreferenceUtil.setInt(AnimFileName.Frequency.KEY_FREQUENCY, frequency);
    }

    public static String getAnimFrequencyStr() {
        int i = getAnimFrequency();
        switch (i) {
            case AnimFileName.Frequency.EVERY_TIME:
                return "每次";
            case AnimFileName.Frequency.DAY_TIME:
                return "每天一次";
            case AnimFileName.Frequency.WEEK_TIME:
                return "每星期一次";
        }
        return "";
    }
}
