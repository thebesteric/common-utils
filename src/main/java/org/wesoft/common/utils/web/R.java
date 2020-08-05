package org.wesoft.common.utils.web;

import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Response 工具类
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-01-23 15:24
 */
@NoArgsConstructor
public class R extends LinkedHashMap<String, Object> {

    private static final HttpStatusCode SUCCESS_CODE = HttpStatusCode.SUCCESS;
    private static final HttpStatusCode ERROR_CODE = HttpStatusCode.ERROR;
    private static final String CODE_KEY = "code";
    private static final String OBJECT_KEY = "object";
    private static final String DATE_KEY = "date";
    private static final String MESSAGE_KEY = "message";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public enum HttpStatusCode {
        CONTINUE(100, "CONTINUE", "继续"), CHANGE_PROTOCOL(101, "CHANGE PROTOCOL", "切换协议"),
        SUCCESS(200, "SUCCEED", "成功"), CREATED(201, "CREATED", "已创建"), ACCEPTED(202, "ACCEPTED", "已接受"),
        AUTH_ILLEGALITY(203, "AUTH ILLEGALITY", "非授权信息"),
        EMPTY_CONTENT(204, "EMPTY CONTENT", "无内容"), RESET_CONTENT(205, "RESET CONTENT", "重置内容"),
        PART_CONTENT(206, "PART CONTENT", "部分内容"),
        MANY_CHOICE(300, "MANY CHOICE", "多种选择"), FOREVER_MOVED(301, "FOREVER MOVED", "永久移动"), TEMP_MOVED(302, "TEMP MOVED", "临时移动"),
        LOOK_FOR_OTHER(303, "LOOK FOR OTHER", "查看其他位置"), UN_MODIFY(304, "UN MODIFY", "未修改"),
        USE_PROXY(305, "USE PROXY", "使用代理"), TEMP_REDIRECT(307, "TEMP REDIRECT", "临时重定向"),
        REQUEST_ERROR(400, "REQUEST ERROR", "错误请求"), UN_AUTH(401, "UN AUTH", "未授权"), VALIDATE_ERROR(402, "VALIDATE_ERROR", "校验错误"),
        FORBIDDEN(403, "FORBIDDEN", "禁止"), NOT_FOUND(404, "NOT FOUND", "未找到"),
        METHOD_NOT_ALLOW(405, "METHOD NOT ALLOW", "方法禁用"), UN_ACCEPT(406, "UN ACCEPT", "不接受"),
        NEED_PROXY_AUTH(407, "NEED PROXY AUTH", "需要代理授权"), TIMEOUT(408, "TIMEOUT", "请求超时"),
        CONFLICT(409, "CONFLICT", "冲突"), DELETED(410, "DELETED", "已删除"), LENGTH_INVALID(411, "LENGTH INVALID", "需要有效长度"),
        UNSATISFIED_PRECONDITION(412, "UNSATISFIED PRECONDITION", "未满足前提条件"), BODY_TOO_LONG(413, "BODY_TOO_LONG", "请求实体过大"),
        URI_TOO_LONG(414, "URI TOO LONG", "请求的 URI 过长"), UN_SUPPORT_MEDIA_MIME(414, "UN SUPPORT MEDIA MIME", "不支持的媒体类型"),
        SCOPE_INVALID(416, "SCOPE INVALID", "请求范围不符合要求"), UN_SATISFY_EXPECT(417, "UN SATISFY EXPECT", "未满足期望值"),
        ERROR(500, "ERROR", "failed"), UN_IMPLEMENTS(501, "UN IMPLEMENTS", "尚未实施"), GATEWAY_ERROR(502, "GATEWAY ERROR", "错误网关"),
        NOT_AVAILABLE(503, "NOT AVAILABLE", "服务不可用"), GATEWAY_TIMEOUT(504, "GATEWAY TIMEOUT", "网关超时"), UN_SUPPORT_PROTOCOL(505, "UN SUPPORT PROTOCOL", "HTTP 版本不受支持");

        int code;
        String message;
        String memo;

        HttpStatusCode(int code, String message, String memo) {
            this.code = code;
            this.message = message;
            this.memo = memo;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getMemo() {
            return memo;
        }
    }

    public static R newInstance() {
        R r = new R();
        r.put(R.OBJECT_KEY, new LinkedHashMap<>());
        return r;
    }

    @SuppressWarnings("unchecked")
    public R set(String key, Object value) {
        Map<String, Object> object = (Map<String, Object>) super.get(R.OBJECT_KEY);
        object.put(key, value);
        return this;
    }

    public R setCode(HttpStatusCode httpStatusCode) {
        return this.put(R.CODE_KEY, httpStatusCode.code).put(R.MESSAGE_KEY, R.SUCCESS_CODE.message);
    }

    public R setMessage(String message) {
        return this.put(R.MESSAGE_KEY, message);
    }

    public R setCodeAndMessage(HttpStatusCode httpStatusCode, String message) {
        return this.put(R.CODE_KEY, httpStatusCode.code).put(R.MESSAGE_KEY, message != null ? message : httpStatusCode.message);
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public R setCharacterEncoding(HttpServletRequest request, HttpServletResponse response, String encode) throws UnsupportedEncodingException {
        request.setCharacterEncoding(encode);
        response.setCharacterEncoding(encode);
        return this;
    }

    public R setCharacterEncoding(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        setCharacterEncoding(request, response, "UTF-8");
        return this;
    }

    public static R success(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        return success().setCharacterEncoding(request, response);
    }


    public static R success() {
        return R.newInstance().put(R.CODE_KEY, R.SUCCESS_CODE.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, R.SUCCESS_CODE.message);
    }

    public static R success(HttpStatusCode httpStatusCode) {
        return R.newInstance().put(R.CODE_KEY, httpStatusCode.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, httpStatusCode.message);
    }

    public static R success(HttpStatusCode httpStatusCode, String message) {
        return R.newInstance().put(R.CODE_KEY, httpStatusCode.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, message);
    }

    public static R success(String message) {
        return R.newInstance().put(R.CODE_KEY, R.SUCCESS_CODE.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, message);
    }

    public static R success(Object object) {
        return R.newInstance().put(R.CODE_KEY, R.SUCCESS_CODE.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, R.SUCCESS_CODE.message).put(R.OBJECT_KEY, object);
    }

    public static R success(String message, Object object) {
        return R.newInstance().put(R.CODE_KEY, R.SUCCESS_CODE.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, message).put(R.OBJECT_KEY, object);
    }

    public static R error(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        return error().setCharacterEncoding(request, response);
    }

    public static R error() {
        return R.newInstance().put(R.CODE_KEY, R.ERROR_CODE.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, R.ERROR_CODE.message);
    }

    public static R error(String message) {
        return R.newInstance().put(R.CODE_KEY, R.ERROR_CODE.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, message);
    }

    public static R error(HttpStatusCode httpStatusCode) {
        return R.newInstance().put(R.CODE_KEY, httpStatusCode.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, httpStatusCode.message);
    }

    public static R error(HttpStatusCode httpStatusCode, String message) {
        return R.newInstance().put(R.CODE_KEY, httpStatusCode.code)
                .put(R.DATE_KEY, LocalDateTime.now(ZoneOffset.of("+8")).format(formatter)).put(R.MESSAGE_KEY, message);
    }

}
