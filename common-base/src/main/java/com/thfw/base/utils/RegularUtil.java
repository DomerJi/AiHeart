package com.thfw.base.utils;

import android.text.TextUtils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtil {

    // 网址 前端匹配 https://www.baidu.com/Sl4/ir9bsXacHaDev/QmKQ== replaceAll Sl4/ir9bsXacHaDev/QmKQ==
    public static final String REGEX_HTTP = "^(https?:\\/\\/)[a-z0-9_\\\\.-]{2,20}\\/";

    //身份证
    public static final String REGEX_ID_CARD = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";

    //验证邮箱
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    //手机号
    public static final String REGEX_PHONE = "0?(13|14|15|16|17|18|19)[0-9]{9}";

    // 数字
    public static final String REGEX_NUMBER = "^[0-9]*$";

    // 数字和字母
    public static final String REGEX_NUMBER_ABC = "^[a-z0-9A-Z]*$";

    // 字母
    public static final String REGEX_ABC = "^[a-zA-Z]*$";

    //汉字
    public static final String REGEX_TRUENAME = "^[\\u4e00-\\u9fa5]*$";

    //汉字
    public static final String REGEX_TRUENAME_ABC = "^[a-zA-Z\\u4e00-\\u9fa5]*$";
    public static final String REGEX_TRUENAME_ABC123 = "^[a-z0-9A-Z\\u4e00-\\u9fa5]*$";

    //银行卡
    public static final String BANKNUMBER = "^([0-9]{16}|[0-9]{19})$";

    //固话电话正则
    public static final String TELE = "([0-9]{3,4}-)?[0-9]{7,8}";

    //密码强度验证
    public static final String REGEX_PWS = "^(((?=.*[0-9])(?=.*[a-zA-Z])|(?=.*[0-9])(?=.*[^\\s0-9a-zA-Z])|(?=.*[a-zA-Z])(?=.*[^\\s0-9a-zA-Z]))[^\\s]+)$";

    public static boolean isPhone(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(REGEX_PHONE);
        }
    }

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean contains(String peopleNmae) {
        return ("张学友，张杰，许嵩，刀郎，徐良，六哲，冷漠，武艺，周杰伦，陈奕迅，李代沫，魏晨，汪苏泷，" +
                "郑源，庾澄庆，刘欢，小沈阳，林志炫，金志文，张震岳，林宥嘉，林俊杰，杨宗纬，" +
                "李行亮，韩磊，胡夏，黄渤，薛之谦，孙楠，张宇，汪峰，单色凌，王力宏，罗志祥（小猪），" +
                "刘德华，萧敬腾，欧汉声（欧弟），吴克羣，谢霆锋，张卫健，方大同，韩庚，" +
                "庞龙，王杰，胡彦斌，汤潮，古巨基，陶喆，林志颖，黄品冠，张国荣（哥哥），" +
                "后弦，周华健，郑伊健，沙宝亮，陈楚生，张雨生，刘心……女歌手：杨丞琳（妈咪），" +
                "萧亚轩，郭采洁，邓紫棋，庄心妍，王菲，本兮，阿悄，梁静茹，张靓颖，刘忻（小鬼），" +
                "蔡依林，蔡卓妍（阿sa），周笔畅，张韶涵，带泪的鱼，张惠妹，那英，黄小琥，杨幂，" +
                "林心如，姚贝娜，严艺丹，卫兰，林忆莲，丁当，弦子，孙燕姿，曾沛慈，田馥甄，" +
                "范玮琪（范范），戚薇，李玟（Coco），王心凌，金莎，莫文蔚，刘若英，邓丽君，" +
                "蔡健雅，郭静，吉克隽逸，李宇春，刘惜君，陈小春").contains(peopleNmae);
    }

    public static String getRandomPeople() {
        String[] peopleNames = ("张学友，张学友，张学友，张杰，许嵩，刀郎，张杰，许嵩，刀郎，徐良，六哲，冷漠，武艺，周杰伦，陈奕迅，周杰伦，陈奕迅，李代沫，魏晨，汪苏泷，" +
                "郑源，庾澄庆，刘欢，刘欢，小沈阳，林志炫，金志文，张震岳，林宥嘉，林俊杰，杨宗纬，" +
                "李行亮，韩磊，胡夏，黄渤，薛之谦，孙楠，张宇，汪峰，单色凌，王力宏，罗志祥（小猪），" +
                "刘德华，萧敬腾，刘德华，萧敬腾，欧汉声（欧弟），吴克羣，谢霆锋，张卫健，方大同，韩庚，" +
                "庞龙，王杰，胡彦斌，汤潮，古巨基，陶喆，林志颖，黄品冠，张国荣（哥哥），张国荣（哥哥），张国荣（哥哥），" +
                "后弦，周华健，郑伊健，周华健，郑伊健，沙宝亮，陈楚生，张雨生，刘心……女歌手：杨丞琳（妈咪），" +
                "萧亚轩，郭采洁，邓紫棋，庄心妍，王菲，王菲，王菲，本兮，阿悄，梁静茹，张靓颖，张靓颖，刘忻（小鬼），" +
                "蔡依林，蔡卓妍（阿sa），周笔畅，张韶涵，带泪的鱼，张惠妹，张惠妹，那英，黄小琥，杨幂，" +
                "林心如，姚贝娜，严艺丹，卫兰，林忆莲，丁当，弦子，孙燕姿，曾沛慈，田馥甄，" +
                "范玮琪（范范），戚薇，李玟（Coco），王心凌，金莎，莫文蔚，刘若英，邓丽君，" +
                "蔡健雅，郭静，吉克隽逸，李宇春，刘惜君，陈小春").split("，");
        return peopleNames[new Random().nextInt(peopleNames.length)];
    }

    public static boolean isTel(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(TELE);
        }
    }


    public static boolean isIDacard(String idcard) {
        if (TextUtils.isEmpty(idcard)) return false;
        else return idcard.matches(REGEX_ID_CARD);
    }

    public static boolean isBankCard(String bankcard) {
        if (TextUtils.isEmpty(bankcard)) return false;
        else return bankcard.matches(BANKNUMBER);
    }

    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) return false;
        else return email.matches(REGEX_EMAIL);
    }

    public static boolean isTrueName(String name) {
        if (TextUtils.isEmpty(name)) return false;
        else return name.matches(REGEX_TRUENAME);
    }

    /**
     * 验证码格式
     *
     * @param verificationCode
     * @return
     */
    public static boolean isVerificationCode(String verificationCode) {
        if (TextUtils.isEmpty(verificationCode)) {
            return false;
        } else {
            if (verificationCode.length() == 6) {
                return verificationCode.matches(REGEX_NUMBER);
            }
            return false;
        }
    }

    public static boolean isNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            return number.matches(REGEX_NUMBER);
        }
    }

    /**
     * 姓名
     */
    public static boolean isRealName(String realName) {
        if (TextUtils.isEmpty(realName)) {
            return false;
        } else {
            // 大于1个字
            return realName.length() > 1;
        }
    }

    /**
     * 验证密码强度
     */
    public static boolean isPassword(String psw) {
        // 包含数字，字母，特殊符号其中至少两种
        if (TextUtils.isEmpty(psw)) {
            return false;
        } else {
            // 限制长度
            if (psw.length() < 8 || psw.length() > 16) {
                return false;
            }
            // matches():字符串是否在给定的正则表达式匹配
            return psw.matches(REGEX_PWS);
        }
    }

}