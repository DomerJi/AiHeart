package com.thfw.aiui.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.util.Log;
import android.view.View;

import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.aiui.AIUISetting;
import com.thfw.aiui.R;
import com.thfw.aiui.WarningVoice;
import com.thfw.aiui.cae.CaeOperator;
import com.thfw.aiui.cae.OnCaeOperatorlistener;
import com.thfw.aiui.recorder.RecOperator;
import com.thfw.aiui.recorder.RecordListener;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Author:pengs
 * Date: 2022/7/19 13:19
 * Describe:cae 声源定位
 */
public class CaeWakeupHelper3 {

    private static final String TAG = CaeWakeupHelper3.class.getSimpleName();
    private static int ret = 0;
    private static int ret_connect_server = 0;
    private static String strTip = "";
    private static Context appContext;
    // 多麦克算法库
    private CaeOperator mCaeOperator;
    private RecOperator mRecOperator;
    // AIUI
    private AIUIAgent mAIUIAgent = null;
    // AIUI工作状态
    private int mAIUIState = AIUIConstant.STATE_IDLE;

    // 录音机工作状态
    private static boolean isRecording = false;
    // 写音频线程工作中
    private static boolean isWriting = false;
    private static WarningVoice warningVoice;

    private static void init(Context context) {
        appContext = context.getApplicationContext();
        CaeOperator.AUTH_SN = FileUtil.getCPUSerial();
        warningVoice = new WarningVoice(appContext, AudioManager.STREAM_ALARM);
        LogUtils.d(TAG, "CaeOperator.AUTH_SN -> " + CaeOperator.AUTH_SN);
        // 资源拷贝
        CaeOperator.portingFile(context);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.init_sdk) {
            initSDK();
        } else if (id == R.id.btnRec) {
            startReord();
        } else if (id == R.id.btnStop) {
            stopRecord();
        } else if (id == R.id.btnSave) {
            saveAudio();
        }
    }


    private void initSDK() {
        // 初始化AIUI
        createAgent();
        // 初始化CAE
        initCaeEngine();
        // 初始化alsa录音
        initAlsa();
    }

    /**
     * 读取AIUI配置
     */
    private String getAIUIParams() {
        String params = "";
        AssetManager assetManager = appContext.getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 初始化AIUI
     */
    private void createAgent() {
        if (null == mAIUIAgent) {
            Log.i(TAG, "create aiui agent");
           /*
            定义设备唯一标识(SN)
            1. 初始化SDK且处于联网状态，就会激活授权。因此客户要自定义设备sn
            2. 设备sn不可重复
            3. AIUI 与CAE的sn要一样，不然后期CAE和AIUI数据无法对齐
            4. 同一台机器有刷机次数限制，频繁刷机sn失效
            5. sn不能添加特殊字符，长度<64位
            */
            AIUISetting.setSystemInfo(AIUIConstant.KEY_SERIAL_NUM, CaeOperator.AUTH_SN);
            mAIUIAgent = AIUIAgent.createAgent(appContext, getAIUIParams(), mAIUIListener);
        }

        if (null == mAIUIAgent) {
            strTip = "AIUI初始化失败!";
        } else {
            strTip = "AIUI初始化成功!";
        }
        setText(strTip);
        setText("---------create_AIUI---------");

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


    private void startReord() {
        if (!isRecording && mRecOperator != null) {
            if (isWriting) {
                setText("正在写音频测试中，等结束后再开启录音测试");
                setText("---------start_alsa_record---------");
                return;
            }
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

    private void stopRecord() {
        if (isRecording && mRecOperator != null) {
            mRecOperator.stopRecord();
            mCaeOperator.stopSaveAudio();
            isRecording = false;
            setText("停止录音");
            setText("---------stop_alsa_record---------");
        }
    }

    private void saveAudio() {
        if (mCaeOperator != null) {
            if (!mCaeOperator.isAudioSaving()) {//默认为false
                mCaeOperator.startSaveAudio();
            } else {
                mCaeOperator.stopSaveAudio();
            }
        }
    }


    /**
     * AIUI 回调信息处理
     */
    private AIUIListener mAIUIListener = new AIUIListener() {
        @Override
        public void onEvent(AIUIEvent event) {
            switch (event.eventType) {
                case AIUIConstant.EVENT_CONNECTED_TO_SERVER:
                    LogUtils.i(TAG, "已连接服务器");
                    ret_connect_server = 1;
                    break;

                case AIUIConstant.EVENT_SERVER_DISCONNECTED:
                    LogUtils.i(TAG, "与服务器断开连接");
                    ret_connect_server = 0;
                    break;

                case AIUIConstant.EVENT_WAKEUP:
                    LogUtils.i(TAG, "进入识别状态");
                    break;

                case AIUIConstant.EVENT_RESULT:
                    try {
                        JSONObject bizParamJson = new JSONObject(event.info);
                        JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                        JSONObject params = data.getJSONObject("params");
                        JSONObject content = data.getJSONArray("content").getJSONObject(0);

                        if (content.has("cnt_id")) {
                            String cnt_id = content.getString("cnt_id");
                            JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));

                            String sid = event.data.getString("sid");

                            String sub = params.optString("sub");
                            JSONObject result = cntJson.optJSONObject("intent");
                            if ("nlp".equals(sub) && result.length() > 2) {
                                LogUtils.i(TAG, "nlp result :" + result.toString());
                                // 解析得到语义结果
                                String str = "";
                                //在线语义结果
                                String iatRes = result.optString("text");
                                if (result.optInt("rc") == 0) {
                                    str = "语义rc=0，识别内容：" + iatRes;
                                } else {
                                    str = "语义rc≠0，识别内容：" + iatRes;
                                }
                                setText(str);
                                setText("---------nlp_reslut---------");

                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case AIUIConstant.EVENT_ERROR:
                    setText("错误: " + event.arg1 + "\n" + event.info);
                    setText("---------error_aiui---------");
                    break;

                case AIUIConstant.EVENT_VAD:
                    if (AIUIConstant.VAD_BOS == event.arg1) {
                        LogUtils.i("找到vad_bos");
                    } else if (AIUIConstant.VAD_BOS_TIMEOUT == event.arg1) {
                        LogUtils.i("前端点超时");
                    } else if (AIUIConstant.VAD_EOS == event.arg1) {
                        LogUtils.i("找到vad_eos");
                    } else {
                        LogUtils.i(TAG, "event_vad" + event.arg2);
                    }
                    break;
                case AIUIConstant.EVENT_SLEEP:
                    LogUtils.i(TAG, "设备进入休眠");
                    break;

                case AIUIConstant.EVENT_START_RECORD:
                    LogUtils.i(TAG, "已开始录音");
                    break;

                case AIUIConstant.EVENT_STOP_RECORD:
                    LogUtils.i(TAG, "已停止录音");
                    break;

                case AIUIConstant.EVENT_STATE:    // 状态事件
                    mAIUIState = event.arg1;
                    if (AIUIConstant.STATE_IDLE == mAIUIState) {
                        // 闲置状态，AIUI未开启
                        LogUtils.i(TAG, "event state is STATE_IDLE");
                    } else if (AIUIConstant.STATE_READY == mAIUIState) {
                        // AIUI已就绪，等待唤醒
                        LogUtils.i(TAG, "event state is STATE_READY");
                    } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                        // AIUI工作中，可进行交互
                        LogUtils.i(TAG, "event state is STATE_WORKING");
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * CAE 回调消息处理
     */
    private OnCaeOperatorlistener onCaeOperatorlistener = new OnCaeOperatorlistener() {
        @Override
        public void onAudio(byte[] audioData, int dataLen) {
            // CAE降噪后音频写入AIUI SDK进行语音交互
            String params = "data_type=audio,sample_rate=16000";
            AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, audioData);
            mAIUIAgent.sendMessage(msg);
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
            warningVoice.playWarning();

            setText("唤醒成功,angle:" + a + " beam:" + b);
            setText("---------WAKEUP_CAE---------");
            // CAE SDK触发唤醒后给AIUI SDK发送手动唤醒事件：让AIUI SDK置于工作状态
            AIUIMessage resetWakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(resetWakeupMsg);

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

    private void destroyRecord() {
        if (null != mRecOperator && null != mCaeOperator) {
            mRecOperator.stopRecord();
            mCaeOperator.stopSaveAudio();
        } else {
            LogUtils.d(TAG, "distoryCaeEngine is Done!");
        }
    }

    private void setText(final String str) {
        LogUtils.d(TAG, "setText str -> " + str);

    }


}
