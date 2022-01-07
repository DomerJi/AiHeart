package com.thfw.base.utils;

import android.text.TextUtils;
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
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                Log.e("upgradeListener", "jsp UpgradeInfo == " + strategy);

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
        if (BuglyUtil.requestUpgradeStateListener == null) {
            return;
        }

        if (Beta.getUpgradeInfo() != null) {
            String version = Util.getAppVersion(ContextApp.get());
            LogUtil.d("requestNewVersion = ");
            if (!TextUtils.isEmpty(version) && version.equals(Beta.getUpgradeInfo().versionName)) {
                BuglyUtil.requestUpgradeStateListener.onUpgradeNoVersion(false);
                BuglyUtil.requestUpgradeStateListener = null;
            } else {
                BuglyUtil.requestUpgradeStateListener.onVersion(true);
                BuglyUtil.requestUpgradeStateListener = null;
            }
            return;
        }

        if (BuglyUtil.requestUpgradeStateListener != null) {
            try {
                Beta.checkUpgrade(true, true);
            } catch (Exception e) {
                LogUtil.d("checkUpgrade", e.getMessage());
            }
        }
    }
}
