package com.thfw.robotheart.port;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import com.pl.sphelper.ConstantUtil;
import com.pl.sphelper.SPHelper;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.robot.RobotUtil;

import java.util.Random;

public class ShakeNodActivity extends RobotBaseActivity {

    private android.widget.TextView mTvProvider;
    private android.widget.Button mBtShake;
    private android.widget.Button mBtNod;
    private android.widget.Button mBtRotate;

    private int shakeZero = ConstantUtil.DEFAULT_INT;
    private int nodZero = ConstantUtil.DEFAULT_INT;
    private int rotateZero = ConstantUtil.DEFAULT_INT;
    private Button mBtShutdown;


    @Override
    public int getContentView() {
        return R.layout.activity_shake_nod;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTvProvider = (TextView) findViewById(R.id.tv_provider);
        mBtShake = (Button) findViewById(R.id.bt_shake);
        mBtNod = (Button) findViewById(R.id.bt_nod);
        mBtRotate = (Button) findViewById(R.id.bt_rotate);
        mBtShutdown = (Button) findViewById(R.id.bt_shutdown);
    }

    @Override
    public void initData() {
        shakeZero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
        nodZero = SPHelper.getInt(ConstantUtil.Key.NOD_ZERO);
        rotateZero = SPHelper.getInt(ConstantUtil.Key.ROTATE_ZERO);
        mTvProvider.setText("共享信息：\n摇头零位：" + shakeZero + "  点头零位：" + nodZero + "  转身零位：" + rotateZero);
        mTvProvider.setText(mTvProvider.getText().toString()
                + "\nisSystemApp：" + RobotUtil.isSystemApp() + "  isBuildMsg：" + RobotUtil.isBuildMsg());
        mTvProvider.setText(mTvProvider.getText().toString()
                + "  \nproduct: " + Build.PRODUCT + " | device: " + Build.DEVICE + " | brand: " + Build.BRAND + " | model: " + Build.MODEL);
        mBtShake.setOnClickListener(v -> {
            if (!SerialManager.getInstance().isOpen()) {
                ToastUtil.show("串口打开失败");
                return;
            }
            shakeZero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
            if (ConstantUtil.isDefault(shakeZero)) {
                ToastUtil.show("请先获取摇头零位");
                return;
            }
            shakeZero = shakeZero + mShakeZeroOffset;
            runCount = count;
            send();

        });

        mBtNod.setOnClickListener(v -> {

            if (!SerialManager.getInstance().isOpen()) {
                ToastUtil.show("串口打开失败");
                return;
            }
            nodZero = SPHelper.getInt(ConstantUtil.Key.NOD_ZERO);
            if (ConstantUtil.isDefault(nodZero)) {
                ToastUtil.show("请先获取点头零位");
                return;
            }
            nodZero = nodZero + mNodZeroOffset;
            runCountNod = countNod;
            isBottom = true;
            sendNod();
        });


        mBtRotate.setOnClickListener(v -> {

            if (!SerialManager.getInstance().isOpen()) {
                ToastUtil.show("串口打开失败");
                return;
            }

            int order = Order.DOWN_SERVO_STATE;
            SerialManager.getInstance().send(order, new int[]{3, new Random().nextInt(1800), 2000});
        });
        mBtShutdown.setOnClickListener(v -> {
//            RobotUtil.shutdownByBroadcast(mContext);
//            RobotUtil.shutdownByActivity(mContext);
            RobotUtil.shutdownByInvoke();
        });
        init();
    }

    private void init() {
        mHandler = new Handler(Looper.getMainLooper());
        mHandlerNod = new Handler(Looper.getMainLooper());
        left = 360;
        right = 360;
        time = 2000;
        count = 1;

        leftNod = 140;
        rightNod = 260;
        timeNod = 2000;
        countNod = 1;
    }

    /**
     * 点头舵机：（中心角度 = 零位校准角度 - 10）
     * 上位置，中心角度 - 14；下位置，中心角度 + 26
     * 速度：2000ms
     * 摇头舵机：（中心角度 = 零位校准角度 - 0）
     * 左位置，中心角度 - 36；下位置，中心角度 + 36
     * 速度：2000ms
     */

    private Runnable actionRunnable;
    private Runnable actionNodRunnable;
    private int mShakeZeroOffset = 0;
    private int mNodZeroOffset = -100;

    private int left;
    private int right;
    private int time;
    private int count;

    private int leftNod;
    private int rightNod;
    private int timeNod;
    private int countNod;

    private int runCount = 0;
    private int runCountNod = 0;
    private boolean isRight = true;
    private boolean isBottom = true;
    private Handler mHandler;
    private Handler mHandlerNod;

    public void send() {
        int order = Order.DOWN_SERVO_STATE;
        int[] params;
        if (!SerialManager.getInstance().isOpen()) {
            ToastUtil.show("串口未开启");
        }
        if (actionRunnable != null) {
            mHandler.removeCallbacks(actionRunnable);
        }
        if (runCount == count) {
            // 右
            params = new int[]{2, shakeZero + right, time / 2};
        } else if (runCount == -1) {
            // 归零
            isRight = !isRight;
            params = new int[]{2, shakeZero, time / 2};
        } else {
            // 左or右
            isRight = !isRight;
            params = new int[]{2, isRight ? shakeZero + right : shakeZero - left, time};
        }
        SerialManager.getInstance().send(order, params);
        if (runCount > -1) {
            actionRunnable = () -> {
                runCount--;
                send();
            };
            mHandler.postDelayed(actionRunnable, params[2] + 100);
        }
    }

    public void sendNod() {
        int order = Order.DOWN_SERVO_STATE;
        int[] params;
        if (!SerialManager.getInstance().isOpen()) {
            ToastUtil.show("串口未开启");
        }
        if (actionNodRunnable != null) {
            mHandlerNod.removeCallbacks(actionNodRunnable);
        }
        if (runCountNod == countNod) {
            // 上
            params = new int[]{1, nodZero + rightNod, timeNod / 2};
        } else if (runCountNod == -1) {
            // 归零
            isBottom = !isBottom;
            params = new int[]{1, nodZero, timeNod / 2};
        } else {
            // 下or上
            isBottom = !isBottom;
            params = new int[]{1, isBottom ? nodZero + rightNod : nodZero - leftNod, timeNod};
        }
        SerialManager.getInstance().send(order, params);
        if (runCountNod > -1) {
            actionNodRunnable = () -> {
                runCountNod--;
                sendNod();
            };
            mHandlerNod.postDelayed(actionNodRunnable, params[2] + 100);
        }
    }
}