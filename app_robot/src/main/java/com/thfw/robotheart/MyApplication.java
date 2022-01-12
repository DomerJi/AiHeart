package com.thfw.robotheart;

import android.content.Context;

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
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.ui.dialog.TDialog;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 入口
 * Created By jishuaipeng on 2021/4/27
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication app;

    public static MyApplication getApp() {
        return app;
    }

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

    public static AppDatabase getDatabase() {
        return Room.databaseBuilder(app, AppDatabase.class, "database-name").build();
    }

}
