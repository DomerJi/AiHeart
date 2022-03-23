package com.thfw.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.thfw.util.ContextApp;
import com.thfw.util.DeviceIdUtil;
import com.thfw.util.SharePreferenceUtil;

/**
 * Author:pengs
 * Date: 2021/12/4 13:56
 * Describe:网络请求：公共参数
 */
public class CommonParameter {

    private static final String KEY_ORGANIZATION_ID = "key_organization_id";
    private static final String KEY_ORGANIZATION_SELECT = "key_organization_select";
    private static final String TEST_ID = "1";

    public interface DeviceType {
        String PAD = "pad";
        String MOBILE = "phone";
        String ROBOT = "robot";
    }


    public static String getDeviceType() {
        switch (ContextApp.getDeviceType()) {
            case ContextApp.DeviceType.PAD:
                return DeviceType.PAD;
            case ContextApp.DeviceType.MOBILE:
                return DeviceType.MOBILE;
            case ContextApp.DeviceType.ROBOT:
                return DeviceType.ROBOT;
            default:
                return DeviceType.MOBILE;
        }
    }

    public static String getDeviceId() {
        return DeviceIdUtil.getDeviceId(ContextApp.get());
    }

    public static String getAppVersion() {
        return getAppVersion(ContextApp.get());
    }

    public static String getAppVersion(Context context) {
        String versionName = "";

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo p1 = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = p1.versionName;
            if (TextUtils.isEmpty(versionName) || versionName.length() <= 0) {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return versionName;


    }

    public static String getOrganizationId() {
        String id = SharePreferenceUtil.getString(KEY_ORGANIZATION_ID, null);
        return TextUtils.isEmpty(id) ? TEST_ID : id;
    }

    public static void setOrganizationId(String organizationId) {
        SharePreferenceUtil.setString(KEY_ORGANIZATION_ID, organizationId);
    }

    public static boolean isValid() {
        return true;
        // todo 真实环境必须校验
//        return !TextUtils.isEmpty(getOrganizationId()) && !TEST_ID.equals(getOrganizationId());
    }

}
