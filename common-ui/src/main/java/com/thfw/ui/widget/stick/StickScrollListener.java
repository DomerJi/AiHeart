package com.thfw.ui.widget.stick;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * Created By jishuaipeng on 2021/5/13
 */
public abstract class StickScrollListener<T extends ICart> extends RecyclerView.OnScrollListener {

    private View stickHead;
    private int stickHeadHeight;
    private LinearLayoutManager linearLayoutManager;
    private int mCurrentPosition = 0;
    private List<T> items;

    public StickScrollListener(View stickHead, List<T> items) {
        this.stickHead = stickHead;
        this.items = items;
        updateStickHeadView();
    }

    /**
     * RecyclerView.canScrollVertically(1)的值表示是否能向下滚动，false表示已经滚动到底部
     * RecyclerView.canScrollVertically(-1)的值表示是否能向上滚动，false表示已经滚动到顶部
     *
     * @param recyclerView
     * @param dx
     * @param dy
     */
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (stickHead == null) {
            return;
        }
        if (linearLayoutManager == null) {
            linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        }

        stickHeadHeight = stickHead.getHeight();

        int firstVisPos = linearLayoutManager.findFirstVisibleItemPosition();
        ICart firstVisibleItem = items.get(firstVisPos);
        ICart nextItem = items.get(firstVisPos + 1);
        View nextView = linearLayoutManager.findViewByPosition(firstVisPos + 1);

        if (dy > 0) {
            if (nextItem.isGroup()) {


                if (nextView.getTop() <= stickHeadHeight) {
                    //被顶掉的效果
                    stickHead.setY(-(stickHeadHeight - nextView.getTop()));
                } else {
                    stickHead.setY(0);
                }
            }

            // 判断是否需要更新悬浮条
            if (mCurrentPosition != firstVisPos && firstVisibleItem.isGroup()) {
                mCurrentPosition = firstVisPos;
                //更新悬浮条
                updateStickHeadView();
                stickHead.setY(0);
            }
        } else {
            // 1、nextItem -> Post and firstVisibleItem -> CommentEntity       mCurrentPosition = ((Comment) firstVisibleItem).getParentPostPosition()
            // 2、nextItem -> Post and firstVisibleItem -> HeadEntity          mCurrentPosition = firstVisPos
            // 3、nextItem -> Comment and firstVisibleItem -> CommentEntity    stickHead 不动
            // 4、nextItem -> Comment and firstVisibleItem -> HeadEntity       stickHead 不动
            if (nextItem.isGroup()) {
                mCurrentPosition = firstVisibleItem.isGroup() ? firstVisPos : firstVisibleItem.getGroupPosition();
                updateStickHeadView();

                if (nextView.getTop() <= stickHeadHeight) {
                    // 被顶掉的效果
                    stickHead.setY(-(stickHeadHeight - nextView.getTop()));
                } else {
                    stickHead.setY(0);
                }
            }
        }
    }

    public abstract void updateStickHeadView(int position);

    public void updateStickHeadView() {
        updateStickHeadView(mCurrentPosition);
    }


}
