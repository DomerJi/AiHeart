package com.thfw.base.role;

import android.view.View;

import androidx.annotation.IdRes;

public interface DisplayLimits {

    boolean isVisible(@IdRes int id);

    boolean setVisible(View view);
}
