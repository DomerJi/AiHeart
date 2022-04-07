package com.thfw.mobileheart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * Author:pengs
 * Date: 2022/3/15 10:40
 * Describe:Todo
 */
public class LastTextView extends androidx.appcompat.widget.AppCompatTextView {


    private Runnable runnable;
    private int lastVisible = -1;
    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy == 0) {
                return;
            }
            onAnim(dy < 0);

        }
    };

    public LastTextView(Context context) {
        super(context);
    }

    public LastTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LastTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onAttached(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void onAnim(boolean visible) {
        int currentVisible = visible ? 1 : 0;

        if (lastVisible != currentVisible) {
            lastVisible = currentVisible;
            clearAnimation();
            int y = visible ? 0 : getHeight();
            animate().translationY(y).setInterpolator(new DecelerateInterpolator());
        }

    }

}
