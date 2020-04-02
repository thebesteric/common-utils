package org.wesoft.common.utils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-01-23 15:24
 */
public class StringUtils {

    private static Pattern getPattern(String regex) {
        return Pattern.compile(regex);
    }

    public static String getCharacter(int index) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String chr = "";
        if (index > chars.length()) {
            return chr;
        }

        chr = chars.substring(index, index + 1);
        return chr;

    }

    public static int getIndex(String str) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int index = 0;
        if (!chars.contains(str)) {
            return index;
        }

        index = chars.indexOf(chars);

        return index;
    }

    public static boolean contentEqual(String str1, String str2) {
        boolean isEq = false;
        if (isEmpty(str1) || isEmpty(str2)) {
            return isEq;
        }

        String newStr1 = str1.trim().toLowerCase();
        String newStr2 = str2.trim().toLowerCase();

        if (newStr1.length() != newStr2.length()) {
            return isEq;
        }

        char[] strChars = newStr1.toCharArray();
        for (int i = 0; i < strChars.length; i++) {
            if (newStr2.indexOf(strChars[i]) == -1) {
                return isEq;
            }
        }

        isEq = true;

        return isEq;
    }

    public static String subStringWithSuffix(String origString, int length, String suffix) {
        String destString = origString;

        if (length >= origString.length() || length < 0) {
            destString = origString;
        } else {
            destString = subString(origString, 0, length) + suffix;
        }

        return destString;
    }

    public static String subString(String origString, int beginIndex, int subStringLength) {
        String destString = "";
        if (isEmpty(origString)) {
            return destString;
        } else {
            origString = trim(origString);
        }

        int length = origString.length();
        if (beginIndex > length || beginIndex < 0) {
            beginIndex = 0;
        }
        if (subStringLength > length || subStringLength < 0) {
            subStringLength = length;
        }

        int endIndex = beginIndex + subStringLength;
        if (endIndex > length) {
            endIndex = length;
        }

        destString = origString.substring(beginIndex, endIndex);

        return destString;
    }

    public static String trim(String origString) {
        String destString = null;
        if (origString != null) {
            destString = origString.trim();
        } else {
            destString = "";
        }

        return destString;

    }

    /**
     * 删除空格、回车、换行符、制表符
     *
     * @param origString
     * @return
     */
    public static String trimBlank(String origString) {
        String destString = null;
        if (origString != null) {
            Matcher m = getPattern("\\s*|\t|\r|\n").matcher(origString);
            destString = m.replaceAll("");
        } else {
            destString = "";
        }

        return destString;

    }

    public static String toUpperCase(String origString) {
        String destString = null;
        if (origString != null) {
            destString = origString.toUpperCase();
        }

        return destString;
    }

    public static String toLowerCase(String origString) {
        String destString = null;
        if (origString != null) {
            destString = origString.toLowerCase();
        }

        return destString;
    }

    public static Integer toInt(String origString) {
        Integer destInt = null;
        String str = trim(origString);
        if (origString != null && isNumeric(str)) {
            destInt = Integer.valueOf(str);
        }

        return destInt;
    }

    public static boolean isNumeric(String str) {
        boolean isNum = false;
        isNum = getPattern("[0-9]*").matcher(str).matches();

        return isNum;
    }

    public static String null2Blank(String origString) {
        String destString = "";
        if (origString == null) {
            destString = "";
        } else {
            destString = origString.trim();
        }

        return destString;
    }

    public static String blank2Null(String origString) {
        String destString = "";
        if (origString.equals("")) {
            destString = null;
        } else {
            destString = origString.trim();
        }

        return destString;
    }

    public static boolean isEmpty(String str) {
        return EmptyUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static List<String> split(String str, String regex) {
        List<String> list = new ArrayList<String>(0);

        String[] arr = new String[0];
        if (str != null) {
            arr = str.split(regex);
        }

        for (String s : arr) {
            if (!s.trim().equals("")) {
                list.add(s.trim());
            }
        }

        return list;
    }

    public static List<String> split(String str) {
        List<String> list = new ArrayList<String>(0);

        String[] arr = new String[0];
        if (str != null) {
            arr = str.split(",|，|;|；|\r\n|\n\r|\n|\r|\t");
        }

        for (String s : arr) {
            if (!s.trim().equals("")) {
                list.add(s);
            }
        }

        return list;
    }

    public static String iso2Utf8(String origString) {
        String utfString = origString;
        if (utfString != null) {
            try {
                utfString = new String(utfString.getBytes("ISO8859-1"), "UTF-8").trim();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            utfString = "";
        }

        return utfString;
    }

    public static String enter2BR(String origString) {
        String destString = "";
        String space10 = "                    ";
        String nbsp5 = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

        if (origString != null) {
            destString = origString.replace("\r\n", "<br />").replace("\r", "<br />").replace("\n", "<br />")
                    .replace(space10, nbsp5);
        }

        return destString;
    }

    public static String spaceEnter2quot(String origString) {
        String destString = "";

        if (origString != null) {
            Matcher m = getPattern("\\&nbsp;|\\s*|\t|\r|\n").matcher(origString);
            destString = m.replaceAll("").trim();

        }

        return destString;
    }

    public static boolean isMatch(String origString, String regex) {
        boolean match = false;

        if (isEmpty(origString)) {
            return match;
        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(origString);

        return m.find();
    }

    /**
     * @param list        字符串列表
     * @param symbol      连接符号
     * @param autoWrapped 是否自动换行
     * @return 连接后的字符串
     */
    public static String join(List<String> list, String symbol, boolean autoWrapped) {
        String destString = "";

        StringBuffer sb = new StringBuffer();
        int size = list.size();
        int joinCount = 0;
        if (size > 0) {
            for (int i = 0; i < size - 1; i++) {
                if (isEmpty(list.get(i))) {
                    continue;
                }

                sb.append(trim(list.get(i))).append(symbol);
                joinCount++;

                if (joinCount % 5 == 0) {
                    sb.append("\r\n");
                }
            }

            sb.append(list.get(size - 1));

            destString = sb.toString();
        }

        return destString;
    }

    /**
     * 生成随机数
     *
     * @param number 生成位数
     * @return
     */
    public static String generateRandomCode(Integer number) {
        String[] digits = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        String returnStr = "";
        Random rnum = new Random(System.currentTimeMillis());
        for (int i = 0; i < number; i++) {
            int index = Math.abs(rnum.nextInt()) % 10;
            String tmpDigit = digits[index];
            returnStr += tmpDigit;
        }
        return returnStr;
    }

    public static String toString(Object obj) {
        String destString = "";
        if (obj != null) {
            destString = obj.toString();
        }

        return destString;
    }

    public static String repalceAll(Object obj) {
        String destString = "";
        if (obj != null) {
            destString = obj.toString();
            Matcher m = getPattern("\\s*|t|r|n").matcher(destString);
            destString = m.replaceAll("");
        }

        return destString;
    }

    /**
     * serializable转String
     *
     * @param serializable
     * @return
     */
    public static String serializable2String(Serializable serializable) {
        return serializable.toString();
    }

    /**
     * serializable转String
     *
     * @param serializables
     * @return
     */
    public static String serializable2String(List<Serializable> serializables) {
        StringBuffer sb = new StringBuffer();
        for (Serializable serializable : serializables) {
            sb.append("'").append(serializable).append("',");
        }
        return sb.substring(0, sb.length() - 1).toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param str        下划线形式字符串
     * @param smallCamel 大小驼峰（默认：小）
     * @return
     */
    public static String underline2Camel(String str, boolean... smallCamel) {
        if (str == null || "".equals(str)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher = pattern.matcher(str);
        // 匹配正则表达式
        while (matcher.find()) {
            String word = matcher.group();
            // 当是true 或则是空的情况
            if ((smallCamel.length == 0 || smallCamel[0]) && matcher.start() == 0) {
                sb.append(Character.toLowerCase(word.charAt(0)));
            } else {
                sb.append(Character.toUpperCase(word.charAt(0)));
            }

            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param str       驼峰形式字符串
     * @param upperCase 是否全部大写（默认：是）
     * @return
     */
    public static String camel2Underline(String str, boolean... upperCase) {
        if (str == null || "".equals(str)) {
            return "";
        }
        str = String.valueOf(str.charAt(0)).toUpperCase().concat(str.substring(1));
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String word = matcher.group();
            if (upperCase != null && upperCase.length > 0 && !upperCase[0]) {
                sb.append(word);
            } else {
                sb.append(word.toUpperCase());
            }
            sb.append(matcher.end() == str.length() ? "" : "_");
        }
        return sb.toString();
    }
}
