package com.thfw.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.scwang.wave.MultiWaveHeader;

/**
 * Author:pengs
 * Date: 2022/7/29 14:20
 * Describe:Todo
 */
public class MyMultiWaveHeader extends MultiWaveHeader {

    private int maxWidth;
    private int maxVelocity = 55;


    public void setMaxVelocity(int maxVelocity) {
        this.maxVelocity = maxVelocity;
        onVelocityChange();
    }

    public MyMultiWaveHeader(Context context) {
        super(context);
    }

    public MyMultiWaveHeader(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMultiWaveHeader(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        onVelocityChange();
    }

    private void onVelocityChange() {
        int width = getWidth();

        if (width > 0) {
            if (width > maxWidth) {
                maxWidth = width;
            }
            setVelocity(width * 1.0f / maxWidth * maxVelocity);
        }
    }
}
