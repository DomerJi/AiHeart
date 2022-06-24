package com.thfw.robotheart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.Dormant;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;

import java.lang.ref.WeakReference;

import static com.thfw.ui.voice.tts.TtsHelper.WELCOME_TTS;

/**
 * Author:pengs
 * Date: 2022/5/21 10:01
 * Describe:开机广播
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompleteReceiver.class.getSimpleName();

    private static long bootCompleteTime;
    /**
     * 1秒=1000毫秒
     * 1毫秒=1000微秒
     * 1微秒=1000纳秒
     */
    private static final long ONE_NS_TIME = 1000 * 1000 * 1000;
    private static final long JVM_MIN_BOOTP_TIME = 20 * ONE_NS_TIME;
    private static ShutDownCallback shutDownCallback;
    private static WeakReference<FragmentActivity> mFragmentActivity;

    public static void setShutDownCallback(ShutDownCallback shutDownCallback) {
        BootCompleteReceiver.shutDownCallback = shutDownCallback;
    }

    public interface ShutDownCallback {
        void shutDown();
    }

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
        mFragmentActivity = new WeakReference<>(fragmentActivity);
        if (bootCompleteTime == -1) {
            return;
        }
        bootCompleteTime = -1;
        if (EmptyUtil.isEmpty(fragmentActivity)) {
            return;
        }
        LogUtil.i(TAG, "checkBootCompleteAnim S -> " + (System.nanoTime() / ONE_NS_TIME));
        if (System.nanoTime() < JVM_MIN_BOOTP_TIME) {
            DialogRobotFactory.createFullSvgaDialog(fragmentActivity, AnimFileName.EMOJI_KAIJI, new DialogRobotFactory.OnSVGACallBack() {
                @Override
                public void callBack(SVGAImageView svgaImageView) {
                    LogUtil.i(TAG, "checkBootCompleteAnim svga complete");
                }
            });
            TtsHelper.getInstance().start(new TtsModel(WELCOME_TTS), null);
        } else if (isBootComplete()) {
            DialogRobotFactory.createFullSvgaDialog(fragmentActivity, AnimFileName.EMOJI_KAIJI, new DialogRobotFactory.OnSVGACallBack() {
                @Override
                public void callBack(SVGAImageView svgaImageView) {
                    LogUtil.i(TAG, "checkBootCompleteAnim svga complete");
                }
            });
            TtsHelper.getInstance().start(new TtsModel(WELCOME_TTS), null);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            LogUtil.e(TAG, "开机广播 ++++++++++++++++++++++++++++++++++++++++++++");
            if (bootCompleteTime == 0) {
                bootCompleteTime = System.currentTimeMillis();
                if (mFragmentActivity != null && mFragmentActivity.get() != null) {
                    checkBootCompleteAnim(mFragmentActivity.get());
                }
            }

        } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
            LogUtil.e(TAG, "关机广播 ++++++++++++++++++++++++++++++++++++++++++++");
            if (!Dormant.isCanShutdown()) {
                if (shutDownCallback != null) {
                    shutDownCallback.shutDown();
                }
            }
        }

    }
}
