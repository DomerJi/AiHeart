package com.thfw.robotheart.port;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import com.pl.sphelper.ConstantUtil;
import com.pl.sphelper.SPHelper;
import com.thfw.base.ContextApp;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.robot.RobotUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Author:pengs
 * Date: 2022/6/6 13:13
 * Describe:Todo
 */
public class SerialManager implements SensorEventListener {

    /**
     * 陀螺仪
     */
    private SensorManager sensorManager;
    private Sensor sensor;

    private static SerialManager instance;
    private boolean upElectricity;
    private static final String TAG = SerialManager.class.getSimpleName();
    private static final int DELAY_TIME = 100;
    private SerialHelper serialHelper;

    private ArrayList<ElectricityListener> electricityListeners = new ArrayList<>();
    private ArrayList<RobotTouchListener> touchListeners = new ArrayList<>();
    private LinkedList<String> mWaitOrder = new LinkedList<>();
    private HashMap<Integer, ActionParams> mRunningActionParams = new HashMap<>();

    private static final int ELECTRICITY_MAX = 8000;
    private static final int ELECTRICITY_MIN = 7000;
    private int electricity = -1;
    private int percent = 100;
    private int charge = -1;
    private float x;
    private float y;
    private float z;
    private int horizontalFlag = -1;

    private SerialManager() {
        initPort();
    }

    private void initPort() {
        if (RobotUtil.isInstallRobot()) {
            x = 0;
            y = 0;
            z = 0;
            electricity = -1;
            percent = 100;
            charge = -1;
            horizontalFlag = -1;
            try {
                Log.d(TAG, "open =====================1111111111111111111");
                int openStatus = open(SerialPortUtil.PORT_NAME, SerialPortUtil.IBAUDTATE);
                Log.d(TAG, "open =====================openStatus = " + openStatus);
            } catch (Exception e) {
                Log.d(TAG, "open =====================444444444444444444=" + e.getMessage());
            }
            sensorManager = (SensorManager) ContextApp.get().getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager != null) {
                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
            }
        }

    }

    public static SerialManager getInstance() {
        if (instance == null) {
            synchronized (SerialManager.class) {
                instance = new SerialManager();
            }
        }
        if (RobotUtil.isInstallRobot()) {
            if (!instance.isOpen()) {
                if (instance.serialHelper != null) {
                    instance.serialHelper.close();
                    instance.serialHelper = null;
                }
                instance.upElectricity = false;
                instance.initPort();
            }
            instance.upElectricity();
        }
        return instance;
    }

    /**
     * 上电
     */
    private void upElectricity() {
        if (!instance.upElectricity) {
            upElectricityNow();
        }
    }

    /**
     * 强制上电
     */
    public void upElectricityNow() {
        if (instance.isOpen()) {
            instance.upElectricity = true;
            // 先上电
            instance.sendNow(Order.DOWN_ELECTRICITY_STATE, 1);
        }
    }

    /**
     * 全部归零
     */
    public void allToZero() {
        queryServoState(new ServoStateListener() {
            @Override
            public void onState(int[] angle) {
                ActionParams actionParams = new ActionParams(ActionParams.ControlOrder.NOD);
                if (actionParams.canUse()) {
                    if (Math.abs(angle[0] - actionParams.getCenterPoint()) > 30) {
                        send(ActionParams.ControlOrder.NOD, actionParams.getCenterPoint(),
                                Math.abs(angle[0] - actionParams.getCenterPoint()) * 30);
                    }
                }
                int shakeZero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
                if (shakeZero != ConstantUtil.DEFAULT_INT) {
                    if (Math.abs(angle[1] - shakeZero) > 30) {
                        send(ActionParams.ControlOrder.SHAKE, shakeZero, Math.abs(angle[0] - shakeZero) * 30);
                    }
                }
                int rotateZero = SPHelper.getInt(ConstantUtil.Key.ROTATE_ZERO);
                if (rotateZero != ConstantUtil.DEFAULT_INT) {
                    if (Math.abs(angle[2] - rotateZero) > 90) {
                        send(ActionParams.ControlOrder.ROTATE, rotateZero, Math.abs(angle[2] - rotateZero) * 30);
                    }
                }
            }
        });

    }

    /**
     * 是否充电中
     *
     * @return
     */
    public boolean isCharging() {
        return charge == 1;
    }

    /**
     * 是否不在充电中
     *
     * @return
     */
    public boolean isNoCharging() {
        return charge == 0;
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
            return;
        } else if (!actionParams.canUse()) {
            LogUtil.i(TAG, "未校准 参数不可用 ：" + actionParams.canUse());
            return;
        } else if (!isOpen()) {
            LogUtil.i(TAG, "串口未开启");
            return;
        } else if (isNoHorizontal()) {
            LogUtil.i(TAG, "请放好机器人");
            ToastUtil.show("请放好机器人");
            return;
        }
        LogUtil.i(TAG, "actionParams -> " + actionParams.toString());
        int order = Order.DOWN_SERVO_STATE;
        int controlOrder = actionParams.getControlOrder();
        if (mRunningActionParams.containsKey(controlOrder)) {
            LogUtil.i(TAG, "正在执行该动作");
            return;
        }
        mRunningActionParams.put(controlOrder, actionParams);
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
            queryServoState(new ServoStateListener() {
                @Override
                public void onState(int[] angle) {
                    int[] params = new int[]{controlOrder, angle[2] + actionParams.getAngle(), actionParams.getTimeMs()};
                    actionParams.nextRotateIndex();
                    LogUtil.d(TAG, "params -> " + Arrays.toString(params));
                    SerialManager.getInstance().sendNow(order, params);
                    ToastUtil.show("2 -> " + angle[2] + "_" + Arrays.toString(params));

                    if (actionParams.nextRotate()) {
                        actionParams.setRunnable(() -> {
                            if (actionParams.nextRotate()) {
                                mRunningActionParams.remove(controlOrder);
                                startAction(actionParams);
                            }
                        });
                        actionParams.getHandler().postDelayed(actionParams.getRunnable(), params[2] + (DELAY_TIME / 2));
                    } else {
                        actionParams.getHandler().postDelayed(() -> {
                            mRunningActionParams.remove(controlOrder);
                        }, params[2] + DELAY_TIME);
                        LogUtil.i(TAG, "actionParams.nextRotate() false 02");
                    }
                }
            });

        } else {
            int centerPoint = actionParams.getCenterPoint();
            int left = actionParams.getLeft();
            int right = actionParams.getRight();
            int[] params;
            if (actionParams.getRunCount() == actionParams.getRepeatCount()) {
                int time = right * ActionParams.ONE_ANGLE_TIME_10;
                // 右
                params = new int[]{controlOrder, centerPoint + right, time};
            } else if (actionParams.getRunCount() == -1) {
                int time = left * ActionParams.ONE_ANGLE_TIME_10;
                // 归零
                actionParams.reverseRight();
                params = new int[]{controlOrder, centerPoint, time};
            } else {
                int time = (left + right) * ActionParams.ONE_ANGLE_TIME_10;
                // 左or右
                actionParams.reverseRight();
                params = new int[]{controlOrder, actionParams.isRight() ? centerPoint + right : centerPoint - left, time};
            }
            LogUtil.d(TAG, "params -> " + Arrays.toString(params));
            SerialManager.getInstance().sendNow(order, params);
            if (actionParams.getRunCount() > -1) {
                actionParams.setRunnable(() -> {
                    mRunningActionParams.remove(controlOrder);
                    actionParams.runCountChange();
                    startAction(actionParams);
                });
                actionParams.getHandler().postDelayed(actionParams.getRunnable(), params[2] + DELAY_TIME);
            } else {
                actionParams.getHandler().postDelayed(() -> {
                    mRunningActionParams.remove(controlOrder);
                }, params[2] + DELAY_TIME);
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
                            // 查询舵机角度 1，2，4，4，4 状态|故障位|1号舵机|2号舵机|3号舵机
                            // 点头 摇头 转身
                        case 4:
                            if (bytes.length == 5) {
                                if (servoStateListener != null) {
                                    servoStateListener.onState(new int[]{bytes[2], bytes[3], bytes[4]});
                                }
                                servoStateListener = null;
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
        boolean isOpen = serialHelper != null && serialHelper.isOpen();
        if (RobotUtil.isInstallRobot()) {
            if (!isOpen) {
                ToastUtil.show("port not open");
            }
        }
        return isOpen;
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

    public void queryServoState(ServoStateListener servoStateListener) {
        if (this.servoStateListener == null) {
            this.servoStateListener = servoStateListener;
            sendNow(Order.DOWN_STATE, 4);
            new Handler().postDelayed(() -> {
                SerialManager.this.servoStateListener = null;
            }, 3000);
        } else {
            LogUtil.d(TAG, "queryServoState not null");
        }
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
        if (electricity != -1) {
            percent = (int) ((electricity - ELECTRICITY_MIN) * 100f / (ELECTRICITY_MAX - ELECTRICITY_MIN));
        }
        LogUtil.i(TAG, "percent = " + percent + " ; charge = " + charge);
        for (ElectricityListener listener : electricityListeners) {
            listener.onCharge(percent, charge);
        }
    }

    public void onSensor(int sensor) {
        LogUtil.i(TAG, "sensor = " + sensor + " ;");
        for (ElectricityListener listener : electricityListeners) {
            listener.onSensor(sensor);
        }
    }

    /**
     * @param code 右边的是0 胸前的是1 左边的是2 后面的是3 头顶是4
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

    public ServoStateListener servoStateListener;

    // sensor == 1 是 没有水平
    public interface ElectricityListener {
        void onCharge(int percent, int charge);

        void onSensor(int sensor);
    }

    public interface RobotTouchListener {
        void onTouch(int code, int down);
    }

    public interface ServoStateListener {
        void onState(int[] angle);

    }


    public void release() {
        if (sensorManager != null && sensor != null) {
            sensorManager.unregisterListener(this, sensor);
        }
        electricityListeners.clear();
        touchListeners.clear();
        mWaitOrder.clear();
        if (serialHelper != null) {
            serialHelper.close();
        }
        instance = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 传感器返回的数据
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        StringBuffer buffer = new StringBuffer();
        buffer.append("X：").append(String.format("%.2f", x)).append("\n");
        buffer.append("Y：").append(String.format("%.2f", y)).append("\n");
        buffer.append("Z：").append(String.format("%.2f", z)).append("\n");
        int horizontal = isNoHorizontal() ? 1 : 0;
        if (horizontalFlag != horizontal) {
            horizontalFlag = horizontal;
            if (horizontalFlag == 1) {
                onSensor(horizontalFlag);
            }
            ToastUtil.show(buffer.toString());
        }
    }


    public boolean isNoHorizontal() {
        return x > 0.1 || y > 0.1 || z > 0.1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
