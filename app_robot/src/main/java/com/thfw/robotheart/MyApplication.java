package com.thfw.robotheart;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.iflytek.cloud.Setting;
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
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.push.MyPreferences;
import com.thfw.robotheart.push.helper.PushHelper;
import com.thfw.ui.dialog.TDialog;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 入口
 * Created By jishuaipeng on 2021/4/27
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication app;

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

    private static float lv;

    public static MyApplication getApp() {
        return app;
    }

    public static AppDatabase getDatabase() {
        return Room.databaseBuilder(app, AppDatabase.class, "database-name").build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        ContextApp.init(app);
        SharePreferenceUtil.init(this);
        BuglyUtil.init("382fc62522");
        ContextApp.setDeviceType(ContextApp.DeviceType.ROBOT);
        ToastUtil.init(this);
        TDialog.init(this);
        initSpeech();
        initUmengSDK();
    }

    private void initSpeech() {
        /*
         * 科大讯飞
         */
        StringBuffer param = new StringBuffer();
        //5f3de60d.jet
        param.append(SpeechConstant.APPID + "=5f3de60d");
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(app, param.toString());
        Setting.setShowLog(false);
    }


    /**
     * 初始化友盟SDK
     */
    private void initUmengSDK() {
        //日志开关
        UMConfigure.setLogEnabled(true);
        //预初始化
        PushHelper.preInit(this);
        //是否同意隐私政策
        boolean agreed = MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
        if (!agreed) {
            return;
        }
        boolean isMainProcess = UMUtils.isMainProgress(this);
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushHelper.init(getApplicationContext());
                }
            }).start();
        } else {
            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
            PushHelper.init(getApplicationContext());
        }
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
        if (lv > 1.15f) {
            lv = lv * 0.87f;
        } else if (lv < 0.87f) {
            lv = lv * 1.15f;
        }

        LogUtil.d("getFontScale", "lv = " + lv);
        return lv;
    }

}
