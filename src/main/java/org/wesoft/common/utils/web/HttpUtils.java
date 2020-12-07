package org.wesoft.common.utils.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 工具类
 *
 * @author Eric Joe
 * @version Ver 1.1
 * @build 2020-01-23 15:17
 */
public class HttpUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static ConnectionKeepAliveStrategy keepAliveStrategy;

    private static CloseableHttpClient sslHttpClient;
    private static CloseableHttpClient httpClient;

    private HttpUtils() {
        initPool(HttpUtilsConfig.builder().build());
    }

    public HttpUtils(HttpUtilsConfig config) {
        initPool(config);
    }

    public static HttpUtils getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static HttpUtils instance = new HttpUtils();
    }

    @Builder
    public static class HttpUtilsConfig {
        @Builder.Default
        private int maxTotal = 200;
        @Builder.Default
        private int maxPreRoute = 50;
        @Builder.Default
        private int connectTimeout = 5000;
        @Builder.Default
        private int socketTimeout = 10000;
        @Builder.Default
        private int connectionRequestTimeout = 2000;
        @Builder.Default
        private int validateAfterInactivity = 30000;
    }

    private void initPool(HttpUtilsConfig config) {
        connMgr = new PoolingHttpClientConnectionManager(); // 设置连接池
        connMgr.setMaxTotal(config.maxTotal);  // 设置整个连接池最大连接数
        connMgr.setDefaultMaxPerRoute(config.maxPreRoute); // 设置每个主机地址的并发数
        connMgr.setValidateAfterInactivity(config.validateAfterInactivity);
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(config.connectTimeout); // 设置连接超时
        configBuilder.setSocketTimeout(config.socketTimeout); // 设置读取超时
        configBuilder.setConnectionRequestTimeout(config.connectionRequestTimeout); // 设置从连接池获取连接实例的超时
        requestConfig = configBuilder.build();

        keepAliveStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 5 * 1000;
        };
    }

    /**
     * 获取 CloseableHttpClient
     *
     * @param url URL
     * @return CloseableHttpClient
     */
    private CloseableHttpClient getCloseableHttpClient(String url) {
        if (url.startsWith("https")) {
            if (sslHttpClient == null)
                sslHttpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                        .setConnectionManager(connMgr).setConnectionManagerShared(true).setDefaultRequestConfig(requestConfig)
                        .setKeepAliveStrategy(keepAliveStrategy).build();
            return sslHttpClient;
        } else {
            if (httpClient == null)
                httpClient = HttpClients.custom().setConnectionManager(connMgr).setConnectionManagerShared(true)
                        .setDefaultRequestConfig(requestConfig)
                        .setKeepAliveStrategy(keepAliveStrategy).build();
            return httpClient;
        }
    }

    /**
     * 发送 GET 请求
     *
     * @param url url
     */
    public JSONObject doGet(String url) {
        return doGet(url, new HashMap<>(), null);
    }


    public JSONObject doGet(String url, Map<String, String> headers) {
        return doGet(url, null, headers);
    }

    /**
     * 发送 GET 请求
     *
     * @param url    url
     * @param params 参数
     * @return JSONObject
     */
    public JSONObject doGet(String url, Map<String, Object> params, Map<String, String> headers) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        if (params != null) {
            int i = 0;
            for (String key : params.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(params.get(key));
                i++;
            }
        }
        apiUrl += param;
        String httpStr = null;
        int statusCode = 0;
        CloseableHttpClient httpClient = getCloseableHttpClient(apiUrl);

        HttpGet httpGet = new HttpGet(apiUrl);
        httpGet.setConfig(requestConfig);
        try {
            if (headers != null && headers.size() > 0) {
                headers.forEach(httpGet::setHeader);
            }

            HttpResponse response = httpClient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream inputStream = entity.getContent()) {
                    httpStr = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                httpGet.releaseConnection();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return handleJsonObject(statusCode, httpStr);
    }

    /**
     * 发送 POST 请求
     *
     * @param url url
     * @return JSONObject
     */
    public JSONObject doPost(String url) {
        return doPost(url, new HashMap<>(), new HashMap<>());
    }

    /**
     * 发送 POST 请求
     *
     * @param url     url
     * @param params  params
     * @param headers headers
     */
    public JSONObject doPost(String url, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = getCloseableHttpClient(url);
        String httpStr = null;
        int statusCode = 0;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;

        if (params == null) {
            params = new HashMap<>();
        }

        try {
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, StandardCharsets.UTF_8));

            if (headers != null && headers.size() > 0) {
                headers.forEach(httpPost::setHeader);
            }

            response = httpClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            httpPost.releaseConnection();
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                    httpClient.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return handleJsonObject(statusCode, httpStr);
    }

    /**
     * 发送 POST 请求
     *
     * @param url       url
     * @param jsonArray JSONArray 对象
     */
    public JSONObject doPost(String url, JSONArray jsonArray) {
        return doPost(url, jsonArray.toJSONString(), null);
    }

    /**
     * 发送 POST 请求
     *
     * @param url        url
     * @param jsonObject JSONObject
     */
    public JSONObject doPost(String url, JSONObject jsonObject) {
        return doPost(url, jsonObject.toJSONString(), null);
    }

    /**
     * 发送 POST 请求
     *
     * @param url       url
     * @param jsonArray JSONObject
     * @param headers   headers
     */
    public JSONObject doPost(String url, JSONArray jsonArray, Map<String, String> headers) {
        return doPost(url, jsonArray.toJSONString(), headers);
    }

    /**
     * 发送 POST 请求
     *
     * @param url        url
     * @param jsonObject JSONObject
     * @param headers    headers
     */
    public JSONObject doPost(String url, JSONObject jsonObject, Map<String, String> headers) {
        return doPost(url, jsonObject.toJSONString(), headers);
    }

    /**
     * 发送 POST 请求
     *
     * @param url      url
     * @param paramKey 参数名
     * @param in       输入流
     * @param fileName 文件名称
     */
    public JSONObject doPost(String url, String paramKey, InputStream in, String fileName) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(paramKey, in, ContentType.MULTIPART_FORM_DATA, fileName);
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            return JSON.parseObject(result);
        }
        return null;
    }

    /**
     * 发送 POST 请求
     *
     * @param url     url
     * @param json    JSONObject
     * @param headers headers
     */
    private JSONObject doPost(String url, Object json, Map<String, String> headers) {
        CloseableHttpClient httpClient = getCloseableHttpClient(url);
        String httpStr = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        int statusCode = 0;
        try {
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8"); // 解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);

            if (headers != null && headers.size() > 0) {
                headers.forEach(httpPost::setHeader);
            }

            response = httpClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            httpPost.releaseConnection();
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                    httpClient.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return handleJsonObject(statusCode, httpStr);
    }

    private static JSONObject handleJsonObject(int code, String httpStr) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSON.parseObject(httpStr);
            jsonObject.put("code", code);
        } catch (Exception ex) {
            jsonObject.put("source", httpStr);
        }
        return jsonObject;
    }

    /**
     * 下载文件
     *
     * @param url url
     */
    public static InputStream download(String url) {
        InputStream in = null;
        HttpURLConnection http;
        try {
            URL urlGet = new URL(url);
            http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
            System.setProperty("sun.net.client.defaultReadTimeout", "30000");
            http.connect();
            in = http.getInputStream();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return in;
    }

    /**
     * 上传文件
     *
     * @param url      上传地址
     * @param filename 参数
     * @param file     文件
     */
    public JSONObject upload(String url, String filename, File file) {
        CloseableHttpClient httpClient = getCloseableHttpClient(url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        String httpStr = null;
        int statusCode = 0;
        try {
            FileBody fileBody = new FileBody(file);
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart(filename, fileBody).build();
            httpPost.setEntity(reqEntity);
            response = httpClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            httpPost.releaseConnection();
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                    httpClient.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return handleJsonObject(statusCode, httpStr);
    }

    /**
     * 编码 UTF-8
     *
     * @param value 需要编码的值
     * @return String
     */
    public static String encode(String value) {
        return encode(value, StandardCharsets.UTF_8);
    }

    /**
     * 编码 UTF-8
     *
     * @param value   需要编码的值
     * @param charset 编码方式
     * @return String
     */
    public static String encode(String value, Charset charset) {
        String encodeValue = null;
        try {
            encodeValue = URLEncoder.encode(value, charset.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return encodeValue;
    }

    /**
     * 解码
     *
     * @param value 需要解码的值
     * @return String
     */
    public static String decode(String value) {
        return decode(value, StandardCharsets.UTF_8);
    }

    /**
     * 解码
     *
     * @param value   需要解码的值
     * @param charset 解码方式
     * @return String
     */
    public static String decode(String value, Charset charset) {
        String decodeValue = null;
        try {
            decodeValue = URLDecoder.decode(value, charset.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return decodeValue;
    }

    /**
     * 添加 Cookie
     *
     * @param response 请求
     * @param name     名称
     * @param value    值
     * @param path     路径
     * @param domain   域
     * @param age      过期时间
     */
    public static void addCookie(HttpServletResponse response, String name, String value, String path, String domain,
                                 int age) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    /**
     * 获取 Cookie
     *
     * @param request 请求
     * @param name    名称
     * @return Cookie
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = getCookies(request);
        Cookie cookie = null;
        try {
            if (cookies != null && cookies.length > 0) {
                for (Cookie ck : cookies) {
                    if (ck.getName().equals(name)) {
                        cookie = ck;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cookie;
    }

    /**
     * 获取 Cookies
     *
     * @param request 请求
     * @return Cookie[]
     */
    public static Cookie[] getCookies(HttpServletRequest request) {
        return request.getCookies();
    }

    /**
     * 获取 Cookie 值
     *
     * @param request 请求
     * @param name    名称
     * @return String
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        String value = "";
        if (name != null) {
            Cookie cookie = getCookie(request, name);
            if (cookie != null) {
                value = cookie.getValue();
            }
        }
        return value;
    }

    /**
     * 删除 Cookie
     *
     * @param request  请求
     * @param response 响应
     * @param name     名称
     * @param path     路径
     * @param domain   域
     * @return boolean
     */
    public static boolean deleteCookie(HttpServletRequest request, HttpServletResponse response, String name,
                                       String path, String domain) {
        boolean flag = false;
        Cookie cookie = getCookie(request, name);
        if (cookie != null) {
            addCookie(response, name, null, path, domain, 0);
            flag = true;
        }
        return flag;
    }

    /**
     * 获取主机地址
     *
     * @return String
     */
    public static String getHostAddress() {
        String hostAddress = "";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return hostAddress;
    }

    /**
     * 获取主机名
     *
     * @return String
     */
    public static String getHostName() {
        String hostName = "";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return hostName;
    }

    /**
     * 获取request中的请求参数组成的字符串
     *
     * @param request 请求
     * @return ?key1=value1&key2=value2&...
     */
    public static String getRequestParameterStr(HttpServletRequest request) {
        StringBuilder parameterStr = new StringBuilder();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (int i = 0; i < parameterMap.size(); i++) {
            List<String> list = new ArrayList<>(parameterMap.keySet());
            String key = list.get(i);
            String value = request.getParameter(key);
            if (i == 0) {
                parameterStr = new StringBuilder("?" + key + "=" + value);
            } else {
                parameterStr.append("&").append(key).append("=").append(value);
            }
        }
        return parameterStr.toString();
    }

    /**
     * 获取客户端IP
     *
     * @param request 请求
     * @return String
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 创建SSL安全连接
     *
     * @return SSLConnectionSocketFactory
     */
    private SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslFactory = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true).build();
            sslFactory = new SSLConnectionSocketFactory(sslContext, (String arg0, SSLSession arg1) -> true);
        } catch (GeneralSecurityException e) {
            logger.error(e.getMessage(), e);
        }
        return sslFactory;
    }
}
