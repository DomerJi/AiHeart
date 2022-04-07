package com.thfw.base.utils;

import android.util.Log;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;
import com.thfw.base.ContextApp;
import com.thfw.base.face.SimpleUpgradeStateListener;

/**
 * Author:pengs
 * Date: 2021/8/30 13:36
 * Describe:Todo
 */
public class BuglyUtil {

    public static final String TAG = BuglyUtil.class.getSimpleName();
    private static SimpleUpgradeStateListener requestUpgradeStateListener;

    public static void init(String appKey) {
        Beta.autoCheckUpgrade = false;//自动检查更新开关 true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
        Beta.autoInit = true;//自动初始化开关
        Beta.initDelay = 1 * 1000;
        // 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
        Beta.upgradeCheckPeriod = 5 * 60 * 1000;
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                Log.e(TAG, "jsp UpgradeInfo == " + strategy);

                if (strategy != null) {
                    if (BuglyUtil.requestUpgradeStateListener != null) {
                        BuglyUtil.requestUpgradeStateListener.onVersion(true);
                        BuglyUtil.requestUpgradeStateListener = null;
                    }
                } else {
                    if (BuglyUtil.requestUpgradeStateListener != null) {
                        BuglyUtil.requestUpgradeStateListener.onVersion(false);
                        BuglyUtil.requestUpgradeStateListener = null;
                    }
                }
            }
        };

        /* 设置更新状态回调接口 */
        Beta.upgradeStateListener = new UpgradeStateListener() {
            @Override
            public void onUpgradeSuccess(boolean isManual) {
                Log.e(TAG, "更新成功");
                if (requestUpgradeStateListener != null) {
                    requestUpgradeStateListener.onUpgradeSuccess(isManual);
                }
            }

            @Override
            public void onUpgradeFailed(boolean isManual) {
                Log.e(TAG, "更新失败");
                if (requestUpgradeStateListener != null) {
                    requestUpgradeStateListener.onUpgradeFailed(isManual);
                }
            }

            @Override
            public void onUpgrading(boolean isManual) {
                Log.e(TAG, "检测更新中  isManual = " + isManual);
                if (requestUpgradeStateListener != null) {
                    requestUpgradeStateListener.onUpgrading(isManual);
                }
            }

            @Override
            public void onDownloadCompleted(boolean b) {
                Log.e(TAG, "下载完成 = b " + b);
                if (requestUpgradeStateListener != null) {
                    requestUpgradeStateListener.onDownloadCompleted(b);
                }
            }

            @Override
            public void onUpgradeNoVersion(boolean isManual) {
                Log.e(TAG, "没有新版本 isManual = " + isManual);
                if (requestUpgradeStateListener != null) {
                    requestUpgradeStateListener.onUpgradeNoVersion(isManual);
                }
            }
        };
        Bugly.init(ContextApp.get(), appKey, true);
    }

    public static void setUpgradeStateListener(SimpleUpgradeStateListener upgradeStateListener) {
        BuglyUtil.requestUpgradeStateListener = null;
    }

    public static void requestNewVersion(SimpleUpgradeStateListener simpleUpgradeStateListener) {
        BuglyUtil.requestUpgradeStateListener = simpleUpgradeStateListener;
        if (BuglyUtil.requestUpgradeStateListener != null) {
            if (Beta.getUpgradeInfo() != null) {
                int versionCode = Util.getAppVersionCode(ContextApp.get());
                if (Beta.getUpgradeInfo().versionCode <= versionCode) {
                    BuglyUtil.requestUpgradeStateListener.onUpgradeNoVersion(false);
                    BuglyUtil.requestUpgradeStateListener = null;
                    onBetaCheckUpgrade();
                } else {
                    BuglyUtil.requestUpgradeStateListener.onVersion(true);
                    BuglyUtil.requestUpgradeStateListener = null;
                }
            } else {
                onBetaCheckUpgrade();
            }
        }
    }

    private static void onBetaCheckUpgrade() {
        try {
            LogUtil.d(TAG, "onBetaCheckUpgrade ++++++++++++++++++++");
            Beta.checkUpgrade(true, true);
        } catch (Exception e) {
            LogUtil.d(TAG, "checkUpgrade e = " + e.getMessage());
            if (BuglyUtil.requestUpgradeStateListener != null) {
                BuglyUtil.requestUpgradeStateListener.onVersion(false);
                BuglyUtil.requestUpgradeStateListener = null;
            }
        }
    }
}
