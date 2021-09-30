/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.thfw.base.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Gson 转换工具
 */
public class GsonUtil {
    private static Gson sGson = new Gson();

    private GsonUtil() {
    }

    public static <T> T fromJson(String json, java.lang.reflect.Type type) throws Exception {
        return json == null ? null : sGson.fromJson(json, type);
    }

    public static <T> T fromJson(JsonElement json, java.lang.reflect.Type type) throws Exception {
        return json == null ? null : sGson.fromJson(json, type);
    }

    public static String toJson(Object obj) {
        return obj == null ? null : sGson.toJson(obj);
    }
}