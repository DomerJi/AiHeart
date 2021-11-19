package com.thfw.robotheart.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

/**
 * Created By jishuaipeng on 2021/5/25
 */
public class CustomRefreshLayout extends SmartRefreshLayout {

    public CustomRefreshLayout(Context context) {
        super(context, null);
    }

    @SuppressLint("ResourceAsColor")
    public CustomRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEnableFooterFollowWhenNoMoreData = true;
    }

}
