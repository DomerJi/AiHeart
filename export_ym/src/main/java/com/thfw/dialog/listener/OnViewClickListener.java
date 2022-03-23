package com.thfw.dialog.listener;

import android.view.View;

import com.thfw.dialog.TDialog;
import com.thfw.dialog.base.BindViewHolder;


public interface OnViewClickListener {
    void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog);
}
