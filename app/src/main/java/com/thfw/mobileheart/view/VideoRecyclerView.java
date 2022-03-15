package com.thfw.mobileheart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * Author:pengs
 * Date: 2022/3/14 15:02
 * Describe:Todo
 */
public class VideoRecyclerView extends RecyclerView {

    public VideoRecyclerView(@NonNull @NotNull Context context) {
        super(context);
    }

    public VideoRecyclerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoRecyclerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (getLayoutManager() instanceof LinearLayoutManager && ((LinearLayoutManager) getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            return true;
        }
        return super.canScrollHorizontally(direction);
    }
}
