package com.thfw.robotheart.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.thfw.robotheart.R;


/**
 * Created By jishuaipeng on 2021/5/18
 */
public class TitleRobotView extends FrameLayout {
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvRight;
    private TextView mTvLeft;
    private String centerText;
    private String rightText;
    private boolean showIcon;
    private View mLlBack;
    private boolean showTitleBar;
    private TitleBarView mTitleBarView;

    public TitleRobotView(@NonNull Context context) {
        this(context, null);
    }

    public TitleRobotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TitleRobotView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.layout_robot_top_navigation, this);
        initView();
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleRobotView);
            centerText = ta.getString(R.styleable.TitleRobotView_trv_centerText);
            rightText = ta.getString(R.styleable.TitleRobotView_trv_rightText);
            showIcon = ta.getBoolean(R.styleable.TitleRobotView_trv_showIcon, true);
            showTitleBar = ta.getBoolean(R.styleable.TitleRobotView_trv_show_titleBar, true);
            final Drawable d = ta.getDrawable(R.styleable.TitleRobotView_trv_leftIcon);
            final int colorBg = ta.getColor(R.styleable.TitleRobotView_trv_background, getResources().getColor(R.color.transparent));
            final int leftIconColor = ta.getColor(R.styleable.TitleRobotView_trv_leftIconColor, getResources().getColor(R.color.colorRobotFore));
            final int centerTextColor = ta.getColor(R.styleable.TitleRobotView_trv_centerTextColor, getResources().getColor(R.color.colorRobotFore));
            if (d != null) {
                mIvBack.setImageDrawable(d);
            } else {
                if (leftIconColor == Color.WHITE) {
                    mIvBack.setImageResource(R.drawable.navigation_back_selector);
                } else {
                    mIvBack.setColorFilter(leftIconColor);
                }
            }
            view.setBackgroundColor(colorBg);
            mTvTitle.setTextColor(centerTextColor);
            mTvLeft.setTextColor(centerTextColor);
            mTvRight.setTextColor(centerTextColor);
            mIvBack.setVisibility(showIcon ? VISIBLE : GONE);
            if (showTitleBar) {
                mTitleBarView.setVisibility(VISIBLE);
            }
            ta.recycle();
        }
        setRightText(rightText);
        setCenterText(centerText);

    }

    public void showTitleBarView(boolean showTitleBar) {
        mTitleBarView.setVisibility(showTitleBar ? VISIBLE : GONE);
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mLlBack = findViewById(R.id.ll_back);
        mTvTitle = findViewById(R.id.tv_title);
        mTvRight = findViewById(R.id.tv_right);
        mTvLeft = findViewById(R.id.tv_left);
        mTitleBarView = findViewById(R.id.trv_titleBarView);
        mLlBack.setOnClickListener(v -> {
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).onBackPressed();
            }
        });

    }


    public ImageView getIvBack() {
        return mIvBack;
    }

    public void setRightOnClickListener(OnClickListener onClickListener) {
        if (mTvRight != null && onClickListener != null) {
            mTvRight.setOnClickListener(onClickListener);
        }
    }

    public void setBackIcon(@DrawableRes int resId) {
        if (this.mIvBack != null) {
            this.mIvBack.setImageResource(resId);
        }
    }

    public String getCenterText() {
        return centerText;
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }

    public void setCenterText(@StringRes int rightText) {
        setCenterText(getResources().getString(rightText));
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
        if (!TextUtils.isEmpty(centerText)) {
            mTvTitle.setText(centerText);
        }
    }

    public int getCenterTextColor() {
        if (mTvTitle != null) {
            return mTvTitle.getCurrentTextColor();
        }
        return -1;
    }

    public void setCenterTextColor(@ColorInt int color) {
        if (mTvTitle != null) {
            mTvTitle.setTextColor(color);
        }
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(@StringRes int rightText) {
        setRightText(getResources().getString(rightText));
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
        if (!TextUtils.isEmpty(rightText)) {
            mTvRight.setVisibility(VISIBLE);
            mTvRight.setText(rightText);
        }
    }
}
