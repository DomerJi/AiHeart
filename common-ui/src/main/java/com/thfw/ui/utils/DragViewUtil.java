package com.thfw.ui.utils;

import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import com.thfw.base.utils.LogUtil;
import com.thfw.ui.dialog.TDialog;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * 这个工具可以使任何一个view进行拖动。
 */

public class DragViewUtil {


    /**
     * 拖动View方法
     *
     * @param v view
     */
    public static void registerDragAction(View v, TDialog window, View.OnClickListener listener) {
        v.setOnTouchListener(new TouchListener(window, listener));
    }

    private static class TouchListener implements View.OnTouchListener {
        private TDialog window;
        private float downX;
        private float downY;
        private float moveX;
        private float moveY;
        private int[] start = new int[4];
        private long downTime;
        private boolean isMove;
        private View.OnClickListener listener;

        private TouchListener(TDialog window, View.OnClickListener listener) {
            this.window = window;
            this.listener = listener;
        }


        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    downX = event.getX();
                    downY = event.getY();
                    if (start[0] == 0) {
                        start[0] = view.getLeft();
                        start[1] = view.getTop();
                        start[2] = view.getRight();
                        start[3] = view.getBottom();
                    }
                    isMove = false;
                    downTime = System.currentTimeMillis();
                    LogUtil.d("dragView", "down y = " + downY + " ; x = " + downX);
                    break;
                case MotionEvent.ACTION_MOVE:

                    final float xDistance = event.getX() - downX;
                    final float yDistance = event.getY() - downY;
                    moveX += xDistance;

                    if (xDistance != 0 || yDistance != 0) {
                        int l = (int) (view.getLeft() + xDistance);
                        int r = (int) (l + view.getWidth());
                        int t = (int) (view.getTop() + yDistance);
                        int b = (int) (t + view.getHeight());
//                        view.layout(l, t, r, b);
//                        view.layout(l, t, r, b);
                        view.setLeft(l);
                        view.setRight(r);
                        if (t < 0) {
                            moveY += yDistance;
                            view.setTop(t);
                            view.setBottom(b);
                        }
                        isMove = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isMove = System.currentTimeMillis() - downTime > 350;
                    LogUtil.d("dragView", "moveX = " + moveX + " ; moveY = " + moveY);
                    if (Math.abs(moveX) > view.getWidth() / 5 || Math.abs(moveY) > view.getHeight() / 4) {
                        if (window != null) {
                            Vibrator vibrator = (Vibrator) view.getContext().getSystemService(VIBRATOR_SERVICE);
                            if (vibrator != null) {
                                vibrator.vibrate(120);
                            }
                            window.dismiss();
                        }
                    } else {
                        view.layout(start[0], start[1], start[2], start[3]);
                        if (!isMove) {
                            if (listener != null) {
                                listener.onClick(view);
                            }
                        }
                    }

                    break;
                default:
                    break;
            }
            return isMove;
        }

    }

}