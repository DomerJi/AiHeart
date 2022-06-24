package com.thfw.robotheart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.pl.sphelper.SPHelper;
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
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.activitys.MainActivity;
import com.thfw.robotheart.push.MyPreferences;
import com.thfw.robotheart.push.helper.PushHelper;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.ui.dialog.TDialog;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

/**
 * 入口
 * Created By jishuaipeng on 2021/4/27
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication app;
    private static float lv;

    // static 代码段可以防止内存泄露
    static {
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
    }

    public static MyApplication getApp() {
        return app;
    }

    public static AppDatabase getDatabase() {
        return Room.databaseBuilder(app, AppDatabase.class, "database-name").build();
    }

    public static float getFontScale() {
        if (lv > 0) {
            return lv;
        }

        DisplayMetrics dm = app.getResources().getDisplayMetrics();
        int screenDensity = dm.densityDpi;
        lv = 1.0f * DisplayMetrics.DENSITY_XHIGH / screenDensity;
        if (DisplayMetrics.DENSITY_XHIGH >= screenDensity) {
            LogUtil.d("getFontScale01 lv = " + lv);
            // 华为平板 6.25 = 2000/320
            lv = lv * Math.max(dm.widthPixels, dm.heightPixels) * 1.0f / screenDensity / 6.25f;
        }
        LogUtil.d("getFontScale02 lv = " + lv);
        return lv;
    }

    public static void goAppHome(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        ContextApp.init(this);
        SharePreferenceUtil.init(this);
        ContextApp.setDeviceType(ContextApp.DeviceType.ROBOT);
        ToastUtil.init(this);
        TDialog.init(this);
        SPHelper.init(app);
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
        Setting.setShowLog(true);
    }

    private void initAtThread() {
        //日志开关
        UMConfigure.setLogEnabled(true);
        //预初始化
        PushHelper.preInit(this);
        boolean isMainProcess = UMUtils.isMainProgress(this);
        if (isMainProcess) {
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
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        RobotUtil.longPressOffBtn();
        if (RobotUtil.isInstallRobot()) {
            BuglyUtil.init("382fc62522");
        } else {
            BuglyUtil.init("d24e28638f");
        }
        initSpeech();
        //是否同意隐私政策
        boolean agreed = MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
        if (agreed) {
            PushHelper.init(getApplicationContext());
        }
    }

    private static int startedActivityCount;
    private static ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            startedActivityCount++;
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            startedActivityCount--;
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    };

    public static boolean ifForeground() {
        return startedActivityCount > 0;
    }

}
