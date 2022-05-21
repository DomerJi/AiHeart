package com.thfw.robotheart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;

/**
 * Author:pengs
 * Date: 2022/5/21 10:01
 * Describe:开机广播
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompleteReceiver.class.getSimpleName();
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    private static long bootCompleteTime;

    public static boolean isBootComplete() {
        if (bootCompleteTime == 0) {
            return false;
        }
        return System.currentTimeMillis() - bootCompleteTime < 5000;
    }

    /**
     * todo 应该在 MainActivity 已登录下 和 LoginActivity 未登录下 分别调用
     *
     * @param fragmentActivity
     */
    public static void checkBootCompleteAnim(FragmentActivity fragmentActivity) {
        if (isBootComplete() && RobotUtil.isInstallRobot()) {
            bootCompleteTime = 0;
            DialogRobotFactory.createSvgaDialog(fragmentActivity, AnimFileName.TRANSITION_WELCOM, new DialogRobotFactory.OnSVGACallBack() {
                @Override
                public void callBack(SVGAImageView svgaImageView) {
                    LogUtil.i(TAG, "checkBootCompleteAnim svga complete");
                }
            });
            TtsHelper.getInstance().start(new TtsModel("你好，我是小密，给你最贴心的心理服务"), null);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT.equals(intent.getAction())) {
            LogUtil.e(TAG, "开机广播 ++++++++++++++++++++++++++++++++++++++++++++");
            bootCompleteTime = System.currentTimeMillis();
        }

    }
}
