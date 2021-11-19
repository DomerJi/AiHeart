package com.thfw.robotheart.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ui.PlayerView;

public class CustomPlayerView extends PlayerView {

    public CustomPlayerView(Context context) {
        this(context, null);
    }

    public CustomPlayerView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPlayerView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
