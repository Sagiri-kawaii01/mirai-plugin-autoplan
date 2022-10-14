package cn.cimoc.mirai.plugin.util;

import net.mamoe.mirai.console.command.CommandSender;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/** HttpUtil
 * @author xiaochi
 */
public class HttpUtil {

    public static final String JSON_TYPE = "application/json";

    public static final String X_WWW_FORM_TYPE = "application/x-www-form-urlencoded";

    public static final String FORM_DATA_TYPE = "application/form-data";

    private String cookie = "";

    private final Map<String, String> headers = new HashMap<>();

    private final Duration timeout = Duration.ofSeconds(5);
    private final byte[] lock = new byte[0];
    private volatile HttpClient httpClient = null;

    private HttpUtil(){
        if (httpClient == null){
            synchronized (lock){
                if (httpClient == null){
                    httpClient = HttpClient.newBuilder()
                            .version(HttpClient.Version.HTTP_2)
                            .connectTimeout(timeout)
                            .followRedirects(HttpClient.Redirect.NEVER)
                            .sslContext(sslContext())
                            .proxy(ProxySelector.getDefault())
                            .build();
                }
            }
        }
    }

    /**
     * 创建 HttpUtil
     */
    public static HttpUtil builder(){
        return new HttpUtil();
    }

    public HttpUtil setCookie(String key, Object value) {
        cookie += key + "=" + value + ";";
        return this;
    }

    public void header(String k, String v) {
        headers.put(k, v);
    }

    /**
     * get请求
     * @param url 地址
     */
    public HttpResponse<String> get(String url) throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .header("Cookie", cookie)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37");
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                b.header(header.getKey(), header.getValue());
            }
            // 每个用户的HttpUtil都是独立的，而计划任务中所有请求都是阻塞式，不存在并发，不会有并发安全性问题
            headers.clear();
        }
        HttpRequest request = b.version(HttpClient.Version.HTTP_2)
                .uri(URI.create(url))
                .GET()
                .timeout(timeout)
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }



    public HttpResponse<String> get(String url, Map<String, Object> params) throws IOException, InterruptedException {
        if (params.isEmpty()) {
            return get(url);
        }
        StringBuilder builder = new StringBuilder(url);
        builder.append('?');
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }
        return get(builder.toString());
    }

    /**
     * post请求
     * @param url 地址
     */
    public HttpResponse<String> post(String url) throws IOException, InterruptedException {
        return post(url,"");
    }

    public HttpResponse<String> post(String url, String data) throws IOException, InterruptedException {
        return post(url, data, JSON_TYPE);
    }

    public static String postParamsForXWWW(Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> item : params.entrySet()) {
            builder.append(item.getKey()).append('=').append(item.getValue()).append('&');
        }
        return builder.toString();
    }

    /**
     * post请求
     * @param url 地址
     * @param data json字符串
     */
    public HttpResponse<String> post(String url, String data, String type) throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .headers("Referer", "https://www.bilibili.com/")
                .header("Content-Type", type)
                .header("Cookie", cookie)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37");
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                b.header(header.getKey(), header.getValue());
            }

            // 每个用户的HttpUtil都是独立的，而计划任务中所有请求都是阻塞式，不存在并发，不会有并发安全性问题
            headers.clear();
        }
        HttpRequest request = b.version(HttpClient.Version.HTTP_2)
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(data, Charset.defaultCharset()))
                .timeout(timeout)
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static Map<String,String> getCookies(HttpResponse<String> response) {
        Map<String,String> cookieContainer = new HashMap<>();
        HttpHeaders headers = response.headers();
        for (String cookie : headers.allValues("Set-Cookie")) {
            int from = 0, cur, mid;
            while ((cur = cookie.indexOf(';', from)) != -1) {
                mid = cookie.indexOf('=', from);
                if (mid != -1 && mid < cur) {
                    cookieContainer.put(cookie.substring(from, mid), cookie.substring(mid + 1, cur));
                }
                from = cur + 2;
            }
            mid = cookie.indexOf('=', from);
            if (mid != -1 && from < cookie.length()) {
                cookieContainer.put(cookie.substring(from, mid), cookie.substring(mid + 1));
            }
        }
        return cookieContainer;
    }

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     */
    private SSLContext sslContext(){
        TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}
                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustManagers, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sc;
    }
}