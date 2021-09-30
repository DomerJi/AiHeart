package com.thfw.ui.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleItemSpaceView extends RecyclerView.ItemDecoration {

    private int leftRight;
    private int topBottom;
    private int lastBottom = -1;

    // leftRight为横向间的距离 topBottom为纵向间距离
    public RecycleItemSpaceView(int leftRight, int topBottom) {
        this.leftRight = leftRight;
        this.topBottom = topBottom;
    }

    public void setLastBottom(int lastBottom) {
        this.lastBottom = lastBottom;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int pos = parent.getChildLayoutPosition(view);  // 当前条目的position
        int itemCount = state.getItemCount();           // 最后一条的postion
        // 竖直方向的
        if (layoutManager.getOrientation() == RecyclerView.VERTICAL) {
            // 最后一项需要 bottom
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.bottom = topBottom;
                if (lastBottom >= 0) {
                    outRect.bottom = lastBottom;
                }
            }
            outRect.top = topBottom;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            }
            outRect.left = leftRight;
            outRect.right = leftRight;
        } else {
            if (pos == 0) {
                outRect.left = 0;
            } else {
                outRect.left = leftRight;
            }
            outRect.top = topBottom;
            outRect.bottom = topBottom;
        }
    }
}
