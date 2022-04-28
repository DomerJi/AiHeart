package com.thfw.base.utils;

/**
 * Author:pengs
 * Date: 2022/4/28 10:44
 * Describe:Todo
 */
public class FaceSetUtil {

    // 录入人脸使用大小
    public static final float INPUT_MAX = 350f;

    // 刷脸登录使用大小
    public static final float LOGIN_MAX = 200f;

    // 训练数据人脸外有一部分背景图，根据算法要求，可以设置偏移量
    // 偏移量越大人脸在图像中的比例越小，目前低于50成功率不高，需高于60
    public static final int OFFSET = 70;
}
