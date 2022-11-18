package com.thfw.base.utils;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.thfw.base.ContextApp;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.models.VersionModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * Author:pengs
 * Date: 2021/8/30 13:36
 * Describe:Todo
 */
public class BuglyUtil {

    public static final String TAG = BuglyUtil.class.getSimpleName();
    private static SimpleUpgradeStateListener requestUpgradeStateListener;
    private static VersionModel versionModel;
    private static long checkVersionTime;
    private static boolean requestIng = false;

    public static void init(String appKey) {
        Beta.autoCheckUpgrade = false;//自动检查更新开关 true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
        Beta.autoInit = true;//自动初始化开关
        Beta.initDelay = 1 * 1000;
        Beta.enableHotfix = false;// 升级SDK默认是开启热更新能力的，如果你不需要使用热更新，可以将这个接口设置为false。
        Bugly.init(ContextApp.get(), appKey, true);
    }

    public static void setUpgradeStateListener(SimpleUpgradeStateListener upgradeStateListener) {
        BuglyUtil.requestUpgradeStateListener = null;
    }

    public static void requestNewVersion(SimpleUpgradeStateListener simpleUpgradeStateListener) {
        BuglyUtil.requestUpgradeStateListener = simpleUpgradeStateListener;
        if (BuglyUtil.requestUpgradeStateListener != null) {
            onBetaCheckUpgrade();
        }
    }

    public static VersionModel getVersionModel() {
        return versionModel;
    }

    public static boolean isNewVersion() {
        if (versionModel != null) {
            return !Util.getAppVersion(ContextApp.get()).equals(versionModel.getVersion());

        }
        return false;
    }

    private static void onBetaCheckUpgrade() {
        if (requestIng) {
            return;
        }
        requestIng = true;
        if (versionModel != null && System.currentTimeMillis() - checkVersionTime < HourUtil.LEN_MINUTE) {
            requestIng = false;
            if (BuglyUtil.requestUpgradeStateListener != null) {
                BuglyUtil.requestUpgradeStateListener.onVersion(isNewVersion());
            }
            return;
        }
        new OtherPresenter(new OtherPresenter.OtherUi<VersionModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return null;
            }

            @Override
            public void onSuccess(VersionModel data) {
                versionModel = data;
                checkVersionTime = System.currentTimeMillis();
                requestIng = false;
                if (BuglyUtil.requestUpgradeStateListener != null) {
                    BuglyUtil.requestUpgradeStateListener.onVersion(isNewVersion());
                }
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                boolean debug = false;
//                boolean debug = true;
                if (debug) {
                    requestIng = false;
                    versionModel = new VersionModel();
                    versionModel.setVersion("2.0.0999");
                    versionModel.setSize("99889789");
                    versionModel.setDes("版本升级说明");
                    versionModel.setDownloadUrl("https://cos.pgyer.com/5e94311237e44983ac5322095519c80f.apk?sign=aa9dca732c8206d3edee23a8059861dc&t=1668767894&response-content-disposition=attachment%3Bfilename%3DAI%E5%92%A8%E8%AF%A2%E5%B8%88_2.0.0.apk");
                    if (BuglyUtil.requestUpgradeStateListener != null) {
                        BuglyUtil.requestUpgradeStateListener.onVersion(true);
                    }
                } else {
                    TimingHelper.getInstance().addWorkArriveListener(new TimingHelper.WorkListener() {
                        @Override
                        public void onArrive() {
                            onBetaCheckUpgrade();
                        }

                        @Override
                        public WorkInt workInt() {
                            return WorkInt.CHECK_VERSION;
                        }
                    });
                }
            }
        }).onCheckVersion();

    }
}
