package com.thfw.aiui.recorder;

import android.content.Context;

import com.iflytek.alsa.AlsaRecorder;
import com.thfw.aiui.util.LogUtils;
import com.thfw.aiui.util.RootShell;


public class RecOperator {

    private static final String TAG = RecOperator.class.getSimpleName();

    // pcm录音设备号，根据实际情况设置createInstance AlsaRecorder
    /**
     * tinycap /sdcard/test.pcm -D 0 -d 0 -c 4 -r 48000 -b 32 -p 768 -n 10
     *
     * tinycap /sdcard/test.pcm -D 2 -d 0 -c 4 -r 16000 -b 16
     * tinycap /sdcard/test.pcm -D 2 -d 0 -c 8 -r 16000 -b 16
     * -D  card       声卡
     * -d  device     设备
     * -c  channels   通道
     * -r  rate       采样率s
     * -b  bits       pcm 位宽
     * -p  period_size   一次中断的帧数
     * -n  n_periods     周期数
     */

    /**
     * pcm 声卡号
     */
    private final static int mPcmCard = 2;
    /**
     * pcm 声卡设备号
     */
    private final static int mPcmDevice = 0;
    /**
     * 通道数量
     */
    private final static int mPcmChannel = 8;
    /**
     * 采样率
     */
    private final static int mPcmSampleRate = 16000;
    /**
     * 一次中断的帧数 一般不同修改，某些不支持这么大数字时会报错，可以尝试减小改值，例如 1023
     */
    private final static int mPcmPeriodSize = 1536;
    /**
     * 周期数 一般不同修改
     */
    private final static int mPcmPeriodCount = 4;
    /**
     * pcm 位宽 0-PCM_FORMAT_S16_LE、<br>1-PCM_FORMAT_S32_LE、<br>2-PCM_FORMAT_S8、<br>3-PCM_FORMAT_S24_LE、<br>4-PCM_FORMAT_MAX
     */
    private final static int mPcmFormat = 0;
    /**
     * 封装的ALS录音库，回调的音频帧大小。如果录音采样率不是16k,在转换音频的时候需要适配，例如48K音频录音可以改成 6144
     */
//    private final static int mPcmBufferSize = 8192;

    protected RecordListener mPcmListener;

    //  录音数据信息透传回调监听
    private AlsaRecorder mAlsaRecorder;


    public void initRec(Context context, RecordListener mRecordListener) {
        RootShell.execRootCmdSilent("setenforce 0");
        RootShell.execRootCmdSilent("chmod 777 /dev/snd/pcmC" + mPcmCard + "D" + mPcmDevice + "c");
        mPcmListener = mRecordListener;
        mAlsaRecorder = AlsaRecorder.createInstance(mPcmCard, mPcmDevice, mPcmChannel, mPcmSampleRate,
                mPcmPeriodSize, mPcmPeriodCount, mPcmFormat);
//        mAlsaRecorder = AlsaRecorder.createInstance(mPcmCard, mPcmDevice, mPcmChannel, mPcmSampleRate,
//                mPcmPeriodSize, mPcmPeriodCount, mPcmFormat,mPcmBufferSize);
        mAlsaRecorder.setLogShow(false);                // Alsa-Jni日志控制 true-开启  false-关闭
    }


    // 开始录音
    public int startrecord() {
        if (mAlsaRecorder != null) {
            int recRet = mAlsaRecorder.startRecording(mAlsaPcmListener);
            if (0 == recRet) {
                LogUtils.i(TAG, "start recording sucess...");
            } else {
                LogUtils.i(TAG, "start recording fail...");
            }
            return recRet;
        } else {
            LogUtils.d(TAG, "AlsaRecorder is null ...");
            return 111111;
        }
    }


    // 停止录音
    public void stopRecord() {
        mAlsaRecorder.stopRecording();
        LogUtils.i(TAG, "stopRecd ok...");
    }


    // tinyalsa录音音频监听器
    AlsaRecorder.PcmListener mAlsaPcmListener = new AlsaRecorder.PcmListener() {
        @Override
        public void onPcmData(byte[] bytes, int length) {
            mPcmListener.onPcmData(bytes);
        }
    };


}
