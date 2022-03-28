package com.thfw.ui.widget;

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

import com.thfw.ui.R;


/**
 * Created By jishuaipeng on 2021/5/18
 */
public class TitleView extends FrameLayout {
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvRight;
    private String centerText;
    private String rightText;
    private boolean showIcon;

    public TitleView(@NonNull Context context) {
        this(context, null);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.layout_top_navigation, this);
        initView();
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
            centerText = ta.getString(R.styleable.TitleView_tv_centerText);
            rightText = ta.getString(R.styleable.TitleView_tv_rightText);
            showIcon = ta.getBoolean(R.styleable.TitleView_tv_showIcon, true);
            final Drawable d = ta.getDrawable(R.styleable.TitleView_tv_leftIcon);
            final int colorBg = ta.getColor(R.styleable.TitleView_tv_background, getResources().getColor(R.color.colorPrimary));
            final int leftIconColor = ta.getColor(R.styleable.TitleView_tv_leftIconColor, Color.WHITE);
            final int centerTextColor = ta.getColor(R.styleable.TitleView_tv_centerTextColor, Color.WHITE);
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
            mIvBack.setVisibility(showIcon ? VISIBLE : GONE);

            ta.recycle();
        }
        setRightText(rightText);
        setCenterText(centerText);

    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mTvRight = findViewById(R.id.tv_right);
        mIvBack.setOnClickListener(v -> {
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).onBackPressed();
            }
        });

    }

    public TextView getTvRight() {
        return mTvRight;
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
