package com.thfw.robotheart.view;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

/**
 * Author:pengs
 * Date: 2022/3/8 9:50
 * Describe:Todo
 */
public class HomeIpTextView extends ConstraintLayout {
    private ImageView mIvTextBoxBg;
    private TextView mTvIpText;
    private Animator.AnimatorListener listener;
    private boolean zoomIn = false;
    private float min = 0.95f;
    private float max = 1.0f;
    private boolean resume = false;
    private String[] textArray;
    private int textArrayIndex = 0;
    private Runnable textViewRunnable;

    public HomeIpTextView(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public HomeIpTextView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public HomeIpTextView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.layout_ip_text, this);
        initView();
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HomeIpTextView);
            String hipText = ta.getString(R.styleable.HomeIpTextView_hip_text);
            if (!TextUtils.isEmpty(hipText)) {
                if (hipText.contains(" ")) {
                    textArray = hipText.split(" ");
                    if (textArray != null) {
                        if (textArray.length >= 1) {
                            mTvIpText.setText(textArray[0]);
                        }
                    }
                } else {
                    mTvIpText.setText(hipText);
                }
            }
        }

    }

    public void setIpText(String mIpText) {
        this.mTvIpText.setText(mIpText);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resume = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resume = false;
    }

    public void pause() {
        if (textViewRunnable != null) {
            removeCallbacks(textViewRunnable);
        }
        resume = false;
    }

    public void resume() {
        resume = true;
        startAnim();
        if (textViewRunnable == null) {
            textViewRunnable = new Runnable() {
                @Override
                public void run() {
                    if (resume) {
                        if (textArray != null && textArray.length > 1) {
                            textArrayIndex = (textArrayIndex + 1) % textArray.length;
                            mTvIpText.setText(textArray[textArrayIndex]);
                            postDelayed(textViewRunnable, 5000);
                        }
                    }
                }
            };
        }
        if (textArray != null && textArray.length > 1) {
            postDelayed(textViewRunnable, 5000);
        }
    }

    private void startAnim() {
        animate().scaleY(zoomIn ? min : max).scaleX(zoomIn ? min : max).setDuration(800).setInterpolator(new LinearInterpolator())
                .setListener(listener).start();
    }


    private void initView() {

        listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (resume) {
                    zoomIn = !zoomIn;
                    startAnim();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        mIvTextBoxBg = (ImageView) findViewById(R.id.iv_text_box_bg);
        mTvIpText = (TextView) findViewById(R.id.tv_ip_text);
    }
}
