package com.thfw.robotheart.activitys;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.port.SerialManager;
import com.thfw.robotheart.receiver.BootCompleteReceiver;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.Dormant;
import com.thfw.ui.base.IBaseActivity;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;

/**
 * Author:pengs
 * Date: 2021/11/15 10:20
 * Describe:Todo
 */
public abstract class RobotBaseActivity<T extends IPresenter> extends IBaseActivity<T> implements SerialManager.RobotTouchListener {

    @Override
    public void onTouch(int code, int down) {
        if (!EmptyUtil.isEmpty(RobotBaseActivity.this) && isMeResumed()) {
            DialogRobotFactory.createFullSvgaDialog(RobotBaseActivity.this,
                    AnimFileName.EMOJI_CHUMO, new DialogRobotFactory.OnSVGACallBack() {
                        @Override
                        public void callBack(SVGAImageView svgaImageView) {

                        }
                    });
            TtsHelper.getInstance().start(new TtsModel("您好"), null);
        }
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SerialManager.getInstance().addRobotTouchListener(this);
    }

    //重写字体缩放比例  api>25
    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            final Resources res = newBase.getResources();
            final Configuration config = res.getConfiguration();
            // 1 设置正常字体大小的倍数
            config.fontScale = MyApplication.getFontScale();
            final Context newContext = newBase.createConfigurationContext(config);
            super.attachBaseContext(newContext);
        } else {
            super.attachBaseContext(newBase);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // todo 关机动画
        BootCompleteReceiver.setShutDownCallback(() -> {
            if (isMeResumed()) {
                RobotUtil.shutdownByDialog(this);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SerialManager.getInstance().removeRobotTouchListener(this);
        BootCompleteReceiver.setShutDownCallback(null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Dormant.reset();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Dormant.reset();
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (RobotUtil.isInstallRobot()) {
            // 机器人顶部触摸
            if (keyCode == KeyEvent.KEYCODE_F1 && event.getRepeatCount() == 0) {
                SerialManager.getInstance().onTouch(4, 1);
            }
        }
        Dormant.reset();
        return super.onKeyDown(keyCode, event);
    }
}
