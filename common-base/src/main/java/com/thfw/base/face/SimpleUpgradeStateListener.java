package com.thfw.base.face;

import android.util.Log;

import com.tencent.bugly.beta.upgrade.UpgradeStateListener;

public abstract class SimpleUpgradeStateListener implements UpgradeStateListener {

    @Override
    public void onUpgradeFailed(boolean b) {
        onVersion(false);
    }

    @Override
    public void onUpgradeSuccess(boolean b) {
        onVersion(false);
    }

    @Override
    public void onUpgradeNoVersion(boolean b) {
        onVersion(false);
    }

    @Override
    public void onUpgrading(boolean b) {
        onVersion(false);
    }

    @Override
    public void onDownloadCompleted(boolean b) {
        onVersion(false);
    }

    public void onVersion(boolean hasNewVersion) {
        Log.d("SimpleUpgradeState", "hasNewVersion = " + hasNewVersion);
    }
}