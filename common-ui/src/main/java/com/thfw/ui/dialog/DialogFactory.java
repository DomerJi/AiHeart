package com.thfw.ui.dialog;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.ui.R;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnBindViewListener;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.widget.InputBoxView;

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
                .setLayoutRes(R.layout.dialog_custom_layout)
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

    /**
     * 交易密码
     *
     * @param activity
     * @param onInputCompleteListener
     * @return
     */
    public static TDialog createTransactionPasswordDialog(FragmentActivity activity, InputBoxView.OnInputCompleteListener onInputCompleteListener) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_transaction_password_layout)
                .setGravity(Gravity.BOTTOM)
                .setScreenWidthAspect(activity, 1)
                .addOnClickListener(R.id.iv_close)
                .setOnBindViewListener(new OnBindViewListener() {
                    @Override
                    public void bindView(BindViewHolder viewHolder) {
                        InputBoxView inputBoxView = viewHolder.getView(R.id.ibv_transaction);
                        inputBoxView.setOnInputCompleteListener(onInputCompleteListener);
                    }
                })
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        tDialog.dismiss();
                    }
                }).create().show();

    }

    /**
     * 手机验证码
     *
     * @param activity
     * @param onInputCompleteListener
     * @return
     */
    public static TDialog createVerificationCodeDialog(FragmentActivity activity, InputBoxView.OnInputCompleteListener onInputCompleteListener) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_verification_code_layout)
                .setGravity(Gravity.BOTTOM)
                .setScreenWidthAspect(activity, 1)
                .addOnClickListener(R.id.iv_close)
                .setOnBindViewListener(new OnBindViewListener() {
                    @Override
                    public void bindView(BindViewHolder viewHolder) {
                        InputBoxView inputBoxView = viewHolder.getView(R.id.ibv_transaction);
                        inputBoxView.setOnInputCompleteListener(onInputCompleteListener);
                    }
                })
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        tDialog.dismiss();
                    }
                }).create().show();

    }

    /**
     * 商品筛选
     *
     * @param activity
     * @return
     */
    public static TDialog createScreenDialog(FragmentActivity activity) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_screen_layout)
                .setDialogAnimationRes(R.style.animate_dialog_right)
                .setGravity(Gravity.RIGHT)
                .setScreenHeightAspect(activity, 1f)
                .setScreenWidthAspect(activity, 0.8f)
                .addOnClickListener(R.id.bt_reset, R.id.bt_confirm)
                .setOnBindViewListener(new OnBindViewListener() {
                    @Override
                    public void bindView(BindViewHolder viewHolder) {
                        RecyclerView rvScreen = viewHolder.getView(R.id.rv_screen);
                    }
                })
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        if (view.getId() == R.id.bt_confirm) {
                            tDialog.dismiss();
                        }
                    }
                }).create().show();

    }

    public interface OnViewCallBack extends OnViewClickListener {
        void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical);
    }


}
