package com.thfw.base.models;

import com.thfw.base.utils.NumberUtil;

import java.math.BigDecimal;

/**
 * Author:pengs
 * Date: 2022/12/4 14:46
 * Describe:Todo
 */
public class GuPiaoModel {

    public String code;
    public String market;
    public String company;

    /**
     * 以~分割字符串	string	根据数字判断
     * 1	string	股票名字
     * 2	string	股票代码
     * 3	string	当前价格
     * 4	string	昨收
     * 5	string	今开
     * 6	string	成交量（手）
     * 7	string	外盘
     * 8	string	内盘
     * 9	string	买一
     * 10	string	买一量（手）
     * 11～18	string	买二 买五
     * 19～20	string	卖一
     * 20	string	卖一量
     * 21～28	string	卖二 卖五
     * 29	string	最近逐笔成交
     * 30	string	时间
     * 31	string	涨跌
     * 32	string	涨跌%
     * 33	string	最高
     * 34	string	最低
     * 35	string	价格/成交量（手）/成交额
     * 36	string	成交量（手）
     * 37	string	成交额（万）
     * 38	string	换手率
     * 39	string	市盈率
     * 40～41	string	最高
     * 42	string	最低
     * 43	string	涨幅
     * 44	string	流通市值
     * 45	string	总市值
     * 46	string	市净率
     * 47	string	涨停价
     * 48	string	跌停价
     */
    public String[] data;

    public String requestGp() {
        return market + code;
    }

    public String all() {
        return company + "(" + market + code + ")";
    }

    public String ttsAll() {
        boolean isZhiShu = company.contains("指数") || company.contains("成指");
        return company + "(" + market + code + (isZhiShu ? ")，当前：" : ")，当前价格：") + data[3]
                + (isZhiShu ? "点，涨跌：" : "元，涨跌：") + data[32]
                + "%，总市值：" + NumberUtil.amountConversion(new BigDecimal(data[45])) + "亿元。";
    }

    public static class MarketType {
        // 香港
        public static final String HK = "hk";
        // 上海
        public static final String SH = "sh";
        // 深证
        public static final String SZ = "sz";
    }
}
