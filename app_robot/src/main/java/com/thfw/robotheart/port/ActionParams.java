package com.thfw.robotheart.port;

import android.os.Handler;
import android.os.Looper;

import com.pl.sphelper.ConstantUtil;
import com.pl.sphelper.SPHelper;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.SharePreferenceUtil;

import java.util.Arrays;

/**
 * Author:pengs
 * Date: 2022/6/11 13:06
 * Describe:机器人动作 参数配置
 */
public class ActionParams {

    public static final int ONE_ANGLE_TIME = 30;
    public static final int ONE_ANGLE_TIME2 = 20;
    public static final int ROTATE_ANGLE = 30;
    public static final int SHAKE_ANGLE = 36;
    public static final int NOD_UP_ANGLE = 26;
    public static final int NOD_DOWN_ANGLE = 14;

    public static final String KEY_ROTATE_ANGLE = "key.rotate.angle";
    public static final String KEY_SHAKE_ANGLE = "key.shake.angle";
    // 低头
    public static final String KEY_NOD_UP_ANGLE = "key.nod.up.angle";
    // 抬头
    public static final String KEY_NOD_DOWN_ANGLE = "key.nod.down.angle";
    public static final String KEY_ROTATE_TIME = "key.rotate.time";
    public static final String KEY_SHAKE_TIME = "key.shake.time";
    public static final String KEY_NOD_TIME = "key.nod.time";

    public int getOneAngleTime() {
        switch (controlOrder) {
            case ControlOrder.ROTATE:
                return oneRotateTimeMs;
            case ControlOrder.SHAKE:
                return SharePreferenceUtil.getInt(KEY_SHAKE_TIME, ONE_ANGLE_TIME2);
            case ControlOrder.NOD:
                return SharePreferenceUtil.getInt(KEY_NOD_TIME, ONE_ANGLE_TIME2);
        }
        return ONE_ANGLE_TIME;
    }


    /**
     * leftNod = 140;
     * rightNod = 260;
     * timeNod = 2000;
     * countNod = 1;
     *
     * @return
     */
    public static ActionParams getNormalNod() {
        int right = SharePreferenceUtil.getInt(KEY_NOD_UP_ANGLE, NOD_UP_ANGLE) * 10;
        int left = SharePreferenceUtil.getInt(KEY_NOD_DOWN_ANGLE, NOD_DOWN_ANGLE) * 10;
        return new ActionParams(ControlOrder.NOD)
                .setRight(right)
                .setLeft(left)
                .setRepeatCount(1);

    }

    /**
     * left = 360;
     * right = 360;
     * time = 2000;
     * count = 1;
     *
     * @return
     */
    public static ActionParams getNormalShake() {
        int right = SharePreferenceUtil.getInt(KEY_SHAKE_ANGLE, SHAKE_ANGLE) * 10;
        return new ActionParams(ControlOrder.SHAKE)
                .setRight(right)
                .setLeft(right)
                .setRepeatCount(1);
    }

    // =========================  旋转相关【开始】  ==============================
    // 时间旋转 一共360度 没 一度 需要时间
    private int oneRotateTimeMs = ONE_ANGLE_TIME;
    private int[] angles;
    private int anglesIndex = 0;

    public static ActionParams getNormalRotate(int... angles) {
        int time = SharePreferenceUtil.getInt(KEY_ROTATE_TIME, ONE_ANGLE_TIME);
        if (EmptyUtil.isEmpty(angles)) {
            int angle = SharePreferenceUtil.getInt(KEY_ROTATE_ANGLE, ROTATE_ANGLE);
            return new ActionParams(ControlOrder.ROTATE)
                    .setAngles(angle, -(angle * 2), angle)
                    .setOneRotateTimeMs(time);
        }
        return new ActionParams(ControlOrder.ROTATE)
                .setAngles(angles)
                .setOneRotateTimeMs(time);
    }

    /**
     * 仅限转身调用
     *
     * @return
     */
    public int getAngle() {
        return angles[anglesIndex] * 30;
    }

    public void nextRotateIndex() {
        anglesIndex++;
    }

    public boolean nextRotate() {
        if (angles == null || angles.length == 0) {
            return false;
        }
        return anglesIndex < angles.length;
    }

    public ActionParams setAngles(int... angles) {
        this.angles = angles;
        return this;
    }

    public ActionParams setOneRotateTimeMs(int oneRotateTimeMs) {
        this.oneRotateTimeMs = oneRotateTimeMs;
        return this;
    }

    // =========================  旋转相关【结束】  ==============================


    /**
     * 先从右开始
     */
    private boolean beginRight;
    private Handler mHandler;
    private Runnable mRunnable;

    private int runCount;

    public boolean isRight() {
        return beginRight;
    }

    public boolean reverseRight() {
        beginRight = !beginRight;
        return beginRight;
    }

    public int getRunCount() {
        return runCount;
    }

    public void runCountChange() {
        runCount--;
    }

    public void setRunnable(Runnable mRunnable) {
        this.mRunnable = mRunnable;
    }

    public Runnable getRunnable() {
        return mRunnable;
    }

    private int controlOrder;
    private int zero = ConstantUtil.DEFAULT_INT;
    // 零位并不是 视觉上的中心点所以加偏移量
    private int offset;
    private int repeatCount = 1;
    // 时间
    private int timeMs;

    private int left;
    private int right;


    public ActionParams setLeft(int left) {
        this.left = left;
        return this;
    }

    public ActionParams setRight(int right) {
        this.right = right;
        return this;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public ActionParams setRepeatCount(int repeatCount) {
        if (repeatCount > 0) {
            this.repeatCount = repeatCount;
            this.runCount = repeatCount;
        }
        return this;
    }

    public int getControlOrder() {
        return controlOrder;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public boolean canUse() {
        return zero != ConstantUtil.DEFAULT_INT;
    }

    public int getCenterPoint() {
        return zero + offset;
    }

    public ActionParams setTimeMs(int timeMs) {
        this.timeMs = timeMs;
        return this;
    }

    public int getTimeMs() {
        switch (controlOrder) {
            case ControlOrder.ROTATE:
                return Math.abs(angles[anglesIndex]) * oneRotateTimeMs;
        }

        return timeMs;
    }

    public ActionParams(int controlOrder) {
        this.controlOrder = controlOrder;
        mHandler = new Handler(Looper.getMainLooper());
        beginRight = true;
        switch (controlOrder) {
            case ControlOrder.NOD:
                zero = SPHelper.getInt(ConstantUtil.Key.NOD_ZERO);
                offset = -100;
                break;
            case ControlOrder.SHAKE:
                zero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
                break;
            case ControlOrder.ROTATE:
                zero = SPHelper.getInt(ConstantUtil.Key.ROTATE_ZERO);
                break;
        }
    }


    /**
     * 1点头 2摇头 3转身
     */
    public interface ControlOrder {
        int NOD = 1;
        int SHAKE = 2;
        int ROTATE = 3;
    }

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public String toString() {
        if (controlOrder == ControlOrder.ROTATE) {
            return "ActionParams{" +
                    "controlOrder=" + controlOrder +
                    "oneRotateTimeMs=" + oneRotateTimeMs +
                    ", angles=" + Arrays.toString(angles) +
                    ", anglesIndex=" + anglesIndex +
                    ", mHandler=" + mHandler +
                    ", mRunnable=" + mRunnable +
                    ", zero=" + zero +
                    '}';
        } else {
            return "ActionParams{" +
                    "controlOrder=" + controlOrder +
                    ", runCount=" + runCount +
                    ", zero=" + zero +
                    ", offset=" + offset +
                    ", repeatCount=" + repeatCount +
                    ", timeMs=" + timeMs +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }
}
