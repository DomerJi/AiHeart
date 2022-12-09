/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.thfw.base.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.lang.reflect.Modifier;

/**
 * Gson 转换工具
 */
public class GsonUtil {

    private static Gson gson;

    public static Gson sGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
            gson = builder.create();
        }
        return gson;
    }

    private GsonUtil() {
    }

    public static <T> T fromJson(String json, java.lang.reflect.Type type) {
        return json == null ? null : sGson().fromJson(json, type);
    }

    public static <T> T fromJson(JsonElement json, java.lang.reflect.Type type) {
        return json == null ? null : sGson().fromJson(json, type);
    }

    public static String toJson(Object obj) {
        return obj == null ? null : sGson().toJson(obj);
    }
}