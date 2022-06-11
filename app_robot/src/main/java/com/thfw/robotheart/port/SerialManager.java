package com.thfw.robotheart.port;

import android.os.Handler;
import android.util.Log;

import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.robot.RobotUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Author:pengs
 * Date: 2022/6/6 13:13
 * Describe:Todo
 */
public class SerialManager {
    private static SerialManager instance;
    private static final String TAG = SerialManager.class.getSimpleName();
    SerialHelper serialHelper;

    ArrayList<ElectricityListener> electricityListeners = new ArrayList<>();
    ArrayList<RobotTouchListener> touchListeners = new ArrayList<>();
    LinkedList<String> mWaitOrder = new LinkedList<>();
    private static final int ELECTRICITY_MAX = 8000;
    private static final int ELECTRICITY_MIN = 7000;
    private int electricity = -1;
    private int percent = -1;
    private int charge = -1;

    private SerialManager() {
        if (RobotUtil.isInstallRobot()) {
            electricity = -1;
            percent = -1;
            charge = -1;
            try {
                Log.d(TAG, "open =====================1111111111111111111");
                int openStatus = open(SerialPortUtil.PORT_NAME, SerialPortUtil.IBAUDTATE);
                Log.d(TAG, "open =====================openStatus = " + openStatus);
            } catch (Exception e) {
                Log.d(TAG, "open =====================444444444444444444=" + e.getMessage());
            }
        }
    }

    public static SerialManager getInstance() {
        if (instance == null) {
            synchronized (SerialManager.class) {
                instance = new SerialManager();
            }
        }
        return instance;
    }

    private int open(String selectPort, int selectBaud) {
        serialHelper = new SerialHelper(selectPort, selectBaud) {
            @Override
            protected void onDataReceived(byte[] buff) {
                String hexData = SerialDataUtils.ByteArrToHex(buff);
                Log.d(TAG, "接收到数据: hexData = " + hexData);
                HandlerUtil.getMainHandler().post(() -> {
                    SerialManager.this.onDataReceived(hexData);
                });
            }

            @Override
            protected void onSendDataReceived(byte[] buff) {
                String hexData = SerialDataUtils.ByteArrToHex(buff);
                Log.d(TAG, "onSendDataReceived = " + hexData);

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

    public void startAction(ActionParams actionParams) {
        if (actionParams == null) {
            LogUtil.e(TAG, "actionParams -> null");
        } else {
            LogUtil.i(TAG, "actionParams -> " + actionParams.toString());
        }
        if (!actionParams.canUse()) {
            LogUtil.i(TAG, "未校准 参数不可用 ：" + actionParams.canUse());
            return;
        }
        if (!SerialManager.getInstance().isOpen()) {
            LogUtil.i(TAG, "串口未开启");
            return;
        }

        int order = Order.DOWN_SERVO_STATE;
        int controlOrder = actionParams.getControlOrder();
        int[] params;

        if (actionParams.getRunnable() != null) {
            actionParams.getHandler().removeCallbacks(actionParams.getRunnable());
        }

        /**
         * 旋转
         */
        if (controlOrder == ActionParams.ControlOrder.ROTATE) {
            if (!actionParams.nextRotate()) {
                LogUtil.i(TAG, "actionParams.nextRotate() false 01");
                return;
            }
            params = new int[]{controlOrder, actionParams.getAngle(), actionParams.getTimeMs()};
            actionParams.nextRotateIndex();
            LogUtil.d(TAG, "params -> " + Arrays.toString(params));
            SerialManager.getInstance().sendNow(order, params);
            if (actionParams.nextRotate()) {
                actionParams.setRunnable(() -> {
                    if (actionParams.nextRotate()) {
                        startAction(actionParams);
                    }
                });
                actionParams.getHandler().postDelayed(actionParams.getRunnable(), params[2] + 100);
            } else {
                LogUtil.i(TAG, "actionParams.nextRotate() false 02");
            }
        } else {
            int time = actionParams.getTimeMs();
            int centerPoint = actionParams.getCenterPoint();
            int left = actionParams.getLeft();
            int right = actionParams.getRight();
            if (actionParams.getRunCount() == actionParams.getRepeatCount()) {
                // 右
                params = new int[]{controlOrder, centerPoint + right, time / 2};
            } else if (actionParams.getRunCount() == -1) {
                // 归零
                actionParams.reverseRight();
                params = new int[]{controlOrder, centerPoint, time / 2};
            } else {
                // 左or右
                actionParams.reverseRight();
                params = new int[]{controlOrder, actionParams.isRight() ? centerPoint + right : centerPoint - left, time};
            }
            LogUtil.d(TAG, "params -> " + Arrays.toString(params));
            SerialManager.getInstance().sendNow(order, params);
            if (actionParams.getRunCount() > -1) {
                actionParams.setRunnable(() -> {
                    actionParams.runCountChange();
                    startAction(actionParams);
                });
                actionParams.getHandler().postDelayed(actionParams.getRunnable(), params[2] + 100);
            }
        }


    }

    private void onDataReceived(String hexData) {
        SerialPortUtil.setParseDataListener(((order, bytes) -> {
            switch (order) {

                case Order.UP_STATE:
                    // 状态 0所有 1电压 2按键 3红外 4舵机 5适配器
                    int type = bytes[0];
                    switch (type) {
                        // 查询电压 1，2，1 状态|故障位|电压
                        case 1:
                            if (bytes.length == 3) {
                                if (electricity != bytes[2]) {
                                    electricity = bytes[2];
                                    onCharge();
                                }
                            }
                            break;
                        // 查询适配器状态 1，2，1 状态|故障位|适配器
                        case 5:
                            if (bytes.length == 3) {
                                if (charge != bytes[2]) {
                                    charge = bytes[2];
                                    onCharge();
                                }
                            }
                            break;
                    }


                    break;
                case Order.UP_ADAPTER_STATE:
                    if (bytes.length == 1) {
                        if (charge != bytes[0]) {
                            charge = bytes[0];
                            onCharge();
                        }
                    }
                    break;
                // 按键序号 + 按键状态
                case Order.UP_BUTTON_STATE:
                    if (bytes.length == 2) {
                        onTouch(bytes[0], bytes[1]);
                    }
                    break;
            }
        }));
        SerialPortUtil.parseOrder(hexData);
    }

    public boolean isOpen() {
        return serialHelper != null && serialHelper.isOpen();
    }

    /**
     * 查询电压和适配器状态
     */
    public void queryCharge() {
        /**
         * .add("所有信息", 0)
         * .add("电压", 1)
         * .add("按键", 2)
         * .add("红外", 3)
         * .add("舵机角度", 4)
         * .add("适配器", 5);
         */
        if (charge == -1) {
            send(Order.DOWN_STATE, 5);
        }
        send(Order.DOWN_STATE, 1);

    }

    public synchronized boolean send(int order, int... params) {
        return send(SerialPortUtil.getSendData(order, params));
    }

    public synchronized boolean sendNow(int order, int... params) {
        if (isOpen()) {
            serialHelper.sendHex(SerialPortUtil.getSendData(order, params));
            return true;
        }
        return false;
    }

    public synchronized boolean send(String hex) {
        if (isOpen()) {
            if (mWaitOrder.isEmpty()) {
                mWaitOrder.add(hex);
                sendDelayed();
            } else {
                mWaitOrder.add(hex);
            }
            return true;
        }
        return false;
    }

    private void sendDelayed() {
        String hex = mWaitOrder.getFirst();
        serialHelper.sendHex(hex);
        Log.d(TAG, "send -> hex：" + hex);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWaitOrder.removeFirst();
                if (!mWaitOrder.isEmpty()) {
                    sendDelayed();
                }
            }
        }, 1000);
    }

    public void onCharge() {
        percent = (int) ((electricity - ELECTRICITY_MIN) * 100f / (ELECTRICITY_MAX - ELECTRICITY_MIN));
        LogUtil.i(TAG, "percent = " + percent + " ; charge = " + charge);
        for (ElectricityListener listener : electricityListeners) {
            listener.onCharge(percent, charge);
        }
    }

    /**
     * @param code 右边的是0 胸前的是1 左边的是2 后面的是3
     * @param down 1按下 0抬起
     */
    public void onTouch(int code, int down) {
        LogUtil.i(TAG, "code = " + code + " ;");
        for (RobotTouchListener listener : touchListeners) {
            listener.onTouch(code, down);
        }
    }

    public synchronized void addEleListener(ElectricityListener listener) {
        if (listener != null) {
            listener.onCharge(percent, charge);
            if (!electricityListeners.contains(listener)) {
                electricityListeners.add(listener);
            }
        }
    }

    public synchronized void removeEleListener(ElectricityListener listener) {
        if (listener != null) {
            if (electricityListeners.contains(listener)) {
                electricityListeners.remove(listener);
            }
        }
    }

    public synchronized void addRobotTouchListener(RobotTouchListener listener) {
        if (listener != null) {
            if (!touchListeners.contains(listener)) {
                touchListeners.add(listener);
            }
        }
    }

    public synchronized void removeRobotTouchListener(RobotTouchListener listener) {
        if (listener != null) {
            if (touchListeners.contains(listener)) {
                touchListeners.remove(listener);
            }
        }
    }

    public interface ElectricityListener {
        void onCharge(int percent, int charge);
    }

    public interface RobotTouchListener {
        void onTouch(int code, int down);
    }


    public void release() {
        if (serialHelper != null) {
            serialHelper.close();
        }
    }

}
