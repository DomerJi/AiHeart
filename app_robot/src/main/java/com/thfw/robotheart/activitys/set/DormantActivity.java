package com.thfw.robotheart.activitys.set;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.widget.TextView;

import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.util.Dormant;
import com.thfw.robotheart.view.DialogRobotFactory;
import com.thfw.robotheart.view.SVGAHelper;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.ui.voice.wakeup.WakeupHelper;

public class DormantActivity extends RobotBaseActivity implements Dormant.MinuteChangeListener {

    private SVGAImageView mIvAnim;
    private android.widget.TextView mTvTime;
    private String mOriginalText;
    // 唤醒过程中不允许，多次唤醒
    private boolean wakeupIng = false;

    private String[] mEmojiFileNames = new String[]{
            AnimFileName.EMOJI_CHUMO,
            AnimFileName.EMOJI_HUANXING,
            AnimFileName.EMOJI_KAIJI,
            AnimFileName.EMOJI_SHIWANG,
            AnimFileName.EMOJI_SPEECH,
            AnimFileName.EMOJI_WELCOM,
            AnimFileName.EMOJI_XIUMIAN,
            AnimFileName.EMOJI_XUANYUN,
            AnimFileName.EMOJI_QINGTING,
    };

    private int testIndex = 0;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, DormantActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_dormant;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mIvAnim = (SVGAImageView) findViewById(R.id.iv_anim);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mOriginalText = mTvTime.getText().toString();

//        testEmojiLoop(mEmojiFileNames[testIndex]);

        onStartDormant();
        onStartWakeupListener();
    }


    private void onStartDormant() {
        SVGAHelper.playSVGA(mIvAnim, SVGAHelper.SVGAModel.create(AnimFileName.EMOJI_XIUMIAN).setLoopCount(1), new DialogRobotFactory.SimpleSVGACallBack() {
            @Override
            public void onFinished() {
                onJoinDormant();
            }
        });
    }

    private void onJoinDormant() {
        SVGAHelper.playSVGA(mIvAnim, SVGAHelper.SVGAModel.create(AnimFileName.EMOJI_XIUMIAN).setLocation(30, 0), new DialogRobotFactory.SimpleSVGACallBack() {
            @Override
            public void onFinished() {
            }
        });
    }

    private void onStartWakeupListener() {
        WakeupHelper.getInstance().setWakeUpListener(new WakeupHelper.OnWakeUpListener() {
            @Override
            public void onWakeup() {
                onWakeUp(WakeUpType.VOICE);
            }

            @Override
            public void onError() {
                onStartWakeupListener();
            }
        });
        WakeupHelper.getInstance().start();
    }

    /**
     * 测试所有emoji动画
     *
     * @param filename
     */
    private void testEmojiLoop(String filename) {
        SVGAHelper.playSVGA(mIvAnim, SVGAHelper.SVGAModel.create(filename).setLoopCount(1), new DialogRobotFactory.SimpleSVGACallBack() {
            @Override
            public void onFinished() {
                testEmojiLoop(mEmojiFileNames[++testIndex % mEmojiFileNames.length]);
            }
        });
    }

    private void onWakeUp(int type) {
        if (wakeupIng) {
            LogUtil.d(TAG, "wakeupIng 正在执行唤醒操作 type = " + type);
            return;
        }
        wakeupIng = true;
        SVGAHelper.SVGAModel svgaModel = null;
        switch (type) {
            case WakeUpType.CLICK:
            case WakeUpType.VOICE:
                svgaModel = SVGAHelper.SVGAModel.create(AnimFileName.EMOJI_HUANXING).setLoopCount(1);
                break;
            case WakeUpType.TOUCH:
                svgaModel = SVGAHelper.SVGAModel.create(AnimFileName.EMOJI_CHUMO).setLoopCount(1);
                break;
            case WakeUpType.SWIM:
                SVGAHelper.playSVGA(mIvAnim, SVGAHelper.SVGAModel.create(AnimFileName.EMOJI_XUANYUN).setLoopCount(1), new DialogRobotFactory.SimpleSVGACallBack() {
                    @Override
                    public void onFinished() {
                        onStartDormant();
                        wakeupIng = false;
                    }
                });
                return;
        }
        if (svgaModel == null) {
            LogUtil.d(TAG, "onWakeUp -> svgaModel is null");
            return;
        }
        wakeAnswer();
        SVGAHelper.playSVGA(mIvAnim, svgaModel, new DialogRobotFactory.SimpleSVGACallBack() {
            @Override
            public void onFinished() {
                finish();
                wakeupIng = false;
            }
        });
    }

    /**
     * 唤醒后应答
     */
    private void wakeAnswer() {
        TtsHelper.getInstance().start(new TtsModel("在呢"), null);
    }

    @Override
    public void initData() {
        Dormant.setMinuteChangeListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        onWakeUp(WakeUpType.CLICK);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onDestroy() {
        if (mIvAnim != null) {
            mIvAnim.clear();
        }
        TimingHelper.getInstance().removeWorkArriveListener(WorkInt.SECOND3);
        Dormant.setMinuteChangeListener(null);
        super.onDestroy();
    }

    @Override
    public void onChange() {
        mTvTime.setText(mOriginalText + "（已休眠" + Dormant.getSleepTime() + "分钟）");
    }

    public static class WakeUpType {
        // 点击屏幕 【唤醒】
        public static final int CLICK = 0;
        // 触摸机器人 【唤醒】
        public static final int TOUCH = 1;
        // 语音唤醒 【唤醒】
        public static final int VOICE = 2;
        // 离开充电座 眩晕后继续休眠 【不唤醒】
        public static final int SWIM = 3;
    }

    @Override
    public void onBackPressed() {
        onWakeUp(WakeUpType.CLICK);
    }
}