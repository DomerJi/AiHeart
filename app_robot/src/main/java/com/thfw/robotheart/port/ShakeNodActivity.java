package com.thfw.robotheart.port;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.pl.sphelper.ConstantUtil;
import com.pl.sphelper.SPHelper;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.robot.RobotUtil;

import java.io.IOException;
import java.util.Random;

public class ShakeNodActivity extends RobotBaseActivity {

    private android.widget.TextView mTvProvider;
    private android.widget.Button mBtShake;
    private android.widget.Button mBtNod;
    private android.widget.Button mBtRotate;

    private int shakeZero = ConstantUtil.DEFAULT_INT;
    private int nodZero = ConstantUtil.DEFAULT_INT;
    private SerialHelper serialHelper;


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
    }

    @Override
    public void initData() {
        shakeZero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
        nodZero = SPHelper.getInt(ConstantUtil.Key.NOD_ZERO);
        mTvProvider.setText("共享信息：\n摇头零位：" + shakeZero + "  点头零位：" + nodZero);
        mTvProvider.setText(mTvProvider.getText().toString()
                + "\nisSystemApp：" + RobotUtil.isSystemApp() + "  isBuildMsg：" + RobotUtil.isBuildMsg());
        mTvProvider.setText(mTvProvider.getText().toString()
                + "  \nproduct: " + Build.PRODUCT + " | device: " + Build.DEVICE + " | brand: " + Build.BRAND + " | model: " + Build.MODEL);
        mBtShake.setOnClickListener(v -> {
            if (!serialOpened()) {
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
            if (!serialOpened()) {
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
            if (!serialOpened()) {
                ToastUtil.show("串口打开失败");
                return;
            }

            int order = Order.DOWN_SERVO_STATE;
            serialHelper.sendHex(SerialPortUtil.getSendData(order, new int[]{new Random().nextInt(360), 2000}));
        });

        init();
    }

    private void init() {
        mHandler = new Handler(Looper.getMainLooper());
        mHandlerNod = new Handler(Looper.getMainLooper());
        try {
            Log.d(TAG, "open =====================1111111111111111111=");
            int openStatus = open(SerialPortUtil.PORT_NAME, SerialPortUtil.IBAUDTATE);
            Log.d(TAG, "open =====================openStatus = " + openStatus);
        } catch (Exception e) {
            Log.d(TAG, "open =====================444444444444444444=" + e.getMessage());
        }
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
     *
     * @param selectPort
     * @param selectBaud
     * @return
     */
    private int open(String selectPort, int selectBaud) {
        if (serialOpened()) {
            serialHelper.close();
        }
        serialHelper = new SerialHelper(selectPort, selectBaud) {
            @Override
            protected void onDataReceived(byte[] buff) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String hexData = SerialDataUtils.ByteArrToHex(buff);
                        Log.d(TAG, "接收到数据: hexData = " + hexData);
                        SerialPortUtil.setParseDataListener(new SerialPortUtil.ParseDataListener() {


                            @Override
                            public void onHandleOrder(int order, int[] bytes) {

                            }
                        });
                        SerialPortUtil.parseOrder(hexData);
                    }
                });

            }

            @Override
            protected void onSendDataReceived(byte[] buff) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String hexData = SerialDataUtils.ByteArrToHex(buff);
                        Log.d(TAG, "onSendDataReceived = " + hexData);
                    }
                });

            }
        };
        Log.d(TAG, "open =================AAAAAAAAAAAAA");
        try {
            serialHelper.open();
            Log.d(TAG, "open =================BBBBBBBBBBBBBBBBBBB");
            return 1;
        } catch (IOException e) {
            Log.d(TAG, "open =================CCCCCCCCCCCCCCCC" + e.getMessage());
            return -1;
        }


    }

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
        if (!serialOpened()) {
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
        if (serialOpened()) {
            serialHelper.sendHex(SerialPortUtil.getSendData(order, params));
        }
        if (runCount > -1) {
            actionRunnable = new Runnable() {
                @Override
                public void run() {
                    runCount--;
                    send();
                }
            };
            mHandler.postDelayed(actionRunnable, params[2] + 100);
        }
    }

    public void sendNod() {
        int order = Order.DOWN_SERVO_STATE;
        int[] params;
        if (!serialOpened()) {
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
        if (serialOpened()) {
            serialHelper.sendHex(SerialPortUtil.getSendData(order, params));
        }
        if (runCountNod > -1) {
            actionNodRunnable = new Runnable() {
                @Override
                public void run() {
                    runCountNod--;
                    sendNod();
                }
            };
            mHandlerNod.postDelayed(actionNodRunnable, params[2] + 100);
        }
    }


    private boolean serialOpened() {
        if (serialHelper == null) return false;
        return serialHelper.isOpen();
    }


}