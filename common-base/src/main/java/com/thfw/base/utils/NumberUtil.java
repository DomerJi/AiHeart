package com.thfw.base.utils;

import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * Created By jishuaipeng on 2021/6/7
 */
public class NumberUtil {

    public static final String ZERO = "0.00";
    public static final String ZERO_1 = "0.0";

    public static String getPrice(double price) {
        if (price == 0f) {
            return ZERO;
        }
        return new java.text.DecimalFormat("##0.00").format(price);
    }

    public static String getPeople(float people) {
        if (people == 0f) {
            return ZERO_1;
        }
        return new java.text.DecimalFormat("##0.0").format(people);
    }

    public static String getPrice(float price) {
        if (price == 0f) {
            return ZERO;
        }
        return new java.text.DecimalFormat("##0.00").format(price);
    }

    public static String getPrice(String price) {
        if (TextUtils.isEmpty(price)) {
            return ZERO;
        }
        if (ZERO.equals(price) || "0".equals(price) || ".".equals(price)
                || "0.0".equals(price) || ".00".equals(price) || ".0".equals(price)) {
            return ZERO;
        }
        return new java.text.DecimalFormat("##0.00").format(new BigDecimal(price));
    }

    public static String numberToStr(long num) {
        if (num > 10000) {
            return getPeople(num / 10000f) + "万";
        }
        return String.valueOf(num);
    }

    public static String getConfoundAccount(String account) {
        if (TextUtils.isEmpty(account)) {
            return "****";
        }
        int atIndex = account.indexOf("@");
        // 邮箱
        if (atIndex != -1) {
            switch (atIndex) {
                case 0:
                    return account.substring(atIndex, account.length());
                case 1:
                    return "*" + account.substring(atIndex, account.length());
                case 2:
                    return "*" + account.substring(atIndex - 1, account.length());
                case 3:
                    return account.substring(0, 1) + "*" + account.substring(atIndex - 1, account.length());
                case 4:
                    return account.substring(0, 1) + "**" + account.substring(atIndex - 1, account.length());
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append(account.substring(0, 2));
                    for (int i = 2, size = atIndex - 2; i < size; i++) {
                        sb.append("*");
                    }
                    sb.append(account.substring(atIndex - 2, account.length()));
                    return sb.toString();
            }
            // 手机号
        } else if (RegularUtil.isPhone(account)) {
            return account.substring(0, 2) + "******" + account.substring(8, 11);
            // 银行账号
        } else {
            if (account.length() > 4) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, size = account.length() - 4; i < size; i++) {
                    if (sb.length() % 4 == 0 && sb.length() != 0) {
                        sb.append(" *");
                    } else {
                        sb.append("*");
                    }
                }
                sb.append(" " + account.substring(account.length() - 4, account.length()));
                return sb.toString();
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, size = account.length() - 1; i < size; i++) {
                    sb.append("*");
                }
                sb.append(account.substring(account.length() - 1, account.length()));
                return sb.toString();
            }
        }
    }

    public static String toUppercase(int number) {
        switch (number) {
            case 0:
                return "零";
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "七";
            case 8:
                return "八";
            case 9:
                return "九";
            case 10:
                return "十";
            default:
                return String.valueOf(number);

        }
    }

    public static int toUppercase(String number) {
        switch (number) {
            case "零":
                return 0;
            case "一":
                return 1;
            case "二":
                return 2;
            case "三":
                return 3;
            case "四":
                return 4;
            case "五":
                return 5;
            case "六":
                return 6;
            case "七":
                return 7;
            case "八":
                return 8;
            case "九":
                return 9;
            case "十":
                return 10;
            default:
                return -1;
        }
    }


    private static final String MILLION_UNIT = "万";
    private static final String BILLION_UNIT = "亿";
    private static final BigDecimal ONE_HUNDRED_THOUSAND = new BigDecimal(100000);
    private static final BigDecimal ONE_HUNDRED_MILLION = new BigDecimal(100000000);
    private static final BigDecimal TEN_THOUSAND = new BigDecimal(10000);

    /**
     * 将数字转换成以万为单位或者以亿为单位，因为在前端数字太大显示有问题
     * @param amount
     * @return
     */
    public static String amountConversion(BigDecimal amount){
        if (amount == null) {
            return null;
        }
        if (amount.abs().compareTo(ONE_HUNDRED_THOUSAND) < 0) {
            //如果小于10万
            return amount.stripTrailingZeros().toPlainString();
        }
        if (amount.abs().compareTo(ONE_HUNDRED_MILLION) < 0) {
            //如果大于10万小于1亿
            return amount.divide(TEN_THOUSAND, 4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + MILLION_UNIT;
        }
        return amount.divide(ONE_HUNDRED_MILLION, 4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + BILLION_UNIT;
    }

    /**
     * 将数字转换成以亿为单位
     * @param amount
     * @return
     */
    public static Double amountConversionBillion(BigDecimal amount){
        if (amount == null) {
            return null;
        }
        return amount.divide(ONE_HUNDRED_MILLION, 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().doubleValue();
    }


}
