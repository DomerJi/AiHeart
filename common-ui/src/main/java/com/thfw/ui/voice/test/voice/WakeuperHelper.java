//package com.thfw.ui.voice.test.voice;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.iflytek.cloud.ErrorCode;
//import com.iflytek.cloud.InitListener;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechError;
//import com.iflytek.cloud.SpeechEvent;
//import com.iflytek.cloud.VoiceWakeuper;
//import com.iflytek.cloud.WakeuperListener;
//import com.iflytek.cloud.WakeuperResult;
//import com.iflytek.cloud.util.ResourceUtil;
//import com.myai.base.ContextApp;
//import com.thfw.ui.voice.WakeType;
//import com.thfw.ui.voice.speech.SpeechHelper;
//import com.thfw.ui.voice.tts.TtsHelper;
//
//import java.io.InputStream;
//
//
///**
// * Author:pengs
// * Date: 2021/8/12 10:20
// * Describe:语音唤醒
// */
//public class WakeuperHelper {
//    private static final String TAG = WakeuperHelper.class.getSimpleName();
//    // for wakeup
//    private static final int curThresh = 1600;
//    private static final String keep_alive = "1";
//    private static final String ivwNetMode = "0";
//    private static final long TOUR = 200;
//    // 主要分为两种：唤醒（wakeup），唤醒识别（oneshot）
//    private static final String IVW_SST_WAKEUP = "wakeup";
//    private static final String IVW_SST_ONESHOT = "oneshot";
//    private VoiceWakeuper mIvw;
//    private MyWakeUperListener myWakeUperListener;
//    private WakeuperListener sleepWakeUperListener;
//    private volatile WakeType wakeType;
//    private Handler handler;
//
//    private WakeuperHelper() {
//        myWakeUperListener = new MyWakeUperListener();
//        initWakeuper();
//    }
//
//    public static WakeuperHelper getInstance() {
//        return Factory.helper;
//    }
//
//    /**
//     * 读取asset目录下文件。
//     *
//     * @return content
//     */
//    public static String readFile(Context mContext, String file, String code) {
//        int len = 0;
//        byte[] buf = null;
//        String result = "";
//        try {
//            InputStream in = mContext.getAssets().open(file);
//            len = in.available();
//            buf = new byte[len];
//            in.read(buf, 0, len);
//
//            result = new String(buf, code);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    /**
//     * 巡视
//     */
//    private void tour() {
//        if (handler != null) {
//            handler.removeCallbacksAndMessages(null);
//            if (isWakeuper()) {
//                SpeechHelper.getInstance().onActivityPause();
//                SpeechHelper.getInstance().stopRecognize(0);
//            }
//            handler.sendEmptyMessage(0);
//            return;
//        }
//        handler = new Handler(Looper.myLooper()) {
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                super.handleMessage(msg);
//                switch (WakeuperHelper.this.wakeType) {
//                    case SPEECH:
//                        if (!SpeechHelper.getInstance().isListening()) {
//                            // tts播放过程中允许开启唤醒打断
//                            if (TtsHelper.getInstance().isSpeaking()) {
//                                startWakeup();
//                            } else {
//                                SpeechHelper.getInstance().startRecognize();
//                            }
//                        } else {
//                            // tts播放过程中关闭 识别
//                            if (TtsHelper.getInstance().isSpeaking()) {
//                                SpeechHelper.getInstance().stopRecognize();
//                            }
//                        }
//                        handler.sendEmptyMessageDelayed(0, TOUR);
//                        break;
//                    default:
//                        if (!isListening() && !SpeechHelper.getInstance().isListening()) {
//                            startWakeup();
//                        }
//                        handler.sendEmptyMessageDelayed(0, TOUR);
//                        break;
//                }
//            }
//        };
//        handler.sendEmptyMessage(0);
//    }
//
//    public WakeType getWakeType() {
//        return wakeType;
//    }
//
//    public void openWakeTypeSpeech() {
//        setWakeType(WakeType.SPEECH);
//    }
//
//    public void closeWakeTypeSpeech() {
//        SpeechHelper.getInstance().clearVolumeList();
//        setWakeType(WakeType.WAKEUPER);
//    }
//
//    public void setWakeType(WakeType wakeType) {
//        Log.d(TAG, "setWakeType = " + wakeType);
//        if (wakeType != this.wakeType) {
//            this.wakeType = wakeType;
//            tour();
//        }
//    }
//
//    public boolean isWakeuper() {
//        return wakeType == WakeType.WAKEUPER;
//    }
//
//    public boolean isSpeech() {
//        return wakeType == WakeType.SPEECH;
//    }
//
//    public boolean isListening() {
//        return mIvw != null && mIvw.isListening();
//    }
//
//    private void initWakeuper() {
//        mIvw = VoiceWakeuper.getWakeuper();
//        Log.d(TAG, "initWakeuper = " + mIvw);
//        if (mIvw == null) {
//            Log.d(TAG, "createWakeuper = " + mIvw);
//            mIvw = VoiceWakeuper.createWakeuper(ContextApp.get(), new InitListener() {
//                @Override
//                public void onInit(int code) {
//                    Log.d(TAG, "InitListener init() code = " + code);
//                    if (code != ErrorCode.SUCCESS) {
//                        Log.e(TAG, "VoiceWakeuper onInit()" + code);
//                    }
//                }
//            });
//            Log.d(TAG, "createWakeuper end = " + mIvw);
//            startWakeup();
//        }
//    }
//
//    public synchronized void startWakeup() {
//
//        if (mIvw == null) {
//            initWakeuper();
//            return;
//        }
//
//        if (!isListening()) {
//            if (SpeechHelper.getInstance().isListening()) {
//                SpeechHelper.getInstance().stopRecognize();
//            }
//            setIVWParams();
//            int startCode = mIvw.startListening(myWakeUperListener);
//            if (startCode != ErrorCode.SUCCESS) {
//                Log.e(TAG, "mIvw" + startCode);
//            }
//        }
//    }
//
//    public void stopWakeup() {
//        if (mIvw != null) {
//            mIvw.stopListening();
//        }
//    }
//
//    public void addListener(WakeuperListener wakeuperListener) {
//        sleepWakeUperListener = wakeuperListener;
//        if (isListening()) {
//            sleepWakeUperListener.onBeginOfSpeech();
//        } else {
//            startWakeup();
//        }
//    }
//
//    public WakeuperListener getSleepWakeUperListener() {
//        return sleepWakeUperListener;
//    }
//
//    public boolean notifySpeechWakeuper() {
//        if (sleepWakeUperListener != null) {
//            sleepWakeUperListener.onResult(new WakeuperResult("{}"));
//            return true;
//        }
//        return false;
//    }
//
//    public void removeListener() {
//        sleepWakeUperListener = null;
//    }
//
//    private void setIVWParams() {
//        if (mIvw == null) {
//            return;
//        }
//        // 清空参数
//        mIvw.setParameter(SpeechConstant.PARAMS, null);
//        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
//        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
//        // 主要分为两种：唤醒（wakeup），唤醒识别（oneshot）
//        mIvw.setParameter(SpeechConstant.IVW_SST, IVW_SST_WAKEUP);
//        // 设置唤醒资源路径
//        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
//        // 设置持续进行唤醒
//        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
//        // 设置闭环优化网络模式
//        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
//        // 设置唤醒录音保存路径，保存最近一分钟的音频
////        mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, ContextApp.get().getCacheDir().getAbsolutePath() + "/msc/ivw.wav");
////        mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
//        //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
//    }
//
//    public void destroy() {
//        // 销毁唤醒对象
//        if (mIvw != null) {
//            mIvw.stopListening();
//            mIvw.destroy();
//            mIvw = null;
//        }
//    }
//
//    private String getResource() {
//        final String resPath = ResourceUtil.generateResourcePath(ContextApp.get(), ResourceUtil.RESOURCE_TYPE.assets, "ivw/5f3de60d.jet");
//        Log.d(TAG, "resPath: " + resPath);
//        return resPath;
//    }
//
//    public boolean isOneShot() {
//        return mIvw != null && IVW_SST_ONESHOT.equals(mIvw.getParameter(SpeechConstant.IVW_SST));
//    }
//
//    /**
//     * {"sst":"wakeup", "id":0, "score":2203,
//     * "bos":1750, "eos":2720 ,"keyword":"xiao3-mi4-xiao3-mi4"}
//     *
//     * @param result
//     */
//    private void onWakeupResult(WakeuperResult result) {
//        if (TtsHelper.getInstance().isSpeaking() && TtsHelper.getInstance().isSpeakingWakeup()) {
//            return;
//        }
//        // 唤醒后开启识别
//        SpeechHelper.getInstance().startRecognizeByWakeuper();
//
//        if (!"1".equalsIgnoreCase(keep_alive)) {
//            Log.d(TAG, "wakeup OK");
//        }
//    }
//
//    private static class Factory {
//        private static WakeuperHelper helper = new WakeuperHelper();
//    }
//
//    public class MyWakeUperListener implements WakeuperListener {
//
//        @Override
//        public void onResult(WakeuperResult result) {
//            Log.i(TAG, "WakeupListener onResult 唤醒！" + result.getResultString());
//            // tts 播报包含小密 禁止唤醒
//
//            onWakeupResult(result);
//            if (sleepWakeUperListener != null) {
//                sleepWakeUperListener.onResult(result);
//            }
//        }
//
//        @Override
//        public void onError(SpeechError error) {
//            Log.e(TAG, "onWakeupError: " + error.getPlainDescription(true));
//            if (sleepWakeUperListener != null) {
//                sleepWakeUperListener.onError(error);
//            }
//        }
//
//        @Override
//        public void onBeginOfSpeech() {
//            Log.i(TAG, "onWakeupStart 开始唤醒....");
//            if (sleepWakeUperListener != null) {
//                sleepWakeUperListener.onBeginOfSpeech();
//            }
//        }
//
//        @Override
//        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
//            Log.i(TAG, "onEvent eventType = " + eventType);
//            if (sleepWakeUperListener != null) {
//                sleepWakeUperListener.onEvent(eventType, isLast, arg2, obj);
//            }
//            switch (eventType) {
//                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
//                case SpeechEvent.EVENT_RECORD_DATA:
//                    final byte[] audio = obj.getByteArray(SpeechEvent.KEY_EVENT_RECORD_DATA);
//                    Log.i(TAG, "ivw audio length: " + audio.length);
//                    break;
//            }
//        }
//
//        @Override
//        public void onVolumeChanged(int volume) {
//            Log.d(TAG, "wake up onVolumeChanged: " + volume);
//        }
//    }
//}
