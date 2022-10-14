package com.thfw.ui.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.thfw.base.utils.Util;


/**
 * 全透明状态栏高度自动补足
 * Created By jishuaipeng on 2021/5/20
 */
public class LinearTopLayout extends LinearLayout {
    public LinearTopLayout(Context context) {
        this(context, null);
    }

    public LinearTopLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LinearTopLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !DeviceUtil.isLhXk_CM_GB03D()) {
            setPadding(0, Util.getStatusBarHeight(context), 0, 0);
        }
    }
}
