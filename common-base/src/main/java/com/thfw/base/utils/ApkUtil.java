/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.thfw.base.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Created by jishuaipeng on 2021-04-26.
 * App信息工具类
 * <p>
 * Created by lishicong on 2017/6/7.
 */
public class ApkUtil {

    /**
     * 获取App包名
     */
    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取app名称
     */
    public static String getAppName(Context context) {

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 获取App版本号
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(getAppPackageName(context), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e("ApkUtil getAppVersionCode error");
        }
        return versionCode;
    }

    /**
     * 获取App版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            versionName = context.getPackageManager().getPackageInfo(getAppPackageName(context), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e("ApkUtil getAppVersionName error");
        }
        return versionName;
    }

    /**
     * 获取App渠道名
     */
    public static String getAppChannel(Context context) {
        return getAppMetaData(context, "channel");
    }

    /**
     * 获取meta信息
     */
    public static String getAppMetaData(Context context, String metaName) {
        ApplicationInfo ai;

        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(metaName);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(e);
        }
        return null;

    }

}
