package com.thfw.ui.dialog.listener;

import android.view.View;

import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;


public interface OnViewClickListener {
    void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog);
}
