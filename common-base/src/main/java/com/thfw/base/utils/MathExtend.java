package com.thfw.base.utils;

import java.math.BigDecimal;

/**
 * BigDecimal舍入模式（Rounding mode）介绍：
 * BigDecimal定义了一下舍入模式，只有在作除法运算或四舍五入时才用到舍入模式，下面简单介绍，
 * ROUND_CEILING 向正无穷方向舍入
 * ROUND_DOWN 向零方向舍入
 * ROUND_FLOOR 向负无穷方向舍入
 * ROUND_HALF_DOWN 向（距离）最近的一边舍入，除非两边（的距离）是相等,如果是这样，向下舍入,例如1.55保留一位小数结果为1.5
 * ROUND_HALF_EVEN 向（距离）最近的一边舍入，除非两边（的距离）是相等,如果是这样，如果保留位数是奇数，使用ROUND_HALF_UP，如果是偶数，使用ROUND_HALF_DOWN
 * ROUND_HALF_UP 向（距离）最近的一边舍入，除非两边（的距离）是相等,如果是这样，向上舍入,1.55保留一位小数结果为1.6
 * ROUND_UNNECESSARY 计算结果是精确的，不需要舍入模式
 * ROUND_UP 向远离0的方向舍入
 */
public class MathExtend {

//默认除法运算精度


    private static final int DEFAULT_DIV_SCALE = 10;


    /**
     * 提供精确的加法运算。
     *
     * @param v1
     * @param v2
     * @return两个参数的和
     */


    public static double add(double v1, double v2) {

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.add(b2).doubleValue();

    }

    /**
     * 提供精确的加法运算
     *
     * @param v1
     * @param v2
     * @return两个参数数学加和，以字符串格式返回
     */


    public static String add(String v1, String v2) {

        BigDecimal b1 = new BigDecimal(v1);

        BigDecimal b2 = new BigDecimal(v2);

        return b1.add(b2).toString();

    }


    /**
     * 提供精确的减法运算。
     *
     * @param v1
     * @param v2
     * @return两个参数的差
     */


    public static double subtract(double v1, double v2) {

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.subtract(b2).doubleValue();

    }


    /**
     * 提供精确的减法运算
     *
     * @param v1
     * @param v2
     * @return两个参数数学差，以字符串格式返回
     */


    public static String subtract(String v1, String v2) {

        BigDecimal b1 = new BigDecimal(v1);

        BigDecimal b2 = new BigDecimal(v2);

        return b1.subtract(b2).toString();

    }


    /**
     * 提供精确的乘法运算。
     *
     * @param v1
     * @param v2
     * @return两个参数的积
     */
    public static double multiply(double v1, double v2) {

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.multiply(b2).doubleValue();

    }


    /**
     * 提供精确的乘法运算
     *
     * @param v1
     * @param v2
     * @return两个参数的数学积，以字符串格式返回
     */
    public static String multiply(String v1, String v2) {

        BigDecimal b1 = new BigDecimal(v1);

        BigDecimal b2 = new BigDecimal(v2);

        return b1.multiply(b2).toString();

    }


    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * <p>
     * 小数点以后10位，以后的数字四舍五入,舍入模式采用ROUND_HALF_EVEN
     *
     * @param v1
     * @param v2
     * @return两个参数的商
     */
    public static double divide(double v1, double v2) {

        return divide(v1, v2, DEFAULT_DIV_SCALE);

    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * <p>
     * 定精度，以后的数字四舍五入。舍入模式采用ROUND_HALF_EVEN
     *
     * @param v1
     * @param v2
     * @param scale表示需要精确到小数点以后几位。
     * @return两个参数的商
     */
    public static double divide(double v1, double v2, int scale) {

        return divide(v1, v2, scale, BigDecimal.ROUND_HALF_EVEN);

    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * <p>
     * 定精度，以后的数字四舍五入。舍入模式采用用户指定舍入模式
     *
     * @param v1
     * @param v2
     * @param scale表示需要精确到小数点以后几位
     * @param round_mode表示用户指定的舍入模式
     * @return两个参数的商
     */
    public static double divide(double v1, double v2, int scale, int round_mode) {

        if (scale < 0) {

            throw new IllegalArgumentException("The scale must be a positive integer or zero");

        }

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.divide(b2, scale, round_mode).doubleValue();

    }


    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * <p>
     * 小数点以后10位，以后的数字四舍五入,舍入模式采用ROUND_HALF_EVEN
     *
     * @param v1
     * @param v2
     * @return两个参数的商，以字符串格式返回
     */
    public static String divide(String v1, String v2) {

        return divide(v1, v2, DEFAULT_DIV_SCALE);

    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * <p>
     * 定精度，以后的数字四舍五入。舍入模式采用ROUND_HALF_EVEN
     *
     * @param v1
     * @param v2
     * @param scale表示需要精确到小数点以后几位
     * @return两个参数的商，以字符串格式返回
     */
    public static String divide(String v1, String v2, int scale) {

        return divide(v1, v2, DEFAULT_DIV_SCALE, BigDecimal.ROUND_HALF_EVEN);

    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * <p>
     * 定精度，以后的数字四舍五入。舍入模式采用用户指定舍入模式
     *
     * @param v1
     * @param v2
     * @param scale表示需要精确到小数点以后几位
     * @param round_mode表示用户指定的舍入模式
     * @return两个参数的商，以字符串格式返回
     */
    public static String divide(String v1, String v2, int scale, int round_mode) {

        if (scale < 0) {

            throw new IllegalArgumentException("The scale must be a positive integer or zero");

        }

        BigDecimal b1 = new BigDecimal(v1);

        BigDecimal b2 = new BigDecimal(v2);

        return b1.divide(b2, scale, round_mode).toString();

    }


    /**
     * 提供精确的小数位四舍五入处理,舍入模式采用ROUND_HALF_EVEN
     *
     * @param v需要四舍五入的数字
     * @param scale小数点后保留几位
     * @return四舍五入后的结果
     */
    public static double round(double v, int scale) {

        return round(v, scale, BigDecimal.ROUND_HALF_EVEN);

    }

    /**
     * 提供精确的小数位四舍五入处理
     *
     * @param v需要四舍五入的数字
     * @param scale小数点后保留几位
     * @param round_mode指定的舍入模式
     * @return四舍五入后的结果
     */
    public static double round(double v, int scale, int round_mode) {

        if (scale < 0) {

            throw new IllegalArgumentException("The scale must be a positive integer or zero");

        }

        BigDecimal b = new BigDecimal(Double.toString(v));

        return b.setScale(scale, round_mode).doubleValue();

    }


    /**
     * 提供精确的小数位四舍五入处理,舍入模式采用ROUND_HALF_EVEN
     *
     * @param v需要四舍五入的数字
     * @param scale小数点后保留几位
     * @return四舍五入后的结果，以字符串格式返回
     */
    public static String round(String v, int scale) {

        return round(v, scale, BigDecimal.ROUND_HALF_EVEN);

    }

    /**
     * 提供精确的小数位四舍五入处理
     *
     * @param v需要四舍五入的数字
     * @param scale小数点后保留几位
     * @param round_mode指定的舍入模式
     * @return四舍五入后的结果，以字符串格式返回
     */
    public static String round(String v, int scale, int round_mode) {

        if (scale < 0) {

            throw new IllegalArgumentException("The scale must be a positive integer or zero");

        }

        BigDecimal b = new BigDecimal(v);

        return b.setScale(scale, round_mode).toString();

    }

}