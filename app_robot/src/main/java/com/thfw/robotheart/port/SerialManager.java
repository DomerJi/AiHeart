package com.thfw.robotheart.port;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.pl.sphelper.ConstantUtil;
import com.pl.sphelper.SPHelper;
import com.thfw.base.ContextApp;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.HourUtil;
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
public class SerialManager {


    private static final String TAG = SerialManager.class.getSimpleName();

    /**
     * 陀螺仪
     */
    private SensorManager manager;
    private MySensorEventListener listener;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private float[] gravity = new float[3];
    // 加速度偏差值
    private static final float SENSOR_FLOAT_MIN = 2.6f;
    private static final float SENSOR_FLOAT_MAX = 10 - SENSOR_FLOAT_MIN;
    private static SerialManager instance;
    private boolean upElectricity;
    private static final int DELAY_TIME = 140;
    private SerialHelper serialHelper;

    private ArrayList<ElectricityListener> electricityListeners = new ArrayList<>();
    private ArrayList<RobotTouchListener> touchListeners = new ArrayList<>();
    private LinkedList<String> mWaitOrder = new LinkedList<>();
    private HashMap<Integer, Long> mRunningActionParams = new HashMap<>();
    private HashMap<Integer, Boolean> mRunningActionNoFinish = new HashMap<>();

    private static final int ELECTRICITY_MAX = 8000;
    private static final int ELECTRICITY_MIN = 7000;
    private int electricity = -1;
    private int percent = 100;
    private int charge = -1;
    private int mNoHorizontalFlag = -1;
    private long horizontalFlagContinueTime = -1;
    private static long lastSensorTime;
    private static long queryMvTime;

    private Handler handler = new Handler();
    // 舵机查询延时
    private Runnable runnable;
    private long currentTime;

    private SerialManager() {
        initPort();
    }

    private void initPort() {
        if (RobotUtil.isInstallRobot()) {
            electricity = -1;
            percent = 100;
            charge = -1;
            mNoHorizontalFlag = -1;
            try {
                Log.d(TAG, "open =====================1111111111111111111");
                int openStatus = open(SerialPortUtil.PORT_NAME, SerialPortUtil.IBAUDTATE);
                Log.d(TAG, "open =====================openStatus = " + openStatus);
            } catch (Exception e) {
                Log.d(TAG, "open =====================444444444444444444=" + e.getMessage());
            } finally {
                initSensor();
            }
        }
    }

    private void initSensor() {
        if (manager == null) {
            // 获取SensorManager
            manager = (SensorManager) ContextApp.get().getSystemService(Context.SENSOR_SERVICE);
            listener = new MySensorEventListener();
            // 获取Sensor
            accelerometerSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscopeSensor = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            gravity = new float[3];// 用来保存加速度传感器的值

            boolean register01 = manager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            boolean register02 = manager.registerListener(listener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

            LogUtil.d(TAG, "accelerometerSensor register = " + register01);
            LogUtil.d(TAG, "gyroscopeSensor register = " + register02);
            if (!register01) {
                if (manager != null) {
                    if (accelerometerSensor != null) {
                        manager.unregisterListener(listener, accelerometerSensor);
                    }
                    if (gyroscopeSensor != null) {
                        manager.unregisterListener(listener, gyroscopeSensor);
                    }
                    manager = null;
                }
            }
        }
    }


    private class MySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

//            LogUtil.d("onSensorChanged", "type = " + event.sensor.getType() + "_" + Arrays.toString(event.values));

            switch (event.sensor.getType()) {
                // 地磁
                case Sensor.TYPE_MAGNETIC_FIELD:
                    break;
                // 加速度
                case Sensor.TYPE_ACCELEROMETER:
                    getValue(event);
                    break;
                // 陀螺仪
                case Sensor.TYPE_GYROSCOPE:
                    break;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }


    public static SerialManager getInstance() {
        if (instance == null) {
            synchronized (SerialManager.class) {
                if (instance == null) {
                    instance = new SerialManager();
                }
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
            if (instance.manager == null) {
                instance.initSensor();
            } else if (System.currentTimeMillis() - instance.currentTime > HourUtil.LEN_SECOND5) {
                if (instance.manager != null) {
                    if (instance.accelerometerSensor != null) {
                        instance.manager.unregisterListener(instance.listener, instance.accelerometerSensor);
                    }
                    if (instance.gyroscopeSensor != null) {
                        instance.manager.unregisterListener(instance.listener, instance.gyroscopeSensor);
                    }
                    instance.manager = null;
                    instance.initSensor();
                }
            }
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
            instance.send(Order.DOWN_ELECTRICITY_STATE, 1);
        }
    }

    /**
     * 舵机归零
     */
    public void allToZero(int type) {
        if (isNoAction() || isActionNoFinished()) {
            return;
        }

        queryServoState((angle) -> {
            switch (type) {
                case ActionParams.ControlOrder.NOD:
                    ActionParams actionParams = new ActionParams(ActionParams.ControlOrder.NOD);
                    if (actionParams.canUse()) {
                        if (Math.abs(angle[0] - actionParams.getCenterPoint()) > 30) {
                            startAction(actionParams
                                    .setForce(true)
                                    .setAngles(actionParams.getCenterPoint() - angle[0])
                                    .setOneRotateTimeMs(40));
                        }
                    }
                    break;
                case ActionParams.ControlOrder.SHAKE:
                    int shakeZero = SPHelper.getInt(ConstantUtil.Key.SHAKE_ZERO);
                    if (shakeZero != ConstantUtil.DEFAULT_INT) {
                        if (Math.abs(angle[1] - shakeZero) > 30) {
                            startAction(new ActionParams(ActionParams.ControlOrder.SHAKE)
                                    .setForce(true)
                                    .setAngles(shakeZero - angle[1])
                                    .setOneRotateTimeMs(40));
                        }
                    }
                    break;
                case ActionParams.ControlOrder.ROTATE:
                    int rotateZero = SPHelper.getInt(ConstantUtil.Key.ROTATE_ZERO);
                    if (rotateZero != ConstantUtil.DEFAULT_INT) {
                        if (Math.abs(angle[2] - rotateZero) > 90) {
                            startAction(new ActionParams(ActionParams.ControlOrder.ROTATE)
                                    .setForce(true)
                                    .setAngles(rotateZero - angle[2])
                                    .setOneRotateTimeMs(40));
                        }
                    }
                    break;
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
                try {
                    String hexData = SerialDataUtils.ByteArrToHex(buff);
                    Log.d(TAG, "接收到数据: hexData = " + hexData);
                    HandlerUtil.getMainHandler().post(() -> {
                        SerialManager.this.onDataReceived(hexData);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "e " + e.getMessage());
                }
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

    public void startAllAction() {
        SerialManager.getInstance().startAction(ActionParams.getNormalShake());

        SerialManager.getInstance().startAction(ActionParams.getNormalNod());

        SerialManager.getInstance().startAction(ActionParams.getNormalRotate());
    }

    /**
     * 是否有动作正在进行中
     *
     * @return
     */
    private boolean isActionNoFinished() {
        return mRunningActionNoFinish.containsValue(true);
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
        } else if (!actionParams.isForce() && isNoAction() && !isActionNoFinished()) {
            LogUtil.i(TAG, "请放平机器人后再试");
            ToastUtil.show("请放平机器人后再试");
            return;
        }
        LogUtil.i(TAG, "actionParams -> " + actionParams.toString());
        int order = Order.DOWN_SERVO_STATE;
        int controlOrder = actionParams.getControlOrder();
        if (mRunningActionParams.containsKey(controlOrder)) {
            if (System.currentTimeMillis() - mRunningActionParams.get(controlOrder) < 3000) {
                LogUtil.i(TAG, "正在执行该动作");
                if (LogUtil.isLogEnabled()) {
                    ToastUtil.show("正在执行该动作");
                }
                return;
            }
        }
        mRunningActionParams.put(controlOrder, System.currentTimeMillis());
        if (actionParams.getRunnable() != null) {
            actionParams.getHandler().removeCallbacks(actionParams.getRunnable());
        }
        /**
         * 旋转
         */
        if (controlOrder == ActionParams.ControlOrder.ROTATE) {
            if (!actionParams.nextRotate()) {
                mRunningActionNoFinish.put(controlOrder, false);
                allToZero(controlOrder);
                LogUtil.i(TAG, "actionParams.nextRotate() false 01");
                return;
            }
            // 异常后确保回到零位
            final Runnable rotateCancel = () -> {
                mRunningActionNoFinish.put(controlOrder, false);
                allToZero(controlOrder);
            };
            // 延时后没有反馈则 认为异常 终止此次任务
            handler.postDelayed(rotateCancel, 1500);
            queryServoState((angle) -> {
                handler.removeCallbacks(rotateCancel);
                mRunningActionNoFinish.put(controlOrder, true);
                int[] params = new int[]{controlOrder, angle[2] + actionParams.getAngle(), actionParams.getTimeMs()};
                if (actionParams.isFirst()) {
                    SPHelper.setNewRotateZero(angle[2]);
                }
                actionParams.nextRotateIndex();
                LogUtil.d(TAG, "转身：params -> " + Arrays.toString(params));
                SerialManager.getInstance().send(order, params);
                if (LogUtil.isLogEnabled()) {
                    ToastUtil.show("转身： -> " + angle[2] + "_" + Arrays.toString(params));
                }

                if (actionParams.nextRotate()) {
                    actionParams.setRunnable(() -> {
                        if (actionParams.nextRotate()) {
                            mRunningActionParams.remove(actionParams.getControlOrder());
                            startAction(actionParams);
                        }
                    });
                    actionParams.getHandler().postDelayed(actionParams.getRunnable(), params[2] + (DELAY_TIME / 2));
                } else {
                    mRunningActionNoFinish.put(controlOrder, false);
                    allToZero(controlOrder);
                    LogUtil.i(TAG, "actionParams.nextRotate() false 02");
                }
            });

        } else {
            mRunningActionNoFinish.put(controlOrder, true);
            int centerPoint = actionParams.getCenterPoint();
            int left = actionParams.getLeft();
            int right = actionParams.getRight();
            int[] params;
            if (actionParams.getRunCount() == actionParams.getRepeatCount()) {
                int time = (int) (right * (actionParams.getOneAngleTime() / 10f));
                // 右
                params = new int[]{controlOrder, centerPoint + right, time};
            } else if (actionParams.getRunCount() == -1) {
                int time = (int) (left * (actionParams.getOneAngleTime() / 10f));
                // 归零
                actionParams.reverseRight();
                params = new int[]{controlOrder, centerPoint, time};
            } else {
                int time = (int) ((left + right) * (actionParams.getOneAngleTime() / 10f));
                // 左or右
                actionParams.reverseRight();
                params = new int[]{controlOrder, actionParams.isRight() ? centerPoint + right : centerPoint - left, time};
            }
            LogUtil.d(TAG, "params -> " + Arrays.toString(params));
            if (LogUtil.isLogEnabled()) {
                ToastUtil.show((actionParams.getControlOrder() == ActionParams.ControlOrder.NOD
                        ? "点头： -> " : "摇头： -> ") + "left：" + left + "_right：" + right + "_" + Arrays.toString(params));
            }
            SerialManager.getInstance().send(order, params);
            if (actionParams.getRunCount() > -1) {
                actionParams.setRunnable(() -> {
                    mRunningActionParams.remove(actionParams.getControlOrder());
                    actionParams.runCountChange();
                    startAction(actionParams);
                });
                actionParams.getHandler().postDelayed(actionParams.getRunnable(), params[2] + DELAY_TIME);
            } else {
                mRunningActionNoFinish.put(controlOrder, false);
                allToZero(controlOrder);
                LogUtil.i(TAG, "actionParams.getRunCount() false 02");
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
                        // 查询电压 1，2，1 状态|故障位|电压|电量
                        case 1:
                            if (bytes.length == 4) {
                                // 电量百分比
                                if (electricity != bytes[3]) {
                                    electricity = bytes[3];
                                    onCharge2();
                                }
                            } else if (bytes.length == 3) {
                                // 电压计算方法不准【过时】
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
                                    onCharge2();
                                }
                            }
                            // 查询舵机角度 1，2，4，4，4 状态|故障位|1号舵机|2号舵机|3号舵机
                            // 点头 摇头 转身
                        case 4:
                            if (bytes.length == 5) {
                                if (servoStateListener != null) {
                                    servoStateListener.onState(new int[]{bytes[2], bytes[3], bytes[4]});
                                }
                                handler.removeCallbacks(runnable);
                                servoStateListener = null;
                            }
                            break;
                    }


                    break;
                case Order.UP_ADAPTER_STATE:
                    if (bytes.length == 1) {
                        if (charge != bytes[0]) {
                            charge = bytes[0];
                            onCharge2();
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

        try {
            SerialPortUtil.parseOrder(hexData);
        } catch (Exception e) {
            LogUtil.e(TAG, "hexData e = " + e.getMessage());
        }

    }

    public boolean isOpen() {
        boolean isOpen = serialHelper != null && serialHelper.isOpen();
        if (RobotUtil.isInstallRobot() && LogUtil.isLogEnabled() && !isOpen) {
            ToastUtil.show("port not open");
        }
        return isOpen;
    }

    /**
     * 查询电压和适配器状态
     */
    public synchronized void queryCharge() {
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
        long currentTime = System.currentTimeMillis();
        if (currentTime - queryMvTime > HourUtil.LEN_SECOND) {
            queryMvTime = currentTime;
            send(Order.DOWN_STATE, 1);
        } else {
            if (queryMvTime > currentTime) {
                queryMvTime = currentTime;
            }
        }

    }

    public void queryServoState(ServoStateListener servoStateListener) {
        if (runnable == null) {
            runnable = () -> {
                SerialManager.this.servoStateListener = null;
            };
        }
        if (this.servoStateListener == null) {
            this.servoStateListener = servoStateListener;
            send(Order.DOWN_STATE, 4);
        } else {
            LogUtil.d(TAG, "queryServoState not null");
        }
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 3000);
    }

    public synchronized boolean send(int order, int... params) {
        try {
            String hex = SerialPortUtil.getSendData(order, params);
            return !TextUtils.isEmpty(hex) && send(hex);
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized boolean send(String hex) {
        if (!isOpen()) {
            open(SerialPortUtil.PORT_NAME, SerialPortUtil.IBAUDTATE);
        }
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
        if (isOpen()) {
            serialHelper.sendHex(hex);
            Log.d(TAG, "send -> hex：" + hex);
        } else {
            Log.d(TAG, "send -> hex：fail " + hex);
        }

        handler.postDelayed(() -> {
            mWaitOrder.removeFirst();
            if (!mWaitOrder.isEmpty()) {
                sendDelayed();
            }
        }, 200);
    }

    public void onCharge2() {
        if (electricity == -1) {
            percent = 100;
        } else {
            percent = electricity;
        }
        LogUtil.i(TAG, "percent2 = " + percent + " ; charge = " + charge);
        for (ElectricityListener listener : electricityListeners) {
            listener.onCharge(percent, charge);
        }
    }

    @Deprecated
    public void onCharge() {
        if (electricity != -1) {
            percent = (int) ((electricity - ELECTRICITY_MIN) * 100f / (ELECTRICITY_MAX - ELECTRICITY_MIN));
        }
        LogUtil.i(TAG, "percent = " + percent + " ; charge = " + charge);
        for (ElectricityListener listener : electricityListeners) {
            listener.onCharge(percent, charge);
        }
    }

    /**
     * sensor 1 倾斜
     *
     * @param sensor
     */
    public void onSensor(int sensor, boolean showTip) {
        LogUtil.i(TAG, "sensor = " + sensor);
        for (ElectricityListener listener : electricityListeners) {
            listener.onSensor(sensor, showTip);
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

        void onSensor(int sensor, boolean showTip);
    }

    public interface RobotTouchListener {
        void onTouch(int code, int down);
    }

    public interface ServoStateListener {
        void onState(int[] angle);

    }


    public void release() {
        if (manager != null) {
            if (accelerometerSensor != null) {
                manager.unregisterListener(listener, accelerometerSensor);
            }
            if (gyroscopeSensor != null) {
                manager.unregisterListener(listener, gyroscopeSensor);
            }
            manager = null;
        }
        handler.removeCallbacksAndMessages(null);
        electricityListeners.clear();
        touchListeners.clear();
        mWaitOrder.clear();
        if (serialHelper != null) {
            serialHelper.close();
        }
        instance = null;
    }


    private void getValue(SensorEvent event) {
        if (event == null || EmptyUtil.isEmpty(event.values)) {
            return;
        }
        gravity = event.values;
        final int mNoHorizontal = isNoHorizontal(event.values[0], event.values[1], event.values[2]) ? 1 : 0;
        currentTime = System.currentTimeMillis();
        if (mNoHorizontalFlag != mNoHorizontal) {
            mNoHorizontalFlag = mNoHorizontal;
            horizontalFlagContinueTime = currentTime;
        }
        if (mNoHorizontalFlag == 0) {
            lastSensorTime = 0;
        } else if (isNoAction() && System.currentTimeMillis() - lastSensorTime > HourUtil.LEN_MINUTE30) {
            if (RobotUtil.isInstallRobot()) {
                lastSensorTime = currentTime;
                HandlerUtil.getMainHandler().postDelayed(() -> {
                    if (isNoAction()) {
                        lastSensorTime = System.currentTimeMillis();
                        onSensor(mNoHorizontal, true);
                    } else {
                        lastSensorTime = lastSensorTime - HourUtil.LEN_MINUTE30;
                    }
                }, 1200);
            }
        }

        if (LogUtil.isLogEnabled()) {
            onSensor(mNoHorizontal, false);
        }
    }

    public float[] getGravity() {
        return gravity;
    }

    /**
     * 是否不可以进行动作，机器人不在水平下，且持续时间超过1S
     *
     * @return
     */
    public boolean isNoAction() {
        return mNoHorizontalFlag == 1 && horizontalFlagContinueTime != -1
                && System.currentTimeMillis() - horizontalFlagContinueTime > HourUtil.LEN_SECOND;
    }

    /**
     * 是否不在水平 状态
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean isNoHorizontal(double x, double y, double z) {
        return Math.abs(x) > SENSOR_FLOAT_MIN
                || Math.abs(y) < SENSOR_FLOAT_MAX
                || Math.abs(z) > SENSOR_FLOAT_MIN;
    }


}
