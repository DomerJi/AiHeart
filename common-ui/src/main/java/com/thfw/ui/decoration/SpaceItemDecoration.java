package com.thfw.ui.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "SpaceItemDecoration";

    private int left;
    private int top;
    private int right;
    private int bottom;
    private int firstLeft;
    private int firstTop;
    private int lastRight;
    private int lastBottom;

    /**
     * 设置Item的四周边距
     */
    public SpaceItemDecoration(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * 设置第一个Item的左边距（一般适用于横向列表）
     */
    public SpaceItemDecoration setFirstLeft(int firstLeft) {
        this.firstLeft = firstLeft;
        return this;
    }

    /**
     * 设置第一个Item的顶边距（一般适用于竖向列表）
     */
    public SpaceItemDecoration setFirstTop(int firstTop) {
        this.firstTop = firstTop;
        return this;
    }

    /**
     * 设置最后一个Item的右边距（一般适用于横向列表）
     */
    public SpaceItemDecoration setLastRight(int lastRight) {
        this.lastRight = lastRight;
        return this;
    }

    /**
     * 设置最后一个Item的底边距（一般适用于竖向列表）
     */
    public SpaceItemDecoration setLastBottom(int lastBottom) {
        this.lastBottom = lastBottom;
        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view); // 所处列表的位置
        int setLeft = left;
        int setTop = top;
        int setRight = right;
        int setBottom = bottom;

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        // 列表Item总数量
        int itemCount = parent.getAdapter() != null ? parent.getAdapter().getItemCount() : -1;

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

            // 此网格布局最大的行数（横向列表）/ 列数（竖向列表）
            int spanCount = gridLayoutManager.getSpanCount();

            // 第几列（横向列表）/ 第几行（竖向列表）
            int spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount);

            if (spanGroupIndex == 0) {
                // 首列Item（横向列表）或首行Item（竖向列表）
                if (firstLeft > 0) {
                    setLeft = firstLeft;
                }
                if (firstTop > 0) {
                    setTop = firstTop;
                }

            } else if (spanCount > 0 && spanGroupIndex == Math.ceil(itemCount / (float) spanCount) - 1) {
                // 尾列Item（横向列表）或尾行Item（竖向列表）
                if (lastRight > 0) {
                    setRight = lastRight;
                }
                if (lastBottom > 0) {
                    setBottom = lastBottom;
                }
            }

        } else if (layoutManager instanceof LinearLayoutManager) {
            if (position == 0) {
                // 头部Item
                if (firstLeft > 0) {
                    setLeft = firstLeft;
                }
                if (firstTop > 0) {
                    setTop = firstTop;
                }

            } else if (position == itemCount - 1) {
                // 尾部Item
                if (lastRight > 0) {
                    setRight = lastRight;
                }
                if (lastBottom > 0) {
                    setBottom = lastBottom;
                }
            }
        }

//        LogUtil.i(TAG, "setRect position=" + position + ", left=" + setLeft + ", top=" + setTop
//                + ", right=" + setRight + ", bottom=" + setBottom);

        outRect.set(setLeft, setTop, setRight, setBottom);
    }
}