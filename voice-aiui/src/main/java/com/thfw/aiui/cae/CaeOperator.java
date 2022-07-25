package com.thfw.aiui.cae;


import android.content.Context;
import android.util.Log;

import com.iflytek.iflyos.cae.CAE;
import com.iflytek.iflyos.cae.ICAEListener;
import com.thfw.aiui.util.FileUtil;
import com.thfw.aiui.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CaeOperator {

    private static final String TAG = CaeOperator.class.getSimpleName();

    // 设备鉴权唯一标识
//    public static String AUTH_SN = "6dae24d7-f61d-4a05-93b3-85d052fc4b10";
    public static String AUTH_SN = "7daedrd9-f72c-4a05-93b3-85d052fc4b10";

    // 文件对应设备存储目录，需要与vtn.ini中设置一致
    private static String mWorkDir = "/sdcard/vtn/cae";

    // 音频数据保存开关
    public static boolean isSaveAudio = false;
    public static FileUtil mAlsaRawFileUtil;      // 保存alsa录音原始音频数据（多通道、16k、16/32bit）
    public static FileUtil mAlsaRecFileUtil;      // 保存alsa原始录音经转换后满足CAE音频（多通道、16k、32bit）
    public static FileUtil mCaeOutPutFileUtil;    // 保存降噪后录音（单通道）

    private static String mAlsaRawAudioDir = "/sdcard/vtn/cae/alsaRawAudio/";       // alsa录音原始音频保存路径
    private static String mAlsaRecAudioDir = "/sdcard/vtn/cae/alsaRecAudio/";       // alsa录音转换后原始音频保存路径
    private static String mCaeWriteAudioDir = "/sdcard/vtn/cae/caeOutPutAudio/";    // 降噪后音频保存路径

    // 信息透传回调监听
    private OnCaeOperatorlistener caeOperatorlistener;

    // CAE-SDK 结果回调监听器
    ICAEListener mCAEListener = new ICAEListener() {

        @Override
        public void onAudioCallback(byte[] bytes, int length) {
            caeOperatorlistener.onAudio(bytes, length);
//            LogUtils.w("CAEOutPutAudioo","bytes.length is : "+bytes.length+" , length is : "+ length);
            if (isSaveAudio)
                mCaeOutPutFileUtil.write(bytes);
        }

        @Override
        public void onIvwAudioCallback(byte[] bytes, int i) {

        }

        @Override
        public void onWakeup(String result) {
            LogUtils.d("onWakeup result = " + result);
            try {

                JSONObject wakeupResult = new JSONObject(result).optJSONObject("ivw");
                // 默认的 beam 取值不代表正式的波束，讯飞算法侧内部做了映射关系beam仅输出 0、1、2
                // 环形mic如果需要做手动唤醒指定波束（0~5），需要以 physical 取值作为波束取值
                int physicalBeam = wakeupResult.optInt("physical");
                caeOperatorlistener.onWakeup(wakeupResult.optInt("angle"), physicalBeam);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    // 初始化CAE
    public int initCAE(OnCaeOperatorlistener onCaeOperatorlistener) {
        caeOperatorlistener = onCaeOperatorlistener;
        CAE.loadLib();
        String configPath = String.format("%s/%s", mWorkDir, "resources/config/vtn.ini");
        int isInit = CAE.CAENew(AUTH_SN, configPath, mCAEListener);

        CAE.CAESetShowLog(0);           // Alsa-Jni日志控制 0-开启  1-关闭
        return isInit;
    }

    // 外部音频写入CAE SDK
    public void writeAudioTest(byte[] data) {
//        LogUtils.w("writeAudioTest","data length is "+ data.length );
        CAE.CAEAudioWrite(data, data.length);
    }


    // 设置拾音波束方向，内部唤醒后已经处理，不用重新调用
    public void setRealBeam(int beam) {
        CAE.CAESetRealBeam(beam);
    }

    // 获取CAE版本
    public String getCAEVersion() {
        return CAE.CAEGetVersion();
    }

    // 释放CAE资源
    public void releaseCae() {
        CAE.CAEDestory();
    }


    public void startSaveAudio() {
        isSaveAudio = true;
        mAlsaRawFileUtil.createPcmFile();
        mAlsaRecFileUtil.createPcmFile();
        mCaeOutPutFileUtil.createPcmFile();
    }

    public void stopSaveAudio() {
        isSaveAudio = false;
        if (mAlsaRawFileUtil != null) {
            mAlsaRawFileUtil.closeWriteFile();
        }

        if (mAlsaRecFileUtil != null) {
            mAlsaRecFileUtil.closeWriteFile();
        }
        if (mCaeOutPutFileUtil != null) {
            mCaeOutPutFileUtil.closeWriteFile();
        }
    }

    public void saveAduio(byte[] bytes, FileUtil util) {
        if (isSaveAudio) {
            util.write(bytes);
        }
    }


    // 资源文件拷贝
    public static void portingFile(Context context) {
        mAlsaRawFileUtil = new FileUtil(mAlsaRawAudioDir);
        mAlsaRecFileUtil = new FileUtil(mAlsaRecAudioDir);
        mCaeOutPutFileUtil = new FileUtil(mCaeWriteAudioDir);
        File file = new File(mWorkDir + "/resources/xiaomixiaomi_800_317.bin");
        File file2 = new File(mWorkDir + "/resources/vtn.ini");
        if (file != null && file.exists() && file.length() > 100
                && file2 != null && file2.exists() && file2.length() > 100) {
            Log.d(TAG, "--------------- resources exists ----------------");
            return;
        }
        copyAssetFolder(context, "resources", String.format("%s/resources", mWorkDir));

    }

    public static boolean copyAssetFolder(Context context, String srcName, String dstName) {
        try {
            boolean result = true;
            String fileList[] = context.getAssets().list(srcName);
            if (fileList == null) return false;

            if (fileList.length == 0) {
                result = copyAssetFile(context, srcName, dstName);
            } else {
                File file = new File(dstName);
                result = file.mkdirs();
                for (String filename : fileList) {
                    result &= copyAssetFolder(context, srcName + File.separator + filename, dstName + File.separator + filename);
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyAssetFile(Context context, String srcName, String dstName) {
        try {
            InputStream in = context.getAssets().open(srcName);
            File outFile = new File(dstName);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAudioSaving() {
        return isSaveAudio;
    }


}
