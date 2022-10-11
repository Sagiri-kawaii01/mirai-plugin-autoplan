package cn.cimoc.mirai.plugin.util;

import cn.cimoc.mirai.plugin.AutoPlanPlugin;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.permission.AbstractPermitteeId;
import net.mamoe.mirai.utils.MiraiLogger;

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
import java.util.List;
import java.util.Map;

/** HttpUtil
 * @author xiaochi
 */
public class HttpUtil {

    public static final String JSON_TYPE = "application/json";

    public static final String X_WWW_FORM_TYPE = "application/x-www-form-urlencoded";

    public static final String FORM_DATA_TYPE = "application/form-data";

    private String cookie = "";

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

    public HttpUtil setCookie(String key, String value) {
        cookie += key + "=" + value + ";";
        return this;
    }

    /**
     * get请求
     * @param url 地址
     */
    public HttpResponse<String> get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .headers("Cookie", cookie)
                .version(HttpClient.Version.HTTP_2)
                .uri(URI.create(url))
                .GET()
                .timeout(timeout)
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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

    /**
     * post请求
     * @param url 地址
     * @param data json字符串
     */
    public HttpResponse<String> post(String url, String data, String type) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", type)
                .headers("Cookie", cookie)
                .version(HttpClient.Version.HTTP_2)
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
                if (mid != -1) {
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