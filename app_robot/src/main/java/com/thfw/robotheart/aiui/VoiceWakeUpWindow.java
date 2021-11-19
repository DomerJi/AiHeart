package com.thfw.robotheart.aiui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * 语音唤醒弹框
 */
public class VoiceWakeUpWindow {

    private static final String TAG = VoiceWakeUpWindow.class.getSimpleName();
    // 设置门限值 ： 门限值越低越容易被唤醒
    private final static int MAX = 3000;
    private final static int MIN = 0;
    private static volatile VoiceWakeUpWindow voiceWakeUpWindow;
    /**
     * 唤醒应答语
     */
    private static String[] wakeCalls = new String[]{"我在", "在呢", "请指教", "听着呢"};
    private Context mContext;
    private int curThresh = 1150;
    private String threshStr = "门限值：";
    // 0：单次唤醒   1：循环唤醒
    private String keep_alive = "1";
    private String ivwNetMode = "0";

    private SpeechRecognizer speechRecognizer;
    private TTSLocalManager mTts;
    private RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            LogUtil.d(TAG, "resultString -> speech onVolumeChanged");
            wakeState();
        }

        @Override
        public void onBeginOfSpeech() {
            wakeState();
            LogUtil.d(TAG, "resultString -> speech onBeginOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
            wakeState();
            LogUtil.d(TAG, "resultString -> speech onEndOfSpeech");
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String resultString = recognizerResult.getResultString();
            LogUtil.d(TAG, "resultString -> speech " + resultString);
            printResult(recognizerResult);
            wakeState();
        }

        @Override
        public void onError(SpeechError speechError) {
            ToastUtil.show(speechError.getPlainDescription(true) + speechError.getErrorCode());
            wakeState();
            LogUtil.d(TAG, "resultString -> speech onError");
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            wakeState();
            LogUtil.d(TAG, "resultString -> speech onEvent");
        }
    };
    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            String callWords = wakeCalls[new Random().nextInt(20) % wakeCalls.length];
            setSpeechTextView(callWords);
            mTts.start(callWords, new TTSLocalManager.CustomSynthesizerListener() {
                @Override
                public void onCompleted(SpeechError speechError) {
                    super.onCompleted(speechError);
                    initRecognizer();
                    wakeState();
                }
            });
            String resultString;
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 " + text);
                buffer.append("\n");
                buffer.append("【操作类型】" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】" + object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString = buffer.toString();
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            LogUtil.d(TAG, resultString);
        }

        @Override
        public void onError(SpeechError error) {
            ToastUtil.show(error.getPlainDescription(true) + error.getErrorCode());
        }

        @Override
        public void onBeginOfSpeech() {
            showFlow();
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            switch (eventType) {
                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
                case SpeechEvent.EVENT_RECORD_DATA:
                    final byte[] audio = obj.getByteArray(SpeechEvent.KEY_EVENT_RECORD_DATA);
                    Log.i(TAG, "ivw audio length: " + audio.length);
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int volume) {
        }
    };

    private VoiceWakeUpWindow() {
        mContext = ToastUtil.getAppContext();
        initWakeUp();
        initTts();
    }

    public static VoiceWakeUpWindow getInstance() {
        if (voiceWakeUpWindow == null) {
            synchronized (VoiceWakeUpWindow.class) {
                if (voiceWakeUpWindow == null) {
                    voiceWakeUpWindow = new VoiceWakeUpWindow();
                }
            }
        }
        return voiceWakeUpWindow;
    }

    private void initTts() {
        mTts = new TTSLocalManager(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                LogUtil.e(TAG, "TTSLocalManager -> onInit( " + i + " )");
            }
        });
    }

    public void show() {
        VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw == null) {
            return;
        }
        mIvw.startListening(mWakeuperListener);
    }

    public void dismiss() {
        if (FloatWindow.get() != null) {
            FloatWindow.get().hide();
        }

        VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
            mIvw = null;
        }

        if (mTts != null) {
            mTts.destroy();
            mTts = null;
        }

        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    private void initWakeUp() {
        VoiceWakeuper.createWakeuper(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                LogUtil.e(TAG, "VoiceWakeuper -> onInit( " + i + " )");
            }
        });
        VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw == null) {
            return;
        }
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
        // 设置唤醒模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
        // 设置持续进行唤醒
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
        // 设置闭环优化网络模式
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
        // 设置唤醒资源路径
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
        // 设置唤醒录音保存路径，保存最近一分钟的音频
        mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, mContext.getCacheDir().getPath() + "/msc/ivw.wav");
        mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
        //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
        // 启动唤醒
//        mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
    }

    /**
     * 唤醒资源路径
     *
     * @return
     */
    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + mContext.getResources().getString(R.string.app_id) + ".jet");
        Log.d("getResource", "resPath: " + resPath);
        return resPath;
    }

    private void initRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
                @Override
                public void onInit(int i) {
                    LogUtil.e(TAG, "SpeechRecognizer = init = " + i);
                }
            });

            //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
            speechRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
            speechRecognizer.setParameter(SpeechConstant.SUBJECT, null);
            //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
            speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
            //此处engineType为“cloud”
            speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //设置语音输入语言，zh_cn为简体中文
            speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            //设置结果返回语言
            speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
            // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
            //取值范围{1000～10000}
            speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "3000");
            //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
            //自动停止录音，范围{0~10000}
            speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "8000");
            //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
            speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");
        }
        if (!speechRecognizer.isListening()) {
            speechRecognizer.startListening(recognizerListener);
        }

    }


    private void wakeState() {
        if (FloatWindow.get() != null) {
            RoundedImageView mRivVoiceState = FloatWindow.get().getView().findViewById(R.id.riv_voice_state);
            if (mRivVoiceState == null) {
                return;
            }
            if (speechRecognizer != null && speechRecognizer.isListening()) {
                mRivVoiceState.setImageResource(R.drawable.flow_voice_start);
            } else {
                mRivVoiceState.setImageResource(R.drawable.flow_voice_stop);
            }
        }


    }

    private void showFlow() {
        LogUtil.d(TAG, "showFlow()");
        if (FloatWindow.get() != null && FloatWindow.get().isShowing()) {
            return;
        }
        FloatWindow
                .with(mContext)
                .setView(R.layout.layout_flow_recognizer)
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)                               //设置控件宽高
                .setHeight(Screen.width, 0.2f)
                .setX(100)                                   //设置控件初始位置
                .setY(Screen.height, 0.3f)
                .setDesktopShow(true)                        //桌面显示
                .setViewStateListener(new ViewStateListener() {
                    @Override
                    public void onPositionUpdate(int x, int y) {

                    }

                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onHide() {

                    }

                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onMoveAnimStart() {

                    }

                    @Override
                    public void onMoveAnimEnd() {

                    }

                    @Override
                    public void onBackToDesktop() {

                    }
                })    //监听悬浮控件状态改变
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail() {

                    }
                })  //监听权限申请结果
                .build();
        //手动控制
        FloatWindow.get().show();
    }

    /**
     * sn	sentence	number	第几句
     * ls	last sentence	boolean	是否最后一句
     * bg	begin	number	保留字段，无需关注
     * ed	end	number	保留字段，无需关注
     * ws	words	array	词
     * cw	chinese word	array	中文分词
     * w	word	string	单字
     * sc	score	number	分数
     *
     * @param results
     */
    private void printResult(RecognizerResult results) {
        LogUtil.e("printResult->" + results.getResultString());
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            String sn = resultJson.optString("sn");
            JSONArray jsonArray = resultJson.optJSONArray("ws");
            if (jsonArray != null) {
                StringBuffer stringBuffer = new StringBuffer();
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject != null) {
                        JSONArray cwArray = jsonObject.optJSONArray("cw");
                        if (cwArray != null && cwArray.length() > 0) {
                            JSONObject object = cwArray.optJSONObject(0);
                            if (object != null) {
                                String words = object.optString("w");
                                stringBuffer.append(words);
                            }
                        }
                    }
                }
                setSpeechTextView(stringBuffer.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置展示的文字
     *
     * @param speech
     */
    private void setSpeechTextView(String speech) {
        if (FloatWindow.get() != null) {
            TextView mTvVoice = FloatWindow.get().getView().findViewById(R.id.tv_voice);
            if (mTvVoice != null) {
                if (!TextUtils.isEmpty(speech)) {
                    mTvVoice.setText(speech);
                    mTvVoice.setVisibility(View.VISIBLE);
                } else {
                    mTvVoice.setText("");
                    mTvVoice.setVisibility(View.GONE);
                }

            }
        }
    }


}
