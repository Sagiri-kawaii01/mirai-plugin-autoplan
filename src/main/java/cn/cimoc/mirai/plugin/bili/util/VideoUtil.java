package cn.cimoc.mirai.plugin.bili.util;

import cn.cimoc.mirai.plugin.bili.data.Cards;
import cn.cimoc.mirai.plugin.bili.data.Ranking;
import cn.cimoc.mirai.plugin.bili.data.UserData;
import cn.cimoc.mirai.plugin.bili.data.Video;
import cn.cimoc.mirai.plugin.bili.data.dataBean.DynamicCard;
import cn.cimoc.mirai.plugin.bili.data.dataBean.DynamicDesc;
import cn.cimoc.mirai.plugin.constant.URLConstant;
import cn.cimoc.mirai.plugin.util.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * @author LGZ
 * <p>
 */
public class VideoUtil {
    public static List<Cards> getDynamicNew(BiliWebUtil webUtil) throws IOException, InterruptedException {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", webUtil.userData.dedeUserId.get());
        params.put("type_list", 8);
        params.put("from", "");
        params.put("platform", "web");
        JSONObject json = JSON.parseObject(webUtil.get(URLConstant.BILI_QUERY_DYNAMIC_NEW, params).body());
        if (json.getInteger("code") != 0) {
            throw new RuntimeException("动态获取失败");
        }
        JSONArray jsonArray = json.getJSONObject("data").getJSONArray("cards");
        List<Cards> cards = new ArrayList<>();
        int n = jsonArray.size();
        for (int i = 0; i < n; i++) {
            JSONObject j = jsonArray.getJSONObject(i);
            Cards item = new Cards();
            item.setDesc(j.getJSONObject("desc").toJavaObject(DynamicDesc.class));
            item.setCard(JSON.parseObject(j.getString("card"), DynamicCard.class));
            cards.add(item);
        }
        return cards;
    }

    /**
     * 在有限分区里随机选择
     */
    private static int randomRegion() {
        int[] arr = {1, 3, 4, 5, 160, 22, 119};
        return arr[(int) (Math.random() * arr.length)];
    }

    public static List<Ranking> regionRanking(BiliWebUtil webUtil) throws IOException, InterruptedException {
        int rid = randomRegion();
        int day = 3;
        return regionRanking(rid, day, webUtil);
    }

    public static List<Ranking> regionRanking(int rid, int day, BiliWebUtil webUtil) throws IOException, InterruptedException {
        Map<String, Object> params = new HashMap<>();
        params.put("rid", rid);
        params.put("day", day);
        JSONObject json = JSON.parseObject(webUtil.get(URLConstant.BILI_GET_REGION_RANKING, params).body());
        JSONArray data = json.getJSONArray("data");
        int n = data.size();
        List<Ranking> ret = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ret.add(data.getObject(i, Ranking.class));
        }
        return ret;
    }

    public static Video getVideo(String bvid, BiliWebUtil webUtil) throws IOException, InterruptedException {
        return JSON.parseObject(webUtil.get(URLConstant.BILI_VIDEO_VIEW + "?bvid=" + bvid).body()).getJSONObject("data").toJavaObject(Video.class);
    }

    public static int watchVideo(String bvid, BiliWebUtil webUtil) throws IOException, InterruptedException {
        int playedTime = new Random().nextInt(90) + 1;
        JSONObject json = JSON.parseObject(webUtil.http.post(URLConstant.BILI_VIDEO_HEARTBEAT, "bvid=" + bvid + "&played_time=" + playedTime, HttpUtil.X_WWW_FORM_TYPE).body());
        Video video = getVideo(bvid, webUtil);
        if (json.getInteger("code") != 0) {
            webUtil.log.appendLog("视频: %s播放失败,原因: %s", video.getTitle(), json.getString("message"));
            return 0;
        } else {
            webUtil.log.appendLog("视频: %s播放成功,已观看到第%s秒", video.getTitle(), playedTime);
            return 5;
        }
    }

    public static int shareVideo(String bvid, BiliWebUtil webUtil) throws IOException, InterruptedException {
        JSONObject json = JSON.parseObject(webUtil.http.post(URLConstant.BILI_AV_SHARE, "bvid=" + bvid + "&csrf=" + webUtil.userData.bilijct.get(), HttpUtil.X_WWW_FORM_TYPE).body());
        Video video = getVideo(bvid, webUtil);
        if (0 != json.getInteger("code")) {
            webUtil.log.appendLog("视频分享失败，原因：%s", json.getString("message"));
            return 0;
        }
        webUtil.log.appendLog("视频：%s分享成功", video.getTitle());
        return 5;
    }
}
