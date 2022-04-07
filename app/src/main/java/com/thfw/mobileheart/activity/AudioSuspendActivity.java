package com.thfw.mobileheart.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.aiui.TTSLocalManager;
import com.thfw.ui.base.BaseActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.media.AudioRecord.STATE_INITIALIZED;
import static com.just.agentweb.ActionActivity.REQUEST_CODE;

/**
 * Author:pengs
 * Date: 2021/8/4 11:06
 * Describe:语音中断demo
 */
public class AudioSuspendActivity extends BaseActivity {

    // 音频源：音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    // 音频通道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    // 音频格式：PCM编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MY_PERMISSIONS_REQUEST = 1001;
    String test = "东京的骄阳下，德约太累了。2021年7月，东京有明网球公园被盛夏的蝉鸣声所包围，" +
            "中午的气温可以达到35摄氏度，体感温度逼近50摄氏度。在这种火热的天气之下，带着为自己和" +
            "塞尔维亚代表团夺得一枚奥运金牌的渴望，刚刚在温网夺得第20座大满贯冠军的德约科维奇把自" +
            "己变成另外一团火，完完全全地投入到男单和混双的赛事当中。 从7月24日到7月31日，他在8" +
            "天里打了包括6场男单和3场混双之内的9场比赛。" +
            "超高的强度加上超高的气温，对于没有团队跟随、又是临时决定出战的他来说，都是巨大的消耗。" +
            " 最终，他以男单第四名和退出混双铜牌战结束了第4次奥运之旅，奥运会最好成绩依然是2008年北京的那枚铜牌。" +
            "“很糟糕，我感到很糟糕，我的比赛打得支离破碎。”他在男单半决赛不敌小兹维列夫之后说，“但是为塞尔维亚出战，" +
            "我并不后悔。”";
//    private TTSAndroidManager ttsAndroid;
    SynthesizerListener synthesizerListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            LogUtil.d("onSpeakBegin - ");
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
            LogUtil.d("onBufferProgress - ");
        }

        @Override
        public void onSpeakPaused() {
            LogUtil.d("onSpeakPaused - ");
        }

        @Override
        public void onSpeakResumed() {
            LogUtil.d("onSpeakResumed - ");
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
    private android.widget.TextView mTvText;
    private android.widget.RadioGroup mRgType;
    private android.widget.RadioButton mRbWord;
    private android.widget.RadioButton mRbTalk;
    private TTSLocalManager ttsLocalManager;
    // 唤醒配置
    private int curThresh = 1150;
    private String threshStr = "门限值：";
    // 0：单次唤醒   1：循环唤醒
    private String keep_alive = "1";
    private String ivwNetMode = "0";
    /**
     * 需要申请的运行时权限
     */
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String pcmFileName;
    private String wavFileName;

    // android 录音 ==============================
    private AudioRecord audioRecord = null;  // 声明 AudioRecord 对象
    private int recordBufSize = 0; // 声明recoordBufffer的大小字段
    private byte[] buffer;
    private boolean isRecording;

    /**
     * 音量判断，录音检测
     *
     * @param var0
     * @param var1
     * @return
     */
    public static int calculateVolume(byte[] var0, int var1) {
        int[] var3 = null;
        int var4 = var0.length;
        int var2;
        if (var1 == 8) {
            var3 = new int[var4];
            for (var2 = 0; var2 < var4; ++var2) {
                var3[var2] = var0[var2];
            }
        } else if (var1 == 16) {
            var3 = new int[var4 / 2];
            for (var2 = 0; var2 < var4 / 2; ++var2) {
                byte var5 = var0[var2 * 2];
                byte var6 = var0[var2 * 2 + 1];
                int var13;
                if (var5 < 0) {
                    var13 = var5 + 256;
                } else {
                    var13 = var5;
                }
                short var7 = (short) (var13 + 0);
                if (var6 < 0) {
                    var13 = var6 + 256;
                } else {
                    var13 = var6;
                }
                var3[var2] = (short) (var7 + (var13 << 8));
            }
        }

        int[] var8 = var3;
        if (var3 != null && var3.length != 0) {
            float var10 = 0.0F;
            for (int var11 = 0; var11 < var8.length; ++var11) {
                var10 += (float) (var8[var11] * var8[var11]);
            }
            var10 /= (float) var8.length;
            float var12 = 0.0F;
            for (var4 = 0; var4 < var8.length; ++var4) {
                var12 += (float) var8[var4];
            }
            var12 /= (float) var8.length;
            var4 = (int) (Math.pow(2.0D, (double) (var1 - 1)) - 1.0D);
            double var14 = Math.sqrt((double) (var10 - var12 * var12));
            int var9;
            if ((var9 = (int) (10.0D * Math.log10(var14 * 10.0D * Math.sqrt(2.0D) / (double) var4 + 1.0D))) < 0) {
                var9 = 0;
            }
            if (var9 > 10) {
                var9 = 10;
            }
            return var9;
        } else {
            return 0;
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_audio_suspend;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTvText = (TextView) findViewById(R.id.tv_text);
        mRgType = (RadioGroup) findViewById(R.id.rg_type);
        mRbWord = (RadioButton) findViewById(R.id.rb_word);
        mRbTalk = (RadioButton) findViewById(R.id.rb_talk);
        findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Process.killProcess(Process.myPid());
            }
        });
        mTvText.setText(test);

//        ttsAndroid = new TTSAndroidManager(mContext);
//        ttsAndroid.speak(test);
        ttsLocalManager = new TTSLocalManager(mContext, new InitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        ttsLocalManager.start(test, synthesizerListener);

        mRgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                change();
            }
        });
        change();

        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }
    }

    /**
     * 中断策略选择
     */
    private void change() {
        if (mRgType.getCheckedRadioButtonId() == R.id.rb_word) {
            ttsLocalManager.stopSpeaking();
            ttsLocalManager.start(test, synthesizerListener);
            stop();
            word();
        } else {
            if (VoiceWakeuper.getWakeuper() != null) {
                VoiceWakeuper.getWakeuper().destroy();
            }

            start();
            ttsLocalManager.stopSpeaking();
            ttsLocalManager.start(test, synthesizerListener);
        }
    }

    // 初始化唤醒
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

    /**
     * 开启唤醒服务
     */
    private void word() {
        if (VoiceWakeuper.getWakeuper() == null) {
            initWakeUp();
        }
        VoiceWakeuper mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw == null) {
            return;
        }
        mIvw.startListening(new WakeuperListener() {
            @Override
            public void onBeginOfSpeech() {
                LogUtil.d("onBeginOfSpeech - ");
            }

            @Override
            public void onResult(WakeuperResult wakeuperResult) {
                LogUtil.d("onResult - ");
                ttsLocalManager.stopSpeaking();
            }

            @Override
            public void onError(SpeechError speechError) {

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {
                LogUtil.d(" - " + i);
            }

            @Override
            public void onVolumeChanged(int i) {
                LogUtil.d("onVolumeChanged - " + i);
            }
        });
    }

    @Override
    public void initData() {

    }

    private void stop() {
        isRecording = false;

        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    /**
     * 开始录音
     */
    private void start() {
        stop();
        pcmFileName = getCacheDir().getAbsolutePath() + "/record.pcm";
        wavFileName = getCacheDir().getAbsolutePath() + "/record.wav";
        //audioRecord能接受的最小的buffer大小
        recordBufSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
//        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, recordBufSize);
        // 通过安卓自带的 VOICE_COMMUNICATION模式进行录音，自动消除回音
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, recordBufSize);
        buffer = new byte[recordBufSize];
        if (audioRecord.getState() != STATE_INITIALIZED) {
            ToastUtil.show("录音权限或读写存储未开启！！！");
            return;
        }
        audioRecord.startRecording();
        isRecording = true;

        new Thread(() -> {
            FileOutputStream os = null;

            try {
                if (!new File(pcmFileName).exists()) {
                    new File(pcmFileName).createNewFile();
                }
                os = new FileOutputStream(pcmFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null != os) {
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, recordBufSize);

                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            int volume = calculateVolume(buffer, 16);
                            // 有声音则停止tts
                            if (volume > 0) {
                                ttsLocalManager.stopSpeaking();
                            }
                            LogUtil.d("volume = " + volume);
                            os.write(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //================================================
}
