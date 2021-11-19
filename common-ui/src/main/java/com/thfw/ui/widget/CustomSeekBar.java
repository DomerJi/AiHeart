package com.thfw.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class CustomSeekBar extends SeekBar {

    private Rect mRect;
    private boolean mInterceptClick = false;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Drawable drawable = getThumb();
                mRect = drawable.getBounds();
                if (mInterceptClick) {
                    return interceptAction(x, y);
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     *  主要逻辑都在这里，主要思路就是判断触摸点位置是否在进度条的小圆点里
     *
     *  这里分别向左右扩大了50像素，目的是为了优化拖拽体验，因为小圆点非常小，
     *  拖拽时可能没那么准确的触摸到小圆点区域，导致很难进行拖拽
     */
    private boolean interceptAction(float x, float y) {
        if(mRect == null){
            return true;
        }
        Rect rect = new Rect(mRect.left - 50, mRect.top, mRect.right + 50, mRect.bottom);
        if (rect != null) {
            if (rect.contains((int) (x), (int) (y))) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void interceptAction(boolean interceptClick) {
        mInterceptClick = interceptClick;
    }
}