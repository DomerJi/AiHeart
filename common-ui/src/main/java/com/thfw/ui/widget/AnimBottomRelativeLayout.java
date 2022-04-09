package com.thfw.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.thfw.base.utils.Util;

/**
 * Author:pengs
 * Date: 2022/4/9 16:09
 * Describe:Todo
 */
public class AnimBottomRelativeLayout extends RelativeLayout {

    private int height;
    private boolean oldVisible;

    public AnimBottomRelativeLayout(Context context) {
        this(context, null);
    }

    public AnimBottomRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AnimBottomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        height = Util.dipToPx(46, getContext());
        oldVisible = getVisibility() != GONE;
        post(() -> {
            oldVisible = getHeight() > 0;
        });
    }

    public void setVisibility(boolean visible) {
        setEnabled(visible);
        if (oldVisible == visible) {
            return;
        }
        this.oldVisible = visible;
        if (visible) {
            anim(getLayoutParams().height, height);
        } else {
            anim(height, 0);
        }
    }

    private void anim(int start, int end) {
        ValueAnimator animator = ObjectAnimator.ofInt(start, end);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                getLayoutParams().height = height;
                setLayoutParams(getLayoutParams());
            }
        });
        animator.start();
    }

}
