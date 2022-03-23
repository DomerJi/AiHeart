package com.thfw.util;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.export_ym.R;
import com.thfw.dialog.TDialog;
import com.thfw.dialog.listener.OnViewClickListener;

/**
 * 弹框工厂
 */
public class DialogFactory {

    /**
     * 通用弹框
     *
     * @param activity
     * @param onViewCallBack
     * @return
     */
    public static TDialog createCustomDialog(FragmentActivity activity, OnViewCallBack onViewCallBack) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_custom_layout_ym)
                .setDialogAnimationRes(R.style.animate_dialog_fade)
                .addOnClickListener(R.id.tv_left, R.id.tv_right)
                // R.id.tv_title, R.id.tv_hint, R.id.tv_left, R.id.tv_right
                .setOnBindViewListener(viewHolder -> {
                    TextView mTvTitle = viewHolder.getView(R.id.tv_title);
                    TextView mTvHint = viewHolder.getView(R.id.tv_hint);
                    TextView mTvLeft = viewHolder.getView(R.id.tv_left);
                    TextView mTvRight = viewHolder.getView(R.id.tv_right);
                    View mVLineVertical = viewHolder.getView(R.id.vline_vertical);
                    onViewCallBack.callBack(mTvTitle, mTvHint, mTvLeft, mTvRight, mVLineVertical);
                })
                .setOnViewClickListener(onViewCallBack).create().show();
    }


    public interface OnViewCallBack extends OnViewClickListener {
        void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical);
    }



}
