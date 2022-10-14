package cn.cimoc.mirai.plugin.bili.util;

import cn.cimoc.mirai.plugin.bili.data.Video;
import cn.cimoc.mirai.plugin.constant.URLConstant;
import cn.cimoc.mirai.plugin.util.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LGZ
 * <p>
 */
public class BiliHelperUtil {
    public static int coinsUsedToday(BiliWebUtil webUtil) throws IOException, InterruptedException {
        JSONObject json = JSON.parseObject(webUtil.get(URLConstant.BILI_NEED_COIN_NEW).body());
        int exp = json.getIntValue("data");
        return exp / 10;
    }

    public static int coinBalance(BiliWebUtil webUtil) throws IOException, InterruptedException {
        JSONObject json = JSON.parseObject(webUtil.get(URLConstant.BILI_GET_COIN_BALANCE).body());
        if (json.getInteger("code") != 0) {
            webUtil.log.appendLog("获取硬币余额失败：%s，使用登录时获取的余额：%s" + json.getString("message"), webUtil.userData.money.get().toString());
            return webUtil.userData.money.get().intValue();
        }
        double money = json.getJSONObject("data").getDouble("money");
        webUtil.log.appendLog("硬币余额：" + money);
        return (int)money;
    }

    public static boolean isCoinAdded(String bvid, BiliWebUtil webUtil) throws IOException, InterruptedException {
        JSONObject json = JSON.parseObject(webUtil.get(URLConstant.BILI_IS_COIN + "?bvid=" + bvid).body());
        if (json.getInteger("code") != 0) {
            webUtil.log.appendLog("投币判断出错：" + json.getString("message"));
            return true;
        }
        int multiply = json.getJSONObject("data").getInteger("multiply");
        if (multiply > 0) {
            webUtil.log.appendLog("之前已经为%s投过%s枚硬币啦", bvid, multiply);
            return true;
        }
        return false;
    }

    /**
     * 投币操作工具类.
     * @param bvid       bv号
     * @param multiply   投币数量
     * @param selectLike 是否同时点赞 1是
     * @return 是否投币成功
     */
    public static boolean coinAdd(String bvid, int multiply, Boolean selectLike, BiliWebUtil webUtil) throws IOException, InterruptedException {
        if (isCoinAdded(bvid, webUtil)) {
            return false;
        }
        webUtil.header("Referer", "https://www.bilibili.com/video/" + bvid);
        webUtil.header("Origin", "https://www.bilibili.com");
        Map<String, Object> params = new HashMap<>();
        params.put("bvid", bvid);
        params.put("multiply", String.valueOf(multiply));
        params.put("select_like", selectLike ? "1" : "0");
        params.put("cross_domain", "true");
        params.put("csrf", webUtil.userData.bilijct.get());
        JSONObject json = JSON.parseObject(webUtil.post(URLConstant.BILI_COIN_ADD, HttpUtil.postParamsForXWWW(params), HttpUtil.X_WWW_FORM_TYPE).body());
        if (0 != json.getInteger("code")) {
            webUtil.log.appendLog("投币失败：" + json.getString("message"));
            return false;
        }
        Video video = VideoUtil.getVideo(bvid, webUtil);
        webUtil.log.appendLog("为视频【%s】%s——%s 投币成功", video.getBvid(), video.getTitle(), video.getOwner().getName());
        return true;
    }
}
