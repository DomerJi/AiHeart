package com.thfw.base.net;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.thfw.base.ContextApp;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.utils.DeviceIdUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/4 13:56
 * Describe:网络请求：公共参数
 */
public class CommonParameter {

    private static final String KEY_ORGANIZATION_ID = "key_organization_id";
    private static final String KEY_ORGANIZATION_SELECT = "key_organization_select";

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
        return Util.getAppVersion(ContextApp.get());
    }

    public static String getOrganizationId() {
        return SharePreferenceUtil.getString(KEY_ORGANIZATION_ID, null);
    }

    public static void setOrganizationId(String organizationId) {
        SharePreferenceUtil.setString(KEY_ORGANIZATION_ID, organizationId);
    }

    public static void setOrganizationSelected(ArrayList<OrganizationModel.OrganizationBean> organizationBeans) {
        SharePreferenceUtil.setString(KEY_ORGANIZATION_SELECT + CommonParameter.getOrganizationId(), GsonUtil.toJson(organizationBeans));
    }

    public static List<OrganizationModel.OrganizationBean> getOrganizationSelected() {
        String json = SharePreferenceUtil.getString(KEY_ORGANIZATION_SELECT + CommonParameter.getOrganizationId(), null);
        if (!TextUtils.isEmpty(json)) {

            Type type = new TypeToken<List<OrganizationModel.OrganizationBean>>() {
            }.getType();

            List<OrganizationModel.OrganizationBean> list = GsonUtil.fromJson(json, type);
            return list;
        }
        return null;
    }

}
