package com.thfw.mobileheart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.thfw.base.ContextApp;
import com.thfw.base.room.AppDatabase;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.activity.MainActivity;
import com.thfw.mobileheart.push.MyPreferences;
import com.thfw.mobileheart.push.helper.PushHelper;
import com.thfw.mobileheart.util.ActivityLifeCycle;
import com.thfw.mobileheart.util.AppLifeHelper;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.widget.DeviceUtil;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

/**
 * 入口
 * Created By jishuaipeng on 2021/4/27
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication app;
    private static float lv;
    private static ActivityLifeCycle activityLifeCycle;
    private static BroadcastReceiver broadcastReceiver;
    private static List<OnMinuteListener> onMinuteListeners;

    // static 代码段可以防止内存泄露
    static {
        onMinuteListeners = new ArrayList<>();
        activityLifeCycle = new ActivityLifeCycle();
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context)
                        .setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
            }
        });
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                // 指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });

        // 每分钟时间改变监听
        broadcastReceiver = null;
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!EmptyUtil.isEmpty(onMinuteListeners)) {
                    if (intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                        if (activityLifeCycle.isForegroundNow()) {
                            activityLifeCycle.saveAppUseTime();
                        }
                    }
                }
                getApp().onMinuteChange();
            }
        };
    }

    public static MyApplication getApp() {
        return app;
    }

    public static float getFontScale() {
        if (lv > 0) {
            return lv;
        }
        int screenDensity = app.getResources().getDisplayMetrics().densityDpi;
        if (DisplayMetrics.DENSITY_XHIGH == screenDensity) {
            lv = 1f;
            LogUtil.d("getFontScale", "lv = " + lv);
            return lv;
        }
        lv = 1.0f * DisplayMetrics.DENSITY_XHIGH / screenDensity;
        LogUtil.d("getFontScale", "lv = " + lv);
        return lv;
    }

    public static void goAppHome(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static AppDatabase getDatabase() {
        return Room.databaseBuilder(app, AppDatabase.class, "database-name").build();
    }

    public static void kill() {
        MainActivity.finishMain();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 复制到剪贴板
     *
     * @param copyText
     */
    public static void copy(String copyText) {
        // 获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", copyText);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtil.show("已复制到剪贴板");
    }

    /**
     * 实现粘贴功能
     */
    public static String paste() {
        ClipboardManager cm = (ClipboardManager) getApp().getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        ClipData.Item item = data.getItemAt(0);
        String content = item.getText().toString().trim();
        return content;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        ContextApp.init(this);
        SharePreferenceUtil.init(this);
        ContextApp.setDeviceType(Util.isPad(this) ? ContextApp.DeviceType.PAD
                : ContextApp.DeviceType.MOBILE);
        ToastUtil.init(this);
        TDialog.init(this);
        LogUtil.setLogEnabled(LogUtil.isLogSpEnable());
        initAtThread();

    }

    private void initSpeech() {
        /*
         * 科大讯飞
         */
        StringBuffer param = new StringBuffer();
        //5f3de60d.jet
//        param.append(SpeechConstant.APPID + "=5f3de60d");
        param.append(SpeechConstant.APPID + "=ec8b0856");
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(app, param.toString());
    }

    private void initAtThread() {
        //日志开关
        UMConfigure.setLogEnabled(LogUtil.isLogEnabled());
        //预初始化
        PushHelper.preInit(this);
        boolean isMainProcess = UMUtils.isMainProgress(this);
        if (isMainProcess) {
            if (DeviceUtil.isLhXk_CM_GB03D()) {
                AppLifeHelper.initActivityLifecycle(this);
            }
            //启动优化：建议在子线程中执行初始化
            new Thread(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            }).start();
        }
    }

    private void init() {
        registerActivityLifecycleCallbacks(activityLifeCycle);
        initTimeReceiver();
        BuglyUtil.init("36df997c6c");
        initSpeech();
        //是否同意隐私政策
        boolean agreed = MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
        if (agreed) {
            PushHelper.init(getApplicationContext());
        }
    }

    private void initTimeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        getApp().registerReceiver(broadcastReceiver, filter);
        //广播的注册，其中Intent.ACTION_TIME_CHANGED代表时间设置变化的时候会发出该广播
    }

    public void addOnMinuteListener(OnMinuteListener onMinuteListener) {
        if (!onMinuteListeners.contains(onMinuteListener)) {
            onMinuteListeners.add(onMinuteListener);
        }
    }

    public void onRemoveOnMinuteListener(OnMinuteListener onMinuteListener) {
        if (onMinuteListeners.contains(onMinuteListener)) {
            onMinuteListeners.remove(onMinuteListener);
        }
    }

    private void onMinuteChange() {
        if (!EmptyUtil.isEmpty(onMinuteListeners)) {
            for (OnMinuteListener listener : onMinuteListeners) {
                if (listener != null) {
                    listener.onChanged();
                }
            }
        }
    }

    public interface OnMinuteListener {
        void onChanged();
    }

}
