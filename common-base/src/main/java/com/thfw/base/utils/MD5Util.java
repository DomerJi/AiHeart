package com.thfw.base.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jishuaipeng on 2021-04-26.
 * Description: MD5加密
 */
public class MD5Util {
    /**
     * default
     */
    protected static char[] hexDigits = {'0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    protected static MessageDigest messagedigest = null;

    static {
        init();
    }

    private static void init() {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            System.err.println(MD5Util.class.getName()
                    + "init failed，MessageDigest unsupport MD5Util。");
            nsaex.printStackTrace();
        }
    }

    /**
     * generate md5 string
     *
     * @param s
     * @return
     */
    public static synchronized String getMD5String(String s) {
        return getMD5String(s, false);
    }

    public static synchronized String getMD5String(String s, boolean is16len) {
        try {
            return getMD5String(s.getBytes(), is16len);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized String getMD5String(byte[] bytes, boolean is16Len) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        if (messagedigest == null) {
            init();
        }
        if (messagedigest == null) {
            return "";
        }
        messagedigest.update(bytes);
        String result = bufferToHex(messagedigest.digest());
        if (is16Len) {
            return result.substring(8, 24);
        }
        return result;
    }

    /**
     * chech md5
     *
     * @param password
     * @param md5PwdStr
     * @return
     */
    public static synchronized boolean checkPassword(String password,
                                                     String md5PwdStr) {
        String s = getMD5String(password);
        return s.equals(md5PwdStr);
    }

    private static synchronized String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static synchronized String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static synchronized void appendHexPair(byte bt,
                                                   StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
