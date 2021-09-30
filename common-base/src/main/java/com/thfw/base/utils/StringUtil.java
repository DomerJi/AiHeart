package com.thfw.base.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {
    private static final char[] sWordChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static int[] sPinYinValue = new int[]{-20319, -20317, -20304, -20295, -20292, -20283, -20265, -20257, -20242, -20230, -20051, -20036, -20032, -20026, -20002, -19990, -19986, -19982, -19976, -19805, -19784, -19775, -19774, -19763, -19756, -19751, -19746, -19741, -19739, -19728, -19725, -19715, -19540, -19531, -19525, -19515, -19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275, -19270, -19263, -19261, -19249, -19243, -19242, -19238, -19235, -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006, -19003, -18996, -18977, -18961, -18952, -18783, -18774, -18773, -18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697, -18696, -18526, -18518, -18501, -18490, -18478, -18463, -18448, -18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201, -18184, -18183, -18181, -18012, -17997, -17988, -17970, -17964, -17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752, -17733, -17730, -17721, -17703, -17701, -17697, -17692, -17683, -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427, -17417, -17202, -17185, -16983, -16970, -16942, -16915, -16733, -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470, -16465, -16459, -16452, -16448, -16433, -16429, -16427, -16423, -16419, -16412, -16407, -16403, -16401, -16393, -16220, -16216, -16212, -16205, -16202, -16187, -16180, -16171, -16169, -16158, -16155, -15959, -15958, -15944, -15933, -15920, -15915, -15903, -15889, -15878, -15707, -15701, -15681, -15667, -15661, -15659, -15652, -15640, -15631, -15625, -15454, -15448, -15436, -15435, -15419, -15416, -15408, -15394, -15385, -15377, -15375, -15369, -15363, -15362, -15183, -15180, -15165, -15158, -15153, -15150, -15149, -15144, -15143, -15141, -15140, -15139, -15128, -15121, -15119, -15117, -15110, -15109, -14941, -14937, -14933, -14930, -14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902, -14894, -14889, -14882, -14873, -14871, -14857, -14678, -14674, -14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429, -14407, -14399, -14384, -14379, -14368, -14355, -14353, -14345, -14170, -14159, -14151, -14149, -14145, -14140, -14137, -14135, -14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094, -14092, -14090, -14087, -14083, -13917, -13914, -13910, -13907, -13906, -13905, -13896, -13894, -13878, -13870, -13859, -13847, -13831, -13658, -13611, -13601, -13406, -13404, -13400, -13398, -13395, -13391, -13387, -13383, -13367, -13359, -13356, -13343, -13340, -13329, -13326, -13318, -13147, -13138, -13120, -13107, -13096, -13095, -13091, -13076, -13068, -13063, -13060, -12888, -12875, -12871, -12860, -12858, -12852, -12849, -12838, -12831, -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556, -12359, -12346, -12320, -12300, -12120, -12099, -12089, -12074, -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798, -11781, -11604, -11589, -11536, -11358, -11340, -11339, -11324, -11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041, -11038, -11024, -11020, -11019, -11018, -11014, -10838, -10832, -10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533, -10519, -10331, -10329, -10328, -10322, -10315, -10309, -10307, -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254};

    private static String[] sPinYinString = new String[]{"a", "ai", "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian", "biao", "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai", "chan", "chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu", "cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de", "deng", "di", "dian", "diao", "die", "ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo", "e", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo", "fou", "fu", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo", "ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun", "huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan", "kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia", "lian", "liang", "liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", "lv", "luan", "lue", "lun", "luo", "ma", "mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai", "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang", "niao", "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan", "nue", "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pu", "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", "qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao", "re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan", "shang", "shao", "she", "shen", "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang", "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui", "sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya", "yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang", "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo"};

    private StringUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 字符串解析成int，格式错误则返回默认值
     *
     * @param s            待解析字符串
     * @param defaultValue 格式错误的返回默认值
     * @return
     */
    public static int toInt(String s, int defaultValue) {
        int value = defaultValue;
        if (TextUtils.isEmpty(s)) {
            return value;
        }
        String number = s.trim();

        try {
            value = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            LogUtil.e(e);
            try {
                value = (int) Double.parseDouble(number);
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
        }

        return value;
    }

    /**
     * 字符串解析成int，格式错误则返回0
     *
     * @param s 待解析字符串
     * @return
     */
    public static int toInt(String s) {
        return toInt(s, 0);
    }

    /**
     * 字符串解析成long，格式错误则返回默认值
     *
     * @param s            待解析字符串
     * @param defaultValue 格式错误的返回默认值
     * @return
     */
    public static long toLong(String s, long defaultValue) {
        long value = defaultValue;
        if (TextUtils.isEmpty(s)) {
            return value;
        }
        String number = s.trim();

        try {
            value = Long.parseLong(number);
        } catch (NumberFormatException e) {
            LogUtil.e(e);
            try {
                value = (long) Double.parseDouble(number);
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
        }

        return value;
    }

    /**
     * 字符串解析成long，格式错误则返回0
     *
     * @param s 待解析字符串
     * @return
     */
    public static long toLong(String s) {
        return toLong(s, 0);
    }

    /**
     * 字符串解析成float，格式错误返回默认值
     *
     * @param s            待解析字符串
     * @param defaultValue 格式错误的返回默认值
     * @return
     */
    public static float toFloat(String s, float defaultValue) {
        float value = defaultValue;
        if (TextUtils.isEmpty(s)) {
            return value;
        }
        String number = s.trim();

        try {
            value = Float.parseFloat(number);
        } catch (NumberFormatException e) {
            LogUtil.e(e);
            try {
                value = (float) Double.parseDouble(number);
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
        }

        return value;
    }

    /**
     * 字符串解析成float，格式错误返回0
     *
     * @param s 待解析字符串
     * @return
     */
    public static float toFloat(String s) {
        return toFloat(s, 0);
    }

    /**
     * 字符串解析成double，格式错误转换为默认值
     *
     * @param s            待解析字符串
     * @param defaultValue 格式错误的返回默认值
     * @return
     */
    public static double toDouble(String s, double defaultValue) {
        double value = defaultValue;
        if (TextUtils.isEmpty(s)) {
            return value;
        }
        String number = s.trim();

        try {
            value = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            LogUtil.e(e);
        }

        return value;
    }

    /**
     * 字符串解析成double，格式错误返回0
     *
     * @param s 待解析字符串
     * @return
     */
    public static double toDouble(String s) {
        return toDouble(s, 0);
    }

    /**
     * 将list的数据转换为单个的String
     * 如： {"aaa", "bbb", "ccc"} -> "aaa, bbb, ccc"
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> String fromList(List<T> list) {
        return fromList(list, ",");
    }

    /**
     * 将list的数据转换为单个的String
     * 如： {"aaa", "bbb", "ccc"} -> "aaa, bbb, ccc"
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> String fromList(List<T> list, String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        if (list == null || list.isEmpty()) {
            return stringBuilder.toString();
        }

        int size = list.size();
        for (int i = 0; i < size; i++) {
            T t = list.get(i);
            if (t == null) {
                continue;
            }

            stringBuilder.append(t.toString());
            if (i < size - 1) {
                stringBuilder.append(separator);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 将一定格式的String转换为对象列表
     * 如："aaa, bbb, ccc" ->{"aaa", "bbb", "ccc"}
     *
     * @param listString 符合一定格式的字符串
     * @param separator  列表字符串的分隔符
     * @param builder    对象的构造器
     * @param <T>        返回对象列表的的对象的类型
     * @return
     */
    public static <T> List<T> toList(String listString, String separator, ObjectBuilder<T> builder) {
        List<T> list = new ArrayList<>();

        if (TextUtils.isEmpty(listString)) {
            return list;
        }

        if (TextUtils.isEmpty(separator)) {
            T t = builder.build(listString);
            if (t != null) {
                list.add(t);
            }
            return list;
        }

        String[] split = listString.split(separator);
        if (split.length == 0) {
            return list;
        }

        for (String s : split) {
            if (TextUtils.isEmpty(s)) {
                continue;
            }

            T t = builder.build(s);
            if (t == null) {
                continue;
            }

            list.add(t);
        }

        return list;
    }

    public static <T> List<T> toList(String listString, ObjectBuilder<T> builder) {
        return toList(listString, ",", builder);
    }

    public static List<String> toList(String listString) {
        ObjectBuilder<String> builder = new ObjectBuilder<String>() {
            @Override
            public String build(String s) {
                return s;
            }
        };
        return toList(listString, ",", builder);
    }

    /**
     * 将字符串转换为SpannalbeString，高亮关键字
     *
     * @param key    关键字
     * @param target 待转化字符串
     * @return SpannableString字符串
     */
    public static SpannableString toSpannableString(String key, @NonNull String target) {
        SpannableString spanStr;
        if (key != null && !key.equals("")) {
            spanStr = new SpannableString(target);
            try {
                Pattern pattern = Pattern.compile(key);
                Matcher matcher = pattern.matcher(spanStr);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    spanStr.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } catch (Exception e) {
                LogUtil.e(e);
            }
        } else {
            spanStr = new SpannableString(target);
        }
        return spanStr;
    }

    /**
     * 判断两个字符串的内容是否相同
     */
    public static boolean contentEquals(String s1, String s2) {
        return nullToEmpty(s1).trim().equals(nullToEmpty(s2).trim());
    }

    /**
     * 将null字符串转换为内容为空的字符串
     *
     * @param s 待转换的字符串
     * @return 如果输入的胃null，则返回"",否则返回原字符串
     */
    public static String nullToEmpty(CharSequence s) {
        return s == null ? "" : (s instanceof String) ? (String) s : s.toString();
    }

    /**
     * 对流转化成字符串
     */
    public static String getStringByStream(InputStream is) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString().replaceAll("\n\n", "\n");
        } catch (OutOfMemoryError o) {
            System.gc();
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return null;
    }

    public static String capitalize(String name) {
        char[] charArray = name.toCharArray();

        if (charArray[0] >= 'a' && charArray[0] <= 'z') {
            charArray[0] -= 'a' - 'A';
        }

        return String.valueOf(charArray);
    }

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(e);
        }
        if (messageDigest == null) {
            return "";
        }

        byte[] byteArray = messageDigest.digest();

        StringBuilder stringBuilder = new StringBuilder();

        for (byte b : byteArray) {
            if (Integer.toHexString(0xFF & b).length() == 1) {
                stringBuilder.append("0").append(Integer.toHexString(0xFF & b));
            } else {
                stringBuilder.append(Integer.toHexString(0xFF & b));
            }
        }

        return stringBuilder.toString();
    }

    public static String getRandomString(int length) {
        if (length <= 0) {
            return "";
        }

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(sWordChars.length);
            stringBuilder.append(sWordChars[index]);
        }

        return stringBuilder.toString();
    }

    public static String between(String fullString, String startTag, String endTag) {
        return between(fullString, startTag, endTag, 0);
    }

    public static String between(String fullString, String startTag, String endTag, int fromIndex) {
        int startTagIndex = fullString.indexOf(startTag, fromIndex);
        int contentFromIndex = startTagIndex + startTag.length();
        int endTagIndex = fullString.indexOf(endTag, contentFromIndex);
        if (contentFromIndex > 0 && endTagIndex >= contentFromIndex) {
            return fullString.substring(contentFromIndex, endTagIndex);
        }
        return null;
    }

    /**
     * 判断字符串是否为null或长度为0
     *
     * @param s 待校验字符串
     * @return {@code true}: 空<br> {@code false}: 不为空
     */
    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 判断字符串是否为null或全为空格
     *
     * @param s 待校验字符串
     * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
     */
    public static boolean isSpace(String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * null转为长度为0的字符串
     *
     * @param s 待转字符串
     * @return s为null转为长度为0字符串，否则不改变
     */
    public static String null2Length0(String s) {
        return s == null ? "" : s;
    }

    /**
     * 返回字符串长度
     *
     * @param s 字符串
     * @return null返回0，其他返回自身长度
     */
    public static int length(CharSequence s) {
        return s == null ? 0 : s.length();
    }

    /**
     * 首字母大写
     *
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstLetter(String s) {
        if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param s 待转字符串
     * @return 首字母小写字符串
     */
    public static String lowerFirstLetter(String s) {
        if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * 反转字符串
     *
     * @param s 待反转字符串
     * @return 反转字符串
     */
    public static String reverse(String s) {
        int len = length(s);
        if (len <= 1) {
            return s;
        }
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * 转化为半角字符
     *
     * @param s 待转字符串
     * @return 半角字符串
     */
    public static String toDBC(String s) {
        if (isEmpty(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 转化为全角字符
     *
     * @param s 待转字符串
     * @return 全角字符串
     */
    public static String toSBC(String s) {
        if (isEmpty(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 单个汉字转成ASCII码
     *
     * @param s 单个汉字字符串
     * @return 如果字符串长度是1返回的是对应的ascii码，否则返回-1
     */
    private static int oneCn2ASCII(String s) {
        if (s.length() != 1) {
            return -1;
        }
        int ascii = 0;
        try {
            byte[] bytes = s.getBytes("GB2312");
            if (bytes.length == 1) {
                ascii = bytes[0];
            } else if (bytes.length == 2) {
                int highByte = 256 + bytes[0];
                int lowByte = 256 + bytes[1];
                ascii = (256 * highByte + lowByte) - 256 * 256;
            } else {
                throw new IllegalArgumentException("Illegal resource string");
            }
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(e);
        }
        return ascii;
    }

    /**
     * 单个汉字转成拼音
     *
     * @param s 单个汉字字符串
     * @return 如果字符串长度是1返回的是对应的拼音，否则返回{@code null}
     */
    private static String oneCn2PY(String s) {
        int ascii = oneCn2ASCII(s);
        if (ascii == -1) {
            return null;
        }
        String ret = null;
        if (0 <= ascii && ascii <= 127) {
            ret = String.valueOf((char) ascii);
        } else {
            for (int i = sPinYinValue.length - 1; i >= 0; i--) {
                if (sPinYinValue[i] <= ascii) {
                    ret = sPinYinString[i];
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * 获得第一个汉字首字母
     *
     * @param s 单个汉字字符串
     * @return 拼音
     */
    public static String getPYFirstLetter(String s) {
        if (isSpace(s)) {
            return "";
        }
        String first;
        String py;
        first = s.substring(0, 1);
        py = oneCn2PY(first);
        if (py == null) {
            return null;
        }
        return py.substring(0, 1);
    }

    /**
     * 中文转拼音
     *
     * @param s 汉字字符串
     * @return 拼音
     */
    public static String cn2PY(String s) {
        String hz;
        String py;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            hz = s.substring(i, i + 1);
            py = oneCn2PY(hz);
            if (py == null) {
                py = "?";
            }
            sb.append(py);
        }
        return sb.toString();
    }

    /**
     * 测试一个字符串中是否包含另一个串
     *
     * @param container
     * @param key
     * @return
     */
    public static boolean contains(String container, String key) {
        return !(TextUtils.isEmpty(container) || TextUtils.isEmpty(key)) && container.contains(key);
    }

    /**
     * 测试一个字符串中是否包含另一个串, 不区分大小写
     *
     * @param container
     * @param key
     * @return
     */
    public static boolean containsIgnoreCase(String container, String key) {
        return !(TextUtils.isEmpty(container) || TextUtils.isEmpty(key)) && container.toLowerCase().contains(key.toLowerCase());
    }

    public static String getIndexString(int position) {
        int value = position + 1;

        String text = String.valueOf(value);
        if (value < 10) {
            text = "0" + text;
        }

        return text;
    }

    public static String mobileAddPassword(String mobile) {
        if (!TextUtils.isEmpty(mobile) && mobile.length() == 11) {
            return mobile.substring(0, 3) + "****" + mobile.substring(7, 11);

        }
        return mobile;
    }

    public static String emptyToString(@Nullable CharSequence sequence, String replace) {
        if (sequence != null && !TextUtils.isEmpty(sequence)) {
            if (sequence instanceof String) {
                return (String) sequence;
            } else {
                return sequence.toString();
            }
        }
        return replace;
    }

    public interface ObjectBuilder<T> {
        T build(String s);
    }
}
