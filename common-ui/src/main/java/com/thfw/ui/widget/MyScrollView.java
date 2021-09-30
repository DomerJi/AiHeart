package com.thfw.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created By jishuaipeng on 2021/5/27
 */
public class MyScrollView extends ScrollView {
    private OnScrollChangedListener onScrollChangedListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        // l oldl 分别代表水平位移
        // t oldt 代表当前左上角距离Scrollview顶点的距离
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScroll(l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    public interface OnScrollChangedListener {
        void onScroll(int l, int t, int oldl, int oldt);
    }
}
