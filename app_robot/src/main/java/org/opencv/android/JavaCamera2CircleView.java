package org.opencv.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;

/**
 * Author:pengs
 * Date: 2022/1/4 11:39
 * Describe:Todo
 */
public class JavaCamera2CircleView extends JavaCamera2View{

    public JavaCamera2CircleView(Context context, int cameraId) {
        super(context, cameraId);
        init();
    }

    public JavaCamera2CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    /**
     * 半径
     */
    private int radius;

    /**
     * 中心点坐标
     */
    private Point centerPoint;

    /**
     * 剪切路径
     */
    private Path clipPath;

    /**
     * 是否在预览
     */
    private boolean isPreviewing;

    /**
     * 是否已经设置过窗口尺寸
     */
    private boolean isSizeFitted = false;

    /**
     * 初始化
     */
    private void init() {
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        getHolder().addCallback(this);
        clipPath = new Path();
        centerPoint = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 坐标转换为实际像素
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 计算出圆形的中心点
        centerPoint.x = widthSize >> 1;
        centerPoint.y = heightSize >> 1;
        // 计算出最短的边的一半作为半径
        radius = (centerPoint.x > centerPoint.y) ? centerPoint.y : centerPoint.x;
//        Log.i(TAG, "onMeasure: " + centerPoint.toString() + "radius = " + radius);
        clipPath.reset();
        clipPath.addCircle(centerPoint.x, centerPoint.y, radius, Path.Direction.CCW);
        setMeasuredDimension(widthSize, heightSize);
    }

    public int getRadius() {
        return radius;
    }

//    /**
//     * 绘制
//     *
//     * @param canvas 画布
//     */
    @Override
    public void draw(Canvas canvas) {
        //裁剪画布，并设置其填充方式
        if (Build.VERSION.SDK_INT >= 26) {
            canvas.clipPath(clipPath);
        } else {
            canvas.clipPath(clipPath, Region.Op.REPLACE);
        }
        super.draw(canvas);
    }

}
