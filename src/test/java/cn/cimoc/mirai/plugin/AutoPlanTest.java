package cn.cimoc.mirai.plugin;


import cn.cimoc.mirai.plugin.bili.data.Cards;
import cn.cimoc.mirai.plugin.bili.data.UserData;
import cn.cimoc.mirai.plugin.bili.util.BiliTaskUtil;
import cn.cimoc.mirai.plugin.bili.util.BiliWebUtil;
import cn.cimoc.mirai.plugin.bili.util.VideoUtil;
import cn.cimoc.mirai.plugin.constant.URLConstant;
import cn.cimoc.mirai.plugin.util.HttpUtil;
import cn.cimoc.mirai.plugin.util.LogUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author LGZ
 * <p>
 */
public class AutoPlanTest {

    long dedeuserid = 391252241;

    String bili_jct = "1df64224b7b60d5adbca3d809285794b";

    String sessdata = "606022a6%2C1681261545%2C3e748*a1";

    String bvid = "BV1id4y1i7fY";

    BiliWebUtil webUtil;

    @Before
    public void test() throws Exception {
        HttpUtil http = HttpUtil.builder().setCookie("DedeUserID", "391252241").setCookie("bili_jct", bili_jct).setCookie("SESSDATA", sessdata);
        UserData userData = new UserData(dedeuserid);
        userData.dedeUserId.set(dedeuserid);
        userData.bilijct.set(bili_jct);
        userData.sessdata.set(sessdata);
//        List<Cards> cards = VideoUtil.getDynamicNew(http, userData);
//        System.out.println(cards.get(0));
//        System.out.println(VideoUtil.regionRanking(http));
//        System.out.println(VideoUtil.getVideo("BV1id4y1i7fY", http));
        webUtil = new BiliWebUtil();
        webUtil.setHttp(http);
        webUtil.setUserData(userData);
        webUtil.setLog(new LogUtil());
//        VideoUtil.watchVideo(bvid, webUtil);
    }

    @Test
    public void test2() throws Exception {
        HttpUtil http = HttpUtil.builder().setCookie("DedeUserID", dedeuserid).setCookie("bili_jct", bili_jct).setCookie("SESSDATA", sessdata);
        System.out.println(http.get(URLConstant.BILI_GET_COIN_BALANCE).body());
    }

    @Test
    public void cookie() {
        Map<String, String> cookieContainer = new HashMap<>();
        List<String> cookies = new ArrayList<>();
        cookies.add("SESSDATA=f531b3b1%2C1681206765%2C63118*a1; Domain=.bilibili.com; Expires=Tue, 11-Apr-2023 09:52:45 GMT; Path=/; Secure; HttpOnly; SameSite=None");
        for (String cookie : cookies) {
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
    }

    @Test
    public void test3() {
//        BiliTaskUtil.run(webUtil);
        webUtil.log.appendLog("%.1f", 123.2);
        System.out.println(webUtil.log.getLog());
    }



}
