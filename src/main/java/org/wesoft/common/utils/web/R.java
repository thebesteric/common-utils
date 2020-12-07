package org.wesoft.common.utils.web;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * Response 工具类
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-01-23 15:24
 */

@SuppressWarnings("all")
@NoArgsConstructor
public class R extends HashMap<String, Object> {

    private static final String CODE_KEY = "code";
    private static final String DATA_KEY = "data";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String MESSAGE_KEY = "message";
    private static final String ZONE_OFFSET = "+8";
    private static final String TRACK_ID = "track_id";

    /* if need track_id you can use reflect */
    public R setTrackId(String trackId) {
        this.put(TRACK_ID, trackId);
        return this;
    }

    public static R newInstance() {
        return new R().put(DATA_KEY, new HashMap<String, Object>());
    }

    public Object getData() {
        return this.get(DATA_KEY);
    }

    public R set(String key, Object value) {
        Map<String, Object> object = (Map) super.get(DATA_KEY);
        if (object == null) {
            object = new HashMap<>();
            super.put(DATA_KEY, object);
        }
        object.put(key, value);
        return this;
    }

    public R put(String key, Object data) {
        super.put(key, data);
        return this;
    }

    public R setCode(int code) {
        return this.put(CODE_KEY, code).put(MESSAGE_KEY, HttpStatusCode.SUCCESS.message);
    }

    public R setCode(HttpStatusCode httpStatusCode) {
        return setCode(httpStatusCode.code);
    }

    public R setMessage(String message) {
        return this.put(MESSAGE_KEY, message);
    }

    public R setData(Object data) {
        return this.put(DATA_KEY, data);
    }

    public R setCodeAndMessage(HttpStatusCode httpStatusCode, String message) {
        return this.put(CODE_KEY, httpStatusCode.code).put(MESSAGE_KEY, null != message ? message : httpStatusCode.message);
    }

    public static R success() {
        return success(HttpStatusCode.SUCCESS);
    }

    public static R success(String message) {
        return success(HttpStatusCode.SUCCESS, message);
    }

    public static R success(Object data) {
        return success(null, data);
    }

    public static R success(HttpStatusCode httpStatusCode) {
        return success(httpStatusCode, null);
    }

    public static R success(HttpStatusCode httpStatusCode, String message) {
        return success(httpStatusCode, message, null);
    }

    public static R success(String message, Object data) {
        return success(HttpStatusCode.SUCCESS, message, data);
    }

    public static R success(HttpStatusCode httpStatusCode, String message, Object data) {
        return success(httpStatusCode.code, null != message ? message : httpStatusCode.message, data);
    }

    public static R success(int code, String message, Object data) {
        return newInstance().put(CODE_KEY, code)
                .put(TIMESTAMP_KEY, LocalDateTime.now().toInstant(ZoneOffset.of(ZONE_OFFSET)).toEpochMilli())
                .put(MESSAGE_KEY, null != message ? message : null)
                .put(DATA_KEY, null != data ? data : null);
    }

    public static R error() {
        return error(HttpStatusCode.ERROR);
    }

    public static R error(String message) {
        return error(HttpStatusCode.ERROR, message);
    }

    public static R error(Object data) {
        return error(HttpStatusCode.ERROR, null, data);
    }

    public static R error(String message, Object data) {
        return error(HttpStatusCode.ERROR, message, data);
    }

    public static R error(HttpStatusCode httpStatusCode) {
        return error(httpStatusCode, httpStatusCode.getMessage());
    }

    public static R error(HttpStatusCode httpStatusCode, String message) {
        return error(httpStatusCode, message, null);
    }

    public static R error(HttpStatusCode httpStatusCode, String message, Object data) {
        return error(httpStatusCode.code, null != message ? message : httpStatusCode.message, data);
    }

    public static R error(int code, String message, Object data) {
        return newInstance().put(CODE_KEY, code)
                .put(TIMESTAMP_KEY, LocalDateTime.now().toInstant(ZoneOffset.of(ZONE_OFFSET)).toEpochMilli())
                .put(MESSAGE_KEY, null != message ? message : null)
                .put(DATA_KEY, null != data ? data : null);
    }

    public static enum HttpStatusCode {
        CONTINUE(100, "CONTINUE", "继续"),
        CHANGE_PROTOCOL(101, "CHANGE_PROTOCOL", "切换协议"),
        SUCCESS(200, "SUCCEED", "执行成功"),
        CREATED(201, "CREATED", "已创建"),
        ACCEPTED(202, "ACCEPTED", "已接受"),
        AUTH_ILLEGALITY(203, "AUTH_ILLEGALITY", "非授权信息"),
        EMPTY_CONTENT(204, "EMPTY_CONTENT", "无内容"),
        RESET_CONTENT(205, "RESET_CONTENT", "重置内容"),
        PART_CONTENT(206, "PART_CONTENT", "部分内容"),
        MANY_CHOICE(300, "MANY_CHOICE", "多种选择"),
        FOREVER_MOVED(301, "FOREVER_MOVED", "永久移动"),
        TEMP_MOVED(302, "TEMP_MOVED", "临时移动"),
        LOOK_FOR_OTHER(303, "LOOK_FOR_OTHER", "查看其他位置"),
        UN_MODIFY(304, "UN_MODIFY", "未修改"),
        USE_PROXY(305, "USE_PROXY", "使用代理"),
        TEMP_REDIRECT(307, "TEMP_REDIRECT", "临时重定向"),
        BAD_REQUEST(400, "BAD_REQUEST", "错误请求"),
        UN_AUTH(401, "UN_AUTHORIZATION", "未授权"),
        VALIDATE_ERROR(402, "VALIDATE_ERROR", "校验错误"),
        FORBIDDEN(403, "FORBIDDEN", "禁止"),
        NOT_FOUND(404, "NOT_FOUND", "未找到"),
        METHOD_NOT_ALLOW(405, "METHOD_NOT_ALLOW", "方法禁用"),
        UN_ACCEPT(406, "UN_ACCEPT", "不接受"),
        NEED_PROXY_AUTH(407, "NEED_PROXY_AUTH", "需要代理授权"),
        TIMEOUT(408, "TIMEOUT", "请求超时"),
        CONFLICT(409, "CONFLICT", "冲突"),
        DELETED(410, "DELETED", "已删除"),
        LENGTH_INVALID(411, "LENGTH_INVALID", "需要有效长度"),
        UNSATISFIED_PRECONDITION(412, "UNSATISFIED_PRECONDITION", "未满足前提条件"),
        BODY_TOO_LONG(413, "BODY_TOO_LONG", "请求实体过大"),
        URI_TOO_LONG(414, "URI_TOO_LONG", "请求的 URI 过长"),
        UN_SUPPORT_MEDIA_MIME(414, "UN_SUPPORT_MEDIA_MIME", "不支持的媒体类型"),
        SCOPE_INVALID(416, "SCOPE_INVALID", "请求范围不符合要求"),
        UN_SATISFY_EXPECT(417, "UN_SATISFY_EXPECT", "未满足期望值"),
        ERROR(500, "ERROR", "执行失败"),
        UN_IMPLEMENTS(501, "UN IMPLEMENTS", "尚未实施"),
        GATEWAY_ERROR(502, "GATEWAY_ERROR", "错误网关"),
        NOT_AVAILABLE(503, "NOT_AVAILABLE", "服务不可用"),
        GATEWAY_TIMEOUT(504, "GATEWAY_TIMEOUT", "网关超时"),
        UN_SUPPORT_PROTOCOL(505, "UN_SUPPORT_PROTOCOL", "HTTP 版本不受支持");

        int code;
        String message;
        String memo;

        private HttpStatusCode(int code, String message, String memo) {
            this.code = code;
            this.message = message;
            this.memo = memo;
        }

        public int getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }

        public String getMemo() {
            return this.memo;
        }
    }
}
