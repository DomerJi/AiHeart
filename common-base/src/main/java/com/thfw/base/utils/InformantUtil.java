package com.thfw.base.utils;

import android.text.TextUtils;

/**
 * Author:pengs
 * Date: 2022/4/14 16:43
 * Describe:Todo
 */
public class InformantUtil {

    private static final String KEY_SELECTED = "key.informant.selected";
    private static final String TAG = InformantUtil.class.getSimpleName();
    // 默认小师
    private static final String DEFAULT = "x2_xiaoshi_cts";
    private static String currentInformant;

    public static String getInformant() {

        if (EmptyUtil.isEmpty(currentInformant)) {
            currentInformant = SharePreferenceUtil.getString(KEY_SELECTED, DEFAULT);
            if (EmptyUtil.isEmpty(currentInformant)) {
                currentInformant = DEFAULT;
                SharePreferenceUtil.setString(KEY_SELECTED, DEFAULT);
            }
            LogUtil.d(TAG, "getInformant -> currentInformant = " + currentInformant);
            return currentInformant;
        } else {
            LogUtil.d(TAG, "getInformant -> currentInformant = " + currentInformant);
            return currentInformant;
        }
    }

    public static void setInformant(String currentInformant) {
        LogUtil.d(TAG, "setInformant -> currentInformant = " + currentInformant);
        if (!TextUtils.isEmpty(currentInformant)) {
            InformantUtil.currentInformant = currentInformant;
            SharePreferenceUtil.setString(KEY_SELECTED, currentInformant);
        }
    }
}
