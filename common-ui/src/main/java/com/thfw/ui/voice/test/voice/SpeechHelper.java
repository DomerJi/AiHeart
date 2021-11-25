//package com.thfw.ui.voice.test.voice;
//
//import android.app.Activity;
//import android.content.SharedPreferences;
//import android.content.res.Configuration;
//import android.graphics.Point;
//import android.graphics.drawable.AnimationDrawable;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.animation.CycleInterpolator;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import com.iflytek.cloud.ErrorCode;
//import com.iflytek.cloud.InitListener;
//import com.iflytek.cloud.RecognizerListener;
//import com.iflytek.cloud.RecognizerResult;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechError;
//import com.iflytek.cloud.SpeechRecognizer;
//import com.lzf.easyfloat.EasyFloat;
//import com.lzf.easyfloat.enums.ShowPattern;
//import com.lzf.easyfloat.enums.SidePattern;
//import com.lzf.easyfloat.permission.PermissionUtils;
//import com.myai.base.BaseApplication;
//import com.myai.base.ContextApp;
//import com.myai.base.R;
//import com.myai.base.face.InstructExecuteListener;
//import com.myai.base.face.MyRecognizerListener;
//import com.myai.base.util.JsonParser;
//import com.myai.base.util.Utils;
//import com.thfw.ui.utils.FixSizeLinkedList;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//
///**
// * Author:pengs
// * Date: 2021/8/9 13:02
// * Describe:语音听写全局使用
// */
//public class SpeechHelper {
//
//    private static final String TAG = SpeechHelper.class.getSimpleName();
//    private static final String PREFER_NAME = "cn.soulbuddy.xymy.activity.setting";
//    private static final long VAD_EOS = 1600;
//    private static final long VAD_BOS = 6000;
//    private static final String DEFAULT_TEXT = "...";
//    private static final String DEFAULT_EMPTY = "";
//    private static Point sPoint;
//    private final SharedPreferences mSharedPreferences;
//    private final ArrayList<MyRecognizerListener> recognizerListeners;
//    private final StringBuffer recognizeResult;
//    public MyRecognizerListener myRecognizerListener;
//    // 是否唤醒中断tts
//    boolean wakeUperTtsPause = false;
//    private SpeechRecognizer mIat;
//    // 是否显示语音转文字结果
//    private boolean isWakeuper;
//    private AnimationDrawable mLoadingAnimationDrawable;
//    private Handler handlerFloatWindow;
//    private boolean playResume;
//    private boolean windowTextVisible = true;
//    private boolean firstIsShowing = true;
//    private boolean wakeuperTtsPause;
//    private PlayListener playListener;
//    private FixSizeLinkedList<Integer> volumeList;
//    private static final int VOLUME_SIZE = 40; // 1600/40
//
//
//    private InstructExecuteListener executeListener;
//    private boolean executeShow = false;
//    private long executeShowTime;
//    private static final long MIN_TIME = 800;
//    private static final long MAX_TIME = 3000;
//    private ProgressBar progressBar;
//    private ImageView imageView;
//    private TextView mTvVoice;
//    private Handler executeHandler;
//
//    private SpeechHelper() {
//        mSharedPreferences = ContextApp.get().getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
//        initRecognizerListener();
//        recognizerListeners = new ArrayList<>();
//        recognizeResult = new StringBuffer();
//        volumeList = new FixSizeLinkedList<>(VOLUME_SIZE);
//        executeListener = new InstructExecuteListener() {
//            @Override
//            public void begin() {
//                setExecuteShow(true);
//            }
//
//            @Override
//            public void end() {
//                executeCancelTime();
//                setExecuteShow(false);
//            }
//
//            @Override
//            public void timeOut() {
//                setExecuteShow(false);
//            }
//        };
//        init();
//    }
//
//    public void onActivityPause() {
//        if (executeShow) {
//            executeCancelTime();
//            executeShowTime = 0;
//            setExecuteShow(false);
//        }
//    }
//
//    private void executeCancelTime() {
//        if (executeHandler != null) {
//            executeHandler.removeCallbacksAndMessages(null);
//            return;
//        }
//    }
//
//    private void executeStarTime(int what, long delayMillis) {
//        if (executeHandler != null) {
//            executeCancelTime();
//            executeHandler.sendEmptyMessageDelayed(what, delayMillis);
//            return;
//        }
//        executeHandler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == 0) {
//                    if (executeShow) {
//                        if (executeListener != null) {
//                            executeListener.timeOut();
//                        }
//                    }
//                } else {
//                    setExecuteShow(false);
//                }
//            }
//        };
//    }
//
//    public void executeEnd() {
//        if (executeListener != null) {
//            executeListener.end();
//        }
//    }
//
//    public static SpeechHelper getInstance() {
//        return Factory.speechHelper;
//    }
//
//    public void clearVolumeList() {
//        volumeList.clear();
//    }
//
//    public long getVadBos() {
//        if (wakeUperTtsPause) {
//            wakeUperTtsPause = false;
//            return 4000;
//        } else if (WakeuperHelper.getInstance().isSpeech()) {
//            return 10000;
//        } else {
//            return VAD_BOS;
//        }
//
//    }
//
//    private void clearBuffer() {
//        recognizeResult.setLength(0);
//    }
//
//    public boolean isSpeechBuffer() {
//        Log.d(TAG, "volumeList = " + Arrays.toString(volumeList.toArray()));
//        return isListening() && (recognizeResult.length() > 0);
//    }
//
//    public boolean isVolume() {
//        if (volumeList.size() < 5) {
//            return false;
//        }
//        int countZero = 0;
//        for (Integer volume : volumeList) {
//            if (volume >= 15) {
//                countZero++;
//            }
//        }
//        return countZero * 1f / volumeList.size() > 0.60f;
//    }
//
//    private void init() {
//        Log.e(TAG, "mIat begin init");
//        // 初始化识别无UI识别对象
//        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
//        mIat = SpeechRecognizer.createRecognizer(ContextApp.get(), new InitListener() {
//            @Override
//            public void onInit(int i) {
//                if (i != ErrorCode.SUCCESS) {
//                    Log.e(TAG, "onInit -> " + i);
//                }
//            }
//        });
//    }
//
//    public void stopRecognize() {
//        stopRecognize(300);
//    }
//
//    public void stopRecognize(long delayTime) {
//        if (isListening()) {
//            mIat.stopListening();
//        }
//        delayHideWindow(delayTime);
//    }
//
//    public void startRecognizeByWakeuper() {
//        boolean ttsPause = false;
//        if (TtsHelper.getInstance().isSpeaking()) {
//            // 唤醒中断TTs
//            TtsHelper.getInstance().pauseTTS();
//            ttsPause = true;
//            wakeuperTtsPause = true;
//        }
//        stopRecognize();
//        startRecognize();
//        wakeUperTtsPause = ttsPause;
//    }
//
//    /**
//     * 讯飞SDK接口封装。公共接口，可调用
//     */
//    public void startRecognize() {
//        if (executeShow) {
//            Log.e(TAG, "executeShow = true");
//            return;
//        }
//        if (mIat == null) {
//            Log.e(TAG, "mIat is not init");
//            init();
//        }
//
//        if (isListening()) {
//            Log.i(TAG, "语音识别中....");
//            if (TtsHelper.getInstance().isSpeaking()) {
//                stopRecognize();
//            }
//            return;
//        }
//
//        if (TtsHelper.getInstance().isSpeaking()) {
//            Log.i(TAG, "语音播放中....");
//            return;
//        }
//
//        if (WakeuperHelper.getInstance().isListening()) {
//            WakeuperHelper.getInstance().stopWakeup();
//        }
//        setParam();
//        int code = mIat.startListening(myRecognizerListener);
//        if (code != ErrorCode.SUCCESS) {
//            Log.e(TAG, "startRecognize->" + code);
//        }
//        wakeUperTtsPause = false;
//    }
//
//    public boolean isListening() {
//        return mIat != null && mIat.isListening();
//    }
//
//    public synchronized void addListener(MyRecognizerListener recognizerListener) {
//        if (!recognizerListeners.contains(recognizerListener)) {
//            recognizerListeners.add(recognizerListener);
//        }
//    }
//
//    public synchronized void removeListener(MyRecognizerListener recognizerListener) {
//        if (recognizerListeners.contains(recognizerListener)) {
//            recognizerListeners.remove(recognizerListener);
//        }
//    }
//
//    public void notifyResult(String result) {
//        Log.i(TAG, "notifyResult -> " + result);
//        for (MyRecognizerListener recognizerListener : recognizerListeners) {
//            recognizerListener.onResult(result);
//        }
//    }
//
//    /**
//     * 参数设置
//     *
//     * @return
//     */
//    private void setParam() {
//        if (mIat == null) {
//            return;
//        }
//        // 清空参数
//        mIat.setParameter(SpeechConstant.PARAMS, null);
//        // 设置引擎
//        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//        // 设置返回结果格式
//        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
//        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
//        mIat.setParameter(SpeechConstant.VAD_BOS, String.valueOf(getVadBos()));
//        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//        mIat.setParameter(SpeechConstant.VAD_EOS, String.valueOf(VAD_EOS));
//        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
//        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
//        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.MIC);
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
////        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
////        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, ContextApp.get().getCacheDir().getAbsolutePath() + "/msc/iat.wav");
//        // 通过设置此参数可偏向输出数字结果格式
//        mIat.setParameter("nunum", "1");
//    }
//
//    private void initRecognizerListener() {
//        myRecognizerListener = new MyRecognizerListener() {
//
//            @Override
//            public void onVolumeChanged(int i, byte[] bytes) {
//                Log.d(TAG, "onVolumeChanged...volume -> " + i);
//                volumeList.add(i);
//                for (RecognizerListener recognizerListener : recognizerListeners) {
//                    if (recognizerListener != null) {
//                        recognizerListener.onVolumeChanged(i, bytes);
//                    }
//                }
//            }
//
//            @Override
//            public void onBeginOfSpeech() {
//                Log.d(TAG, "onBeginOfSpeech");
//                for (RecognizerListener recognizerListener : recognizerListeners) {
//                    if (recognizerListener != null) {
//                        recognizerListener.onBeginOfSpeech();
//                    }
//                }
//                wakeState();
//            }
//
//            @Override
//            public void onEndOfSpeech() {
//                Log.d(TAG, "onEndOfSpeech");
//                for (RecognizerListener recognizerListener : recognizerListeners) {
//                    if (recognizerListener != null) {
//                        recognizerListener.onEndOfSpeech();
//                    }
//                }
//                if (wakeUperTtsPause && recognizeResult.length() == 0) {
//                    wakeUperTtsPause = false;
//                    Log.d(TAG, "onEndOfSpeech -> TtsHelper.getInstance().resumeTTS()");
//                    TtsHelper.getInstance().resumeTTS();
//                }
//                wakeState();
//            }
//
//            public void onResult(String result) {
//
//            }
//
//            @Override
//            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
//                String text = JsonParser.parseIatResult(recognizerResult.getResultString());
//                Log.i(TAG, "onResult -> text = " + text + ";isLast = " + isLast);
//                recognizeResult.append(replaceXiaoMi(text));
//                String result = recognizeResult.toString();
//                if (isLast) {
//                    if (result.endsWith("。")) {
//                        result = result.substring(0, result.length() - 1);
//                    }
//                    Log.i(TAG, "onResult -> " + result);
//                    setSpeechTextView(result);
//                    wakeState();
//                    if (TextUtils.isEmpty(result)) {
//                        return;
//                    }
//                    isWakeuper = false;
//                    executeListener.begin();
//                    for (MyRecognizerListener recognizerListener : recognizerListeners) {
//                        if (recognizerListener != null) {
//                            recognizerListener.onResult(result);
//                        }
//                    }
//                } else {
//                    setSpeechTextView(result);
//                }
//            }
//
//            private String replaceXiaoMi(String result) {
//                if (result.contains("小密")) {
//                    if (result.contains("小密小密")) {
//                        onWindowReply();
//                    }
//                    return result.replaceAll("小密", "");
//                } else if (result.contains("小蜜")) {
//                    if (result.contains("小蜜小蜜")) {
//                        onWindowReply();
//                    }
//                    return result.replaceAll("小蜜", "");
//                } else if (result.contains("小秘")) {
//                    return result.replaceAll("小秘", "");
//                } else if (result.contains("小米")) {
//                    return result.replaceAll("小米", "");
//                } else {
//                    return result;
//                }
//            }
//
//            @Override
//            public void onError(SpeechError speechError) {
//                Log.d(TAG, "onError -> ErrorCode = " + speechError.getErrorCode()
//                        + "; ErrorDescription = " + speechError.getErrorDescription()
//                        + "; isListening = " + isListening());
//                for (RecognizerListener recognizerListener : recognizerListeners) {
//                    if (recognizerListener != null) {
//                        recognizerListener.onError(speechError);
//                    }
//                }
//                wakeState();
//            }
//
//            @Override
//            public void onEvent(int eventType, int i1, int i2, Bundle bundle) {
//                Log.d(TAG, "onEvent -> eventType = " + eventType);
//                for (RecognizerListener recognizerListener : recognizerListeners) {
//                    if (recognizerListener != null) {
//                        recognizerListener.onEvent(eventType, i1, i2, bundle);
//                    }
//                }
//            }
//        };
//    }
//
//    public void wakeState() {
//        notifyPlayState(!isListening());
//
//        if (BaseApplication.getBaseApp().isBackground()) {
//            return;
//        }
//
//        if (!windowTextVisible) {
//            hideChangeText();
//            return;
//        }
//
//        if (isListening()) {
//            showFlow();
//        }
//
//        if (imageView == null) {
//            if (EasyFloat.getAppFloatView() != null) {
//                imageView = EasyFloat.getAppFloatView().findViewById(R.id.riv_voice_state);
//            }
//            if (imageView == null) {
//                return;
//            }
//        }
//
//        if (isListening()) {
//            if (mLoadingAnimationDrawable == null || !mLoadingAnimationDrawable.isRunning()) {
//                mLoadingAnimationDrawable = (AnimationDrawable) imageView.getDrawable();
//                imageView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mLoadingAnimationDrawable != null) {
//                            mLoadingAnimationDrawable.start();
//                        }
//                    }
//                });
//            }
//        } else {
//            if (mLoadingAnimationDrawable != null) {
//                mLoadingAnimationDrawable.stop();
//                mLoadingAnimationDrawable = null;
//            }
//            delayHideWindow(VAD_EOS);
//        }
//
//    }
//
//    public void hideChangeText() {
//        Log.i(TAG, "hideChangeText = " + isWindowShowing());
//        if (isWindowShowing()) {
//            if (!BaseApplication.isBackground() && executeShow) {
//                Log.i(TAG, "hideChangeText executeShow = " + executeShow);
//                delayHideWindow(VAD_EOS);
//                return;
//            }
//            if (isMainThread()) {
//                EasyFloat.hideAppFloat();
//                setSpeechTextView(DEFAULT_EMPTY);
//            } else {
//                new Handler(Looper.getMainLooper()) {
//                    @Override
//                    public void handleMessage(@NonNull Message msg) {
//                        super.handleMessage(msg);
//                        EasyFloat.hideAppFloat();
//                        setSpeechTextView(DEFAULT_EMPTY);
//                    }
//                }.sendEmptyMessage(0);
//            }
//        }
//    }
//
//    private void showFlow() {
//        Log.d(TAG, "showFlow()");
//        if (isWindowShowing()) {
//            Log.d(TAG, "showFlow() -> isShowing true");
//            return;
//        }
//
//        if (EasyFloat.getAppFloatView() != null) {
//            setSpeechTextView(DEFAULT_EMPTY);
//            EasyFloat.showAppFloat();
//            return;
//        }
//
//        if (!PermissionUtils.checkPermission(ContextApp.get())) {
//            return;
//        }
//
//        EasyFloat.with(ContextApp.get())
//                .setSidePattern(SidePattern.LEFT)
//                .setGravity(Gravity.BOTTOM, 0, -60)
//                .setShowPattern(ShowPattern.FOREGROUND)
//                .setLayout(Utils.isCongCongRobot() ? R.layout.layout_flow_recognizer_cc : R.layout.layout_flow_recognizer)
//                .show();
//
//        firstIsShowing = isWindowShowing();
//        Log.d(TAG, "create.show() -> isShowing " + firstIsShowing);
//
//    }
//
//    public void setTextVisible(boolean visible) {
//        if (visible != this.windowTextVisible) {
//            this.windowTextVisible = visible;
//            if (!windowTextVisible) {
//                hideChangeText();
//            }
//        }
//    }
//
//    public boolean isMainThread() {
//        return Looper.getMainLooper() == Looper.myLooper();
//    }
//
//    /**
//     * 设置展示的文字
//     *
//     * @param speech
//     */
//    private void setSpeechTextView(String speech) {
//        if (TextUtils.isEmpty(speech)) {
//            clearBuffer();
//        }
//
//        if (mTvVoice == null) {
//            if (EasyFloat.getAppFloatView() != null) {
//                mTvVoice = EasyFloat.getAppFloatView().findViewById(R.id.tv_voice);
//            }
//        }
//        if (mTvVoice != null) {
//            if (!TextUtils.isEmpty(speech)) {
//                mTvVoice.setText(speech);
//            } else {
//                mTvVoice.setText(DEFAULT_TEXT);
//            }
//        }
//    }
//
//
//    private void setExecuteShow(boolean show) {
//
//        if (executeShow == show) {
//            return;
//        }
//        Log.i(TAG, "setExecuteShow -> " + show);
//
//        if (show) {
//            executeShowTime = System.currentTimeMillis();
//            executeStarTime(0, MAX_TIME);
//        } else {
//            long time = System.currentTimeMillis() - executeShowTime;
//            if (time < MIN_TIME) {
//                executeStarTime(1, MIN_TIME - time);
//                return;
//            }
//        }
//
//        executeShow = show;
//        if (progressBar == null) {
//            if (EasyFloat.getAppFloatView() != null) {
//                progressBar = EasyFloat.getAppFloatView().findViewById(R.id.pb_bar);
//            }
//        }
//        if (progressBar != null) {
//            progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//            if (imageView != null) {
//                imageView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
//            }
//        }
//        if (!executeShow) {
//            if (WakeuperHelper.getInstance().isWakeuper()) {
//                hideChangeText();
//            }
//            setSpeechTextView(DEFAULT_EMPTY);
//        }
//    }
//
//    private void onWindowReply() {
//        if (isWindowShowing()) {
//            setSpeechTextView(DEFAULT_EMPTY);
//            if (progressBar == null) {
//                imageView = EasyFloat.getAppFloatView().findViewById(R.id.riv_voice_state);
//            }
//            if (imageView != null) {
//                imageView.animate().translationX(12).setInterpolator(new CycleInterpolator(2)).setDuration(400);
//            }
//        }
//    }
//
//    private void delayHideWindow(long delayTime) {
//        if (!isWindowShowing()) {
//            return;
//        }
//
//        if (delayTime <= 0) {
//            hideChangeText();
//            if (handlerFloatWindow != null) {
//                handlerFloatWindow.removeCallbacksAndMessages(null);
//            }
//        } else if (handlerFloatWindow != null) {
//            handlerFloatWindow.removeCallbacksAndMessages(null);
//            handlerFloatWindow.sendEmptyMessageDelayed(0, delayTime);
//        } else {
//            handlerFloatWindow = new Handler(Looper.getMainLooper()) {
//                @Override
//                public void handleMessage(@NonNull Message msg) {
//                    super.handleMessage(msg);
//                    if (!isListening()) {
//                        hideChangeText();
//                    }
//                }
//            };
//            handlerFloatWindow.sendEmptyMessageDelayed(0, delayTime);
//        }
//    }
//
//    public boolean isWindowShowing() {
//        return EasyFloat.appFloatIsShow();
//    }
//
//    public boolean isScreenOriatationPortrait() {
//        return ContextApp.get().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
//    }
//
//    public void setPlayListener(PlayListener playListener) {
//        this.playListener = playListener;
//    }
//
//    public void removePlayListener() {
//        this.playListener = null;
//    }
//
//    public void notifyPlayState(boolean resume) {
//        if (playResume != resume) {
//            playResume = resume;
//            if (!resume) {
//                if (playListener != null) {
//                    playListener.onResume(playResume);
//                }
//                return;
//            }
//            new Handler(Looper.getMainLooper()) {
//                @Override
//                public void handleMessage(@NonNull Message msg) {
//                    super.handleMessage(msg);
//                    if (playListener != null) {
//                        playListener.onResume(playResume);
//                    }
//                }
//            }.sendEmptyMessageDelayed(0, 1000);
//        }
//    }
//
//    public interface PlayListener {
//        void onResume(boolean resume);
//    }
//
//    public static class Factory {
//        private static SpeechHelper speechHelper = new SpeechHelper();
//    }
//
//}
