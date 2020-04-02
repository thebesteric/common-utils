package org.wesoft.common.utils;

/**
 * 判断为空的工具类
 *
 * @author Eric Joe
 * @version Ver 1.1
 * @build 2020-01-23 15:17
 */
public final class EmptyUtils {

    public static Boolean isEmpty(String param) {
        boolean isEmpty = false;
        if (param == null || param.trim().length() == 0 || param.trim().replaceAll("\"", "").length() == 0) {
            isEmpty = true;
        }

        return isEmpty;
    }

    public static Boolean isEmpty(Integer param) {
        boolean isEmpty = false;
        if (param == null || param == 0) {
            isEmpty = true;
        }

        return isEmpty;
    }

    public static Boolean isEmpty(Long param) {
        boolean isEmpty = false;
        if (param == null || param == 0L) {
            isEmpty = true;
        }

        return isEmpty;
    }

    public static Boolean isEmpty(Double param) {
        boolean isEmpty = false;
        if (param == null || param == 0.0D) {
            isEmpty = true;
        }

        return isEmpty;
    }

    public static Boolean isEmpty(Float param) {
        boolean isEmpty = false;
        if (param == null || param == 0.0F) {
            isEmpty = true;
        }

        return isEmpty;
    }

}
