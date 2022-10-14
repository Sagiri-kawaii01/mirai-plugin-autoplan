package cn.cimoc.mirai.plugin.bili.util;

import cn.cimoc.mirai.plugin.bili.data.UserData;
import cn.cimoc.mirai.plugin.util.HttpUtil;
import cn.cimoc.mirai.plugin.util.LogUtil;
import lombok.Data;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * @author LGZ
 * <p>
 */
@Data
public class BiliWebUtil {
    public HttpUtil http;
    public UserData userData;
    public LogUtil log;

    public void header(String key, String value) {
        http.header(key, value);
    }

    public HttpResponse<String> get(String url) throws IOException, InterruptedException {
        http.header("Referer", "https://www.bilibili.com/");
        return http.get(url);
    }

    public HttpResponse<String> get(String url, Map<String, Object> params) throws IOException, InterruptedException {
        http.header("Referer", "https://www.bilibili.com/");
        return http.get(url, params);
    }

    public HttpResponse<String> post(String url) throws IOException, InterruptedException {
        http.header("Referer", "https://www.bilibili.com/");
        return http.post(url,"");
    }

    public HttpResponse<String> post(String url, String data) throws IOException, InterruptedException {
        http.header("Referer", "https://www.bilibili.com/");
        return http.post(url, data);
    }

    public HttpResponse<String> post(String url, String data, String type) throws IOException, InterruptedException {
        http.header("Referer", "https://www.bilibili.com/");
        return http.post(url, data, type);
    }
}
