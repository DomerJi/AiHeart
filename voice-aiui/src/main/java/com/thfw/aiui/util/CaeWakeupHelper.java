package com.thfw.aiui.util;

import android.content.Context;

import com.iflytek.aiui.AIUIAgent;
import com.thfw.aiui.cae.CaeOperator;
import com.thfw.aiui.cae.OnCaeOperatorlistener;
import com.thfw.aiui.recorder.RecOperator;
import com.thfw.aiui.recorder.RecordListener;

import java.util.Arrays;

/**
 * Author:pengs
 * Date: 2022/7/19 13:19
 * Describe:cae 声源定位
 */
public class CaeWakeupHelper {

    private static final String TAG = CaeWakeupHelper.class.getSimpleName();
    private static int ret = 0;
    private static String strTip = "";
    private static Context appContext;
    // 多麦克算法库
    private CaeOperator mCaeOperator;
    private RecOperator mRecOperator;

    // 录音机工作状态
    private static boolean isRecording = false;
    static CaeWakeupHelper caeWakeupHelper;
    private AIUIAgent mAIUIAgent;

    public static CaeWakeupHelper getInstance() {
        if (caeWakeupHelper == null) {
            synchronized (CaeWakeupHelper.class) {
                if (caeWakeupHelper == null) {
                    caeWakeupHelper = new CaeWakeupHelper();
                    caeWakeupHelper.initSDK();
                }
            }
        }
        if (ret != 0) {
            caeWakeupHelper.initSDK();
        }
        return caeWakeupHelper;
    }

    public static void init(Context context) {
        appContext = context.getApplicationContext();
        CaeOperator.AUTH_SN = FileUtil.getCPUSerial();
        LogUtils.d(TAG, "CaeOperator.AUTH_SN -> " + CaeOperator.AUTH_SN);
        // 资源拷贝
        CaeOperator.portingFile(appContext);
    }


    private void initSDK() {
        // 初始化CAE
        initCaeEngine();
        // 初始化alsa录音
        initAlsa();
    }

    /**
     * 初始化CAE
     */
    private void initCaeEngine() {
        mCaeOperator = new CaeOperator();
        ret = mCaeOperator.initCAE(onCaeOperatorlistener);
        if (ret == 0) {
            strTip = "CAE初始化成功";
            initAlsa();
        } else {
            strTip = "CAE初始化失败,错误信息为：" + ret;
        }
        setText(strTip);
        setText("---------init_CAE---------");
    }

    /**
     * 初始化ALSA
     */
    private void initAlsa() {
        mRecOperator = new RecOperator();
        mRecOperator.initRec(appContext, onRecordListener);
    }


    public void startRecord() {
        if (!isRecording && mRecOperator != null) {
            ret = mRecOperator.startrecord();
            if (0 == ret) {
                strTip = "开启录音成功！";
                isRecording = true;
            } else if (111111 == ret) {
                strTip = "AlsaRecorder is null ...";
            } else {
                strTip = "开启录音失败，请查看/dev/snd/下的设备节点是否有777权限！\nAndroid 8.0 以上需要暂时使用setenforce 0 命令关闭Selinux权限！";
                destroyRecord();
            }
            setText(strTip);
            setText("---------start_alsa_record---------");
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void stopRecord() {
        if (isRecording && mRecOperator != null) {
            mRecOperator.stopRecord();
            mCaeOperator.stopSaveAudio();
            isRecording = false;
            setText("停止录音");
            setText("---------stop_alsa_record---------");
        }
    }

    /**
     * CAE 回调消息处理
     */
    private OnCaeOperatorlistener onCaeOperatorlistener = new OnCaeOperatorlistener() {
        @Override
        public void onAudio(byte[] audioData, int dataLen) {
            // CAE降噪后音频写入AIUI SDK进行语音交互
        }

        @Override
        public void onWakeup(int angle, int beam) {

            // 唤醒响应时间分析标记
            byte[] data = new byte[16];
            Arrays.fill(data, (byte) (0xff / 2));
            mCaeOperator.saveAduio(data, CaeOperator.mAlsaRawFileUtil);

            final int a = angle;
            final int b = beam;
            LogUtils.d(TAG, "唤醒成功,angle:" + a + " beam:" + b);

            setText("唤醒成功,angle:" + a + " beam:" + b);
            setText("---------WAKEUP_CAE---------");
            if (onWakeUpListener != null) {
                onWakeUpListener.onWakeup(a, b);
            }
        }
    };


    /**
     * Alsa录音回调消息处理
     */
    private RecordListener onRecordListener = new RecordListener() {

        private long lastTime;

        @Override
        public void onPcmData(byte[] bytes) {
            if (System.currentTimeMillis() - lastTime > 10000) {
                lastTime = System.currentTimeMillis();
                LogUtils.d(TAG, "onPcmData -> " + Arrays.toString(bytes));
            }
            // 保存原始录音数据
            mCaeOperator.saveAduio(bytes, CaeOperator.mAlsaRawFileUtil);
//            // 录音数据转换：usb声卡 线性4mic
            byte[] data = RecordAudioUtil.adapeter4Mic(bytes);
            // 录音数据转换：usb声卡 线性/环形6mic
//            byte[] data = RecordAudioUtil.adapeter6Mic(bytes);
            // 保存转换后录音数据

            mCaeOperator.saveAduio(data, CaeOperator.mAlsaRecFileUtil);

            // 写入CAE引擎
            mCaeOperator.writeAudioTest(data);
        }
    };

    public void destroyRecord() {
        if (null != mRecOperator && null != mCaeOperator) {
            mRecOperator.stopRecord();
            mCaeOperator.stopSaveAudio();
        } else {
            LogUtils.d(TAG, "distoryCaeEngine is Done!");
        }
        isRecording = false;
    }

    private void setText(final String str) {
        LogUtils.d(TAG, "setText str -> " + str);

    }

    OnWakeUpListener onWakeUpListener;

    public void setOnWakeUpListener(OnWakeUpListener onWakeUpListener) {
        this.onWakeUpListener = onWakeUpListener;
    }

    public interface OnWakeUpListener {
        void onWakeup(int angle, int beam);
    }


}
