package com.thfw.base.utils;

import android.app.Application;

import com.liulishuo.filedownloader.DownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.thfw.base.ContextApp;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.models.VersionModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.net.download.TestDownLoad;
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
    private static DownloadTask downloadTask;

    public static void init(String appKey) {
        Beta.autoCheckUpgrade = false;//自动检查更新开关 true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
        Beta.autoInit = true;//自动初始化开关
        Beta.initDelay = 1 * 1000;
        Beta.enableHotfix = false;// 升级SDK默认是开启热更新能力的，如果你不需要使用热更新，可以将这个接口设置为false。
        Bugly.init(ContextApp.get(), appKey, true);
        FileDownloader.setupOnApplicationOnCreate((Application) ContextApp.get());
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
                if (versionModel != null && data != null) {
                    if (StringUtil.contentEquals(versionModel.getVersion(), data.getVersion())
                            && StringUtil.contentEquals(versionModel.getDownloadUrl(), data.getDownloadUrl())) {

                    } else {
                        downloadTask = null;
                    }

                }
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
                    versionModel.setDownloadUrl("https://oss.ucdl.pp.uc.cn/fs01/union_pack/Wandoujia_3963569_web_seo_baidu_binded.apk?x-oss-process=udf%2Fpp-udf%2CJjc3LiMnJ3J%2BcXZycA%3D%3D");
                    if (BuglyUtil.requestUpgradeStateListener != null) {
                        BuglyUtil.requestUpgradeStateListener.onVersion(true);
                    }
                } else {
                    if (BuglyUtil.requestUpgradeStateListener != null) {
                        BuglyUtil.requestUpgradeStateListener.onVersion(isNewVersion());
                    }
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

    public static synchronized DownloadTask getApkDownLoad() {
        if (downloadTask == null) {
            synchronized (BuglyUtil.class) {
                if (downloadTask == null) {
                    downloadTask = (DownloadTask) FileDownloader.getImpl().create(BuglyUtil.getVersionModel().getDownloadUrl())
                            .setPath(TestDownLoad.getApkPath(BuglyUtil.getVersionModel().getDownloadUrl()));
                }
            }
        }
        return downloadTask;
    }
}
