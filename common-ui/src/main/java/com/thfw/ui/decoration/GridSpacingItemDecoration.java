package com.thfw.ui.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount; // 列数
    private int spacingH; // 水平间隔
    private int spacingV; // 竖直间隔
    private boolean includeEdge; // 是否包含边缘

    public GridSpacingItemDecoration(int spanCount, int spacingH, int spacingV, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacingH = spacingH;
        this.spacingV = spacingV;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        // 这里是关键，需要根据你有几列来判断
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            // spacingH - column * ((1f / spanCount) * spacingH)
            outRect.left = spacingH - column * spacingH / spanCount;
            outRect.right = (column + 1) * spacingH / spanCount; // (column + 1) * ((1f / spanCount) * spacingH)

            if (position < spanCount) { // top edge
                outRect.top = spacingH;
            }
            outRect.bottom = spacingV; // item bottom
        } else {
            outRect.left = column * spacingH / spanCount; // column * ((1f / spanCount) * spacingH)
            // spacingH - (column + 1) * ((1f /    spanCount) * spacingH)
            outRect.right = spacingH - (column + 1) * spacingH / spanCount;
            if (position >= spanCount) {
                outRect.top = spacingV; // item top
            }
        }
    }
}