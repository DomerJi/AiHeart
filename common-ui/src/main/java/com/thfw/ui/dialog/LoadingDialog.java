package com.thfw.ui.dialog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.thfw.base.utils.Util;
import com.thfw.ui.R;


/**
 * Created By jishuaipeng on 2021/6/3
 */
public class LoadingDialog {

    public static final long NO_DELAYMILLIS = -1;
    public static final long DELAYMILLIS = 1500;
    private static TDialog tDialog;
    private static TextView mTvLoading;
    private static Handler handler;

    public static void show(FragmentActivity activity) {
        show(activity, null);
    }

    public static void show(FragmentActivity activity, String tip) {
        show(activity, tip, NO_DELAYMILLIS);
    }

    public static void show(FragmentActivity activity, String tip, long delayMillis) {
        if (tDialog == null || !tDialog.isVisible() || mTvLoading == null) {
            hide();
            TDialog.Builder builder = new TDialog.Builder(activity.getSupportFragmentManager())
                    .setLayoutRes(R.layout.dialog_loading)
                    .setDialogAnimationRes(R.style.animate_dialog_fade)
                    .setGravity(Gravity.CENTER)
                    .setWidth(Util.isPad(activity) ? 220 : 300)
                    .setCancelableOutside(false)
                    .setOnBindViewListener(viewHolder -> {
                        mTvLoading = viewHolder.getView(R.id.tv_loading);
                        if (mTvLoading != null && !TextUtils.isEmpty(tip)) {
                            mTvLoading.setVisibility(View.VISIBLE);
                            mTvLoading.setText(tip);
                        } else {
                            mTvLoading.setVisibility(View.GONE);
                        }
                    });
            tDialog = builder.create().show();
        } else {
            if (mTvLoading != null && !TextUtils.isEmpty(tip)) {
                mTvLoading.setVisibility(View.VISIBLE);
                mTvLoading.setText(tip);
            } else {
                mTvLoading.setVisibility(View.GONE);
            }
        }
        if (delayMillis > 0) {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        hide();
                    }
                };

            }
            handler.sendEmptyMessageDelayed(0, delayMillis);
        } else {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
        }

    }

    public static void hide() {
        if (tDialog != null) {
            tDialog.dismiss();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        mTvLoading = null;
    }
}
