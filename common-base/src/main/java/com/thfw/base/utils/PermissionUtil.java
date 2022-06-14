package com.thfw.base.utils;

import android.Manifest;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Author:pengs
 * Date: 2022/4/25 9:20
 * Describe:Todo
 */
public class PermissionUtil {

    public static String getInfo(String... permission) {

        HashMap<String, String> permissionInfo = new HashMap<>();
        permissionInfo.put(Manifest.permission.CAMERA, "【相机】 - 方便您使用刷脸登录，扫码功能");
        permissionInfo.put(Manifest.permission.RECORD_AUDIO, "【麦克风】 - 语音转文字，倾听您的指令");
        permissionInfo.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "【存储】 - 访问相册完善您的个人信息");
        permissionInfo.put(Manifest.permission.BLUETOOTH, "【蓝牙】 - 连接蓝牙耳机");

        String location = "【位置信息】 - 为您展示wifi打开情况";
        permissionInfo.put(Manifest.permission.ACCESS_COARSE_LOCATION, location);
        permissionInfo.put(Manifest.permission.ACCESS_FINE_LOCATION, location);
        permissionInfo.put(Manifest.permission.CHANGE_WIFI_STATE, location);
        HashSet<String> infoList = new HashSet<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < permission.length; i++) {
            if (infoList.add(permissionInfo.get(permission[i]))) {
                if (i != 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(permissionInfo.get(permission[i]));

            }
        }
        return stringBuilder.toString();
    }

}
