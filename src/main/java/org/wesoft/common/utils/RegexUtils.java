package org.wesoft.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-01-23 15:24
 */
public class RegexUtils {

    /**
     * 是否是图片
     *
     * @param value 值
     */
    public static boolean isImage(String value) {
        String regex = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$";
        return matcher(regex, value);
    }

    /**
     * 是否是音频
     *
     * @param value 值
     */
    public static boolean isAudio(String value) {
        String regex = ".+(.AMR|.amr|.MP3|.mp3|.WMA|.wma|.WAV|.wav|.ASF|.asf|.MID|.mid)$";
        return matcher(regex, value);
    }

    /**
     * 是否是数字
     *
     * @param value 值
     */
    public static boolean isNumber(String value) {
        String regex = "^[0-9]*$";
        return matcher(regex, value);
    }

    /**
     * 是否是N位数字
     *
     * @param value 值
     */
    public static boolean isNumber(String value, int number) {
        String regex = "^\\d{" + number + "}$";
        return matcher(regex, value);
    }

    /**
     * 是否是英文字母
     *
     * <pre>
     * 只能输入由26个英文字母组成的字符串
     * </pre>
     *
     * @param value 值
     */
    public static boolean isLetter(String value) {
        String regex = "^[A-Za-z]+$";
        return matcher(regex, value);
    }

    /**
     * 是否是字符串（不含特殊字符）
     *
     * <pre>
     * 只能输入由数字、26个英文字母或者下划线组成的字符串
     * </pre>
     *
     * @param value 值
     */
    public static boolean isCharacter(String value) {
        String regex = "^\\w+$";
        return matcher(regex, value);
    }

    /**
     * 是否是汉字
     *
     * @param value 值
     */
    public static boolean isChineseCharacter(String value) {
        String regex = "^[\u4e00-\u9fa5]{0,}$";
        return matcher(regex, value);
    }

    /**
     * 是否是身份证号
     *
     * @param value 值
     */
    public static boolean isIdCardNumber(String value) {
        String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
        return matcher(regex, value);
    }

    /**
     * 是否是email
     *
     * @param value 值
     */
    public static boolean isEmail(String value) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return matcher(regex, value);
    }

    /**
     * 验证手机号码
     *
     * @param value 值
     */
    public static boolean isMobile(String value) {
        String regex = "(\\+\\d+)?1[34578]\\d{9}$";
        return matcher(regex, value.trim());
    }

    /**
     * 验证固定电话号码
     *
     * @param value 值
     */
    public static boolean isPhone(String value) {
        String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
        return Pattern.matches(regex, value);
    }

    /**
     * 匹配正则
     *
     * @param regex 正则表达式
     * @param value 值
     */
    public static boolean matcher(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

}
