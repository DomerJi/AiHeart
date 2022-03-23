/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.thfw.net;


import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanglong02 on 17/3/24.
 */
public final class RequestParamSigner {

    // 字符数组，用来存放十六进制字符
    private static final char[] HEX_REFER_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F'};

    public static String sign(Map<String, String> params, String signPrefix, String signPostfix) {
        // 构造请求参数键值对
        List<KeyValuePair> keyValuePairList = buildKeyValuePairList(params);
        // 键值对排序
        sort(keyValuePairList);
        // 构造待signed串
        StringBuffer stringBuffer = new StringBuffer();
        for (KeyValuePair keyValuePair : keyValuePairList) {
            if ("sign".equals(keyValuePair.toString())) {
                continue;
            }
            stringBuffer.append(keyValuePair.toString());
        }
//        signPrefix = "123456";
//        signPostfix = "123456";
        return toMd5(signPrefix + stringBuffer.toString() + signPostfix);
    }

    /**
     * 构造请求参数键值对
     *
     * @param params
     * @return
     */
    private static List<KeyValuePair> buildKeyValuePairList(Map<String, String> params) {
        List<KeyValuePair> keyValuePairList = new ArrayList<KeyValuePair>();
        try {
            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = URLEncoder.encode(params.get(key), "UTF-8");
                keyValuePairList.add(new KeyValuePair(key, value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyValuePairList;
    }

    /**
     * 按字母顺序排列
     *
     * @param keyValuePairList
     */
    private static void sort(List<KeyValuePair> keyValuePairList) {
        Collections.sort(keyValuePairList, new Comparator<KeyValuePair>() {
            @Override
            public int compare(KeyValuePair lhs, KeyValuePair rhs) {
                return lhs.key.compareTo(rhs.key);
            }
        });
    }

    public static String toMd5(String str) {
        String md5Str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes("UTF-8"));
            // 完成计算，返回结果数组
            byte[] b = md.digest();
            md5Str = byteArrayToHex(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5Str.toLowerCase();
    }

    public static String byteArrayToHex(byte[] bytes) {
        // 一个字节占8位，一个十六进制字符占4位；十六进制字符数组的长度为字节数组长度的两倍
        char[] hexChars = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            // 取字节的高4位
            hexChars[index++] = HEX_REFER_CHARS[b >>> 4 & 0xf];
            // 取字节的低4位
            hexChars[index++] = HEX_REFER_CHARS[b & 0xf];
        }
        return new String(hexChars);
    }

    private static class KeyValuePair {

        String key;
        String value;

        KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

}
