package com.thfw.ui.widget;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.scwang.wave.MultiWaveHeader;
import com.thfw.ui.R;

/**
 * Author:pengs
 * Date: 2022/1/12 14:24
 * Describe:Todo
 */
public class SpeechTextView extends ConstraintLayout {

    private TextView mTvSpeechText;
    private MultiWaveHeader mWaveHeader;

    public SpeechTextView(Context context) {
        this(context, null);
    }

    public SpeechTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SpeechTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.layout_speech_text_view, this);
        mTvSpeechText = (TextView) findViewById(R.id.tv_speech_text);
        mWaveHeader = (MultiWaveHeader) findViewById(R.id.waveHeader);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        mWaveHeader.start();
    }

    public void setSpeechText(String text) {
        if (mTvSpeechText != null) {
            mTvSpeechText.setText(text);
        }
    }

    public String getText() {
        if (mTvSpeechText != null) {
            return mTvSpeechText.getText().toString();
        }
        return "";
    }

    public void show() {
        if (getVisibility() == VISIBLE) {
            return;
        }
        mTvSpeechText.setText("");
        setAlpha(0f);
        setVisibility(VISIBLE);
        if (!mWaveHeader.isRunning()) {
            mWaveHeader.start();
        }
        animate().alpha(1f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void hide() {
        if (getVisibility() == GONE) {
            return;
        }
        setSpeechText("");
        if (mWaveHeader.isRunning()) {
            mWaveHeader.stop();
        }
        animate().alpha(0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }
}
