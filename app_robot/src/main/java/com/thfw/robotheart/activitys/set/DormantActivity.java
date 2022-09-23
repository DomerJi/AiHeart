package com.thfw.robotheart.activitys.set;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.port.SerialManager;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.Dormant;
import com.thfw.robotheart.util.SVGAHelper;
import com.thfw.robotheart.view.TitleBarView;
import com.thfw.ui.utils.BrightnessHelper;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.ui.voice.wakeup.WakeupHelper;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;

import static android.os.BatteryManager.BATTERY_STATUS_CHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_DISCHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_FULL;
import static android.os.BatteryManager.BATTERY_STATUS_NOT_CHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_UNKNOWN;
import static android.os.BatteryManager.EXTRA_STATUS;

public class DormantActivity extends RobotBaseActivity
        implements Dormant.MinuteChangeListener, SerialManager.ElectricityListener,
        SerialManager.RobotTouchListener {

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
    private int originCharge = -1;
    private BroadcastReceiver mBatInfoReceiver;
    private int level;
    private boolean chargeIng;
    private static DormantActivity dormantActivity;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, DormantActivity.class);
        if (context instanceof Application) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    public int getContentView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return R.layout.activity_dormant;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        dormantActivity = this;
        // 降低亮度，节约电量
        BrightnessHelper.setActivityBrightness(0.08f, this);
        mIvAnim = (SVGAImageView) findViewById(R.id.iv_anim);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mOriginalText = mTvTime.getText().toString();
        if (RobotUtil.isInstallRobot()) {
            SerialManager.getInstance().addEleListener(this);
            SerialManager.getInstance().addRobotTouchListener(this);
        } else {
            initBatReceiver();
        }

//        testEmojiLoop(mEmojiFileNames[testIndex]);

        onStartDormant();
    }

    public static void onWakeup() {
        Dormant.reset();
        if (!EmptyUtil.isEmpty(dormantActivity)) {
            dormantActivity.onWakeUp(WakeUpType.CLICK);
        }
    }

    public static boolean isWake() {
        return !EmptyUtil.isEmpty(dormantActivity);
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
            public void onWakeup(int angle, int beam) {
                if (ToastUtil.isMainThread()) {
                    onWakeUp(WakeUpType.VOICE);
                } else {
                    runOnUiThread(() -> {
                        onWakeUp(WakeUpType.VOICE);
                    });
                }
                RobotUtil.wakeup(angle, beam);
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
                TtsHelper.getInstance().start(new TtsModel("请尽快把我放到固定位置哦"), null);
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
        boolean flag = super.dispatchTouchEvent(event);
        onWakeUp(WakeUpType.CLICK);
        return flag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        onStartWakeupListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WakeupHelper.getInstance().stop();
    }

    @Override
    public void onDestroy() {
        if (mIvAnim != null) {
            mIvAnim.clear();
        }
        TimingHelper.getInstance().removeWorkArriveListener(WorkInt.SECOND3);
        Dormant.setMinuteChangeListener(null);
        SerialManager.getInstance().removeEleListener(this);
        SerialManager.getInstance().removeRobotTouchListener(this);
        if (mBatInfoReceiver != null) {
            unregisterReceiver(mBatInfoReceiver);
        }
        dormantActivity = null;
        super.onDestroy();
    }

    @Override
    public void onChange() {
        if (RobotUtil.isInstallRobot()) {
            mTvTime.setText(mOriginalText + "（已休眠" + Dormant.getSleepTime() + "分钟，" + Dormant.getShutDownDuration() + "分钟后关机）");
            if (Dormant.isCanShutdown()) {
                shutDown();
            }
        } else {
            mTvTime.setText(mOriginalText + "（已休眠" + Dormant.getSleepTime() + "分钟）");
        }
    }

    private void shutDown() {
        LogUtil.d(TAG, "shutDown -> isInstallRobot true 【关机】");
        TtsHelper.getInstance().start(new TtsModel("拜拜,下次见哦"), null);
        if (mTvTime != null) {
            mTvTime.setVisibility(View.INVISIBLE);
        }
        SVGAHelper.playSVGA(mIvAnim, SVGAHelper.SVGAModel.create(AnimFileName.EMOJI_GUANJI).setLoopCount(1), new DialogRobotFactory.SimpleSVGACallBack() {
            @Override
            public void onFinished() {
                if (!RobotUtil.shutdownShell()) {
                    onStartDormant();
                } else {
                    UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
                    LogUtil.d(TAG, "shutDown -> isInstallRobot true 【关机成功】");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        onWakeUp(WakeUpType.CLICK);
    }

    @Override
    public void onCharge(int percent, int charge) {
        if (originCharge == 1 && charge == 0) {
            onWakeUp(WakeUpType.SWIM);
        }
        originCharge = charge;
    }

    @Override
    public void onSensor(int sensor, boolean showTip) {
        if (sensor == 1 && showTip) {
            if (!Dormant.isCanShutdown()) {
                if (ToastUtil.isMainThread()) {
                    onWakeUp(WakeUpType.SWIM);
                } else {
                    HandlerUtil.getMainHandler().post(() -> {
                        onWakeUp(WakeUpType.SWIM);
                    });
                }
            }
        }
    }

    @Override
    public void onTouch(int code, int down) {
        if (down == 1) {
            onWakeUp(WakeUpType.TOUCH);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i(TAG, "keyCode = " + keyCode);

        return super.onKeyDown(keyCode, event);
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

    private void initBatReceiver() {
        createBatReceiver();
        mContext.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void createBatReceiver() {
        level = TitleBarView.getLevel();
        if (mBatInfoReceiver == null) {
            mBatInfoReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    LogUtil.d(TAG, "level = " + level);
                    int status = intent.getIntExtra(EXTRA_STATUS, BATTERY_STATUS_UNKNOWN);

                    switch (status) {
                        case BATTERY_STATUS_FULL:
                            // 充满电
                        case BATTERY_STATUS_CHARGING:
                            // 充电中
                            chargeIng = true;
                            break;
                        case BATTERY_STATUS_UNKNOWN:
                            // 未知
                        case BATTERY_STATUS_NOT_CHARGING:
                            // 未充电
                        case BATTERY_STATUS_DISCHARGING:
                            // 放电中
                            if (chargeIng) {
                                onWakeUp(WakeUpType.SWIM);
                                chargeIng = false;
                            }
                            break;
                    }
                }
            };
        }
    }
}