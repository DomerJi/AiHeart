package com.thfw.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thfw.base.utils.Util;
import com.thfw.ui.R;

/**
 * Author:pengs
 * Date: 2022/12/31 16:41
 * Describe:Todo
 */
public class OrderView extends FrameLayout {
    private View view;
    private TextView mTvOrder;

    public OrderView(@NonNull Context context) {
        this(context, null);
    }

    public OrderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public OrderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        view = inflate(context, R.layout.layout_order_view, this);
        mTvOrder = findViewById(R.id.tv_order);
//        if (!DeviceUtil.isLhXk_OS_R_SD01B()) {
//            setVisibility(GONE);
//            return;
//        }
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OrderView);
            final int orderBgColor = ta.getColor(R.styleable.OrderView_order_bg_color, -1);
            final int orderTextColor = ta.getColor(R.styleable.OrderView_order_bg_color, getResources().getColor(R.color.gold));
            final float orderTextSize = ta.getDimensionPixelSize(R.styleable.OrderView_order_text_size, Util.spToPx(14, context));
            mTvOrder.setTextColor(orderTextColor);
            mTvOrder.setTextSize(orderTextSize);
            if (orderBgColor != -1) {
                mTvOrder.setBackgroundColor(orderBgColor);
            } else {
                final Drawable d = ta.getDrawable(R.styleable.OrderView_order_bg_src);
                if (d != null) {
                    mTvOrder.setBackground(d);
                }
            }

            ta.recycle();
        }

    }


    public void setOrder(int order) {
        mTvOrder.setText(String.valueOf(order));
    }

}
