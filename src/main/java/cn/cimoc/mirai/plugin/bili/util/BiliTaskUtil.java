package cn.cimoc.mirai.plugin.bili.util;

import cn.cimoc.mirai.plugin.AutoPlanPlugin;
import cn.cimoc.mirai.plugin.bili.data.*;
import cn.cimoc.mirai.plugin.bili.pojo.QR;
import cn.cimoc.mirai.plugin.bili.pojo.QRCode;
import cn.cimoc.mirai.plugin.constant.URLConstant;
import cn.cimoc.mirai.plugin.util.HttpUtil;
import cn.cimoc.mirai.plugin.util.JsonUtil;
import cn.cimoc.mirai.plugin.util.LogUtil;
import cn.cimoc.mirai.plugin.util.QRCodeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.contact.Contact;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * @author LGZ
 * <p>
 */
public class BiliTaskUtil {
    public static QRCode getLoginQR(Contact contact) throws IOException, InterruptedException {
        QR qr = JsonUtil.json2Bean(HttpUtil.builder().get(URLConstant.BILI_QRCODE_URL).body(), QR.class);
        if (0 == qr.getCode()) {
            BufferedImage image = QRCodeUtil.createImage(qr.getData().getUrl());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            return new QRCode(qr.getData().getOauthKey(), Contact.uploadImage(contact, in));
        }
        throw new RuntimeException("请求失败");
    }

    public static Map<String, String> getQRCodeStatus(String oauthKey) throws IOException, InterruptedException {
        HttpResponse<String> response = HttpUtil.builder().post(URLConstant.BILI_QRCODE_STATUS_URL, "oauthKey=" + oauthKey, HttpUtil.X_WWW_FORM_TYPE);
        HashMap<String, Object> map = JsonUtil.json2Map(response.body());
        if (null == map || !map.containsKey("status")) {
            throw new RuntimeException("获取状态失败");
        }
        Boolean status = (Boolean) map.get("status");
        if (!status) {
            throw new RuntimeException(map.get("message").toString());
        }
        Map<String, String> cookies = HttpUtil.getCookies(response);
        Map<String, String> result = new HashMap<>();
        result.put("DedeUserID", cookies.get("DedeUserID"));
        result.put("SESSDATA", cookies.get("SESSDATA"));
        result.put("bili_jct", cookies.get("bili_jct"));
        return result;
    }

    public static HttpUtil getHttpUtilWithCookies(UserData userData) {
        return HttpUtil.builder()
                .setCookie("DedeUserId", userData.dedeUserId.get())
                .setCookie("bili_jct", userData.bilijct.get())
                .setCookie("SESSDATA", userData.sessdata.get());
    }

    public static boolean login(BiliWebUtil webUtil) throws IOException, InterruptedException {
        AutoPlanPlugin.INSTANCE.reloadPluginData(webUtil.userData);
        HttpResponse<String> response = webUtil.get(URLConstant.BILI_LOGIN);
        JSONObject jsonObject = JSON.parseObject(response.body());
        // 登录失败
        if (0 != jsonObject.getInteger("code") || !jsonObject.getJSONObject("data").getBoolean("isLogin")) {
            return false;
        }
        // 登录成功后记录日志
        BiliData data = JSON.toJavaObject(jsonObject.getJSONObject("data"), BiliData.class);
        webUtil.log.appendLog("用户名称：" + data.getUname())
                .appendLog("硬币余额：" + data.getMoney())
                .appendLog("当前等级：Lv%d，当前经验值：%d，下一级需要经验值：%d", data.getLevel_info().getCurrent_level(), data.getLevel_info().getCurrent_exp(), data.getLevel_info().getNext_exp_asInt());
        webUtil.userData.money.set(data.getMoney());
        return true;
    }

    private static void coinLogs(BiliWebUtil webUtil) {
            try {
            JSONObject jsonObject = JSON.parseObject(webUtil.get(URLConstant.BILI_GET_COIN_LOG).body());
            if (jsonObject.getInteger("code") == 0) {
                JSONObject data = jsonObject.getJSONObject("data");
                webUtil.log.appendLog("最近一周共计%s条硬币记录", data.getInteger("count"));
                JSONArray coinList = data.getJSONArray("list");
                double income = 0.0;
                double expend = 0.0;
                for (int i = 0; i < coinList.size(); i++) {
                    double delta = coinList.getJSONObject(i).getDouble("delta");
                    if (delta > 0) {
                        income += delta;
                    } else {
                        expend += delta;
                    }
                }
                webUtil.log.appendLog("最近一周收入%s个硬币", income)
                        .appendLog("最近一周支出%s个硬币", expend);
            }
        } catch (Exception e) {
            webUtil.log.appendLog("获取硬币记录异常：" + e.getMessage());
        }
    }

    public static Reward getReward(BiliWebUtil webUtil) {
        try {
            return JSON.parseObject(webUtil.get(URLConstant.BILI_REWARD).body()).getJSONObject("data").toJavaObject(Reward.class);
        } catch (Exception e) {
            webUtil.log.appendLog("获取经验值记录出现异常");
            return null;
        }
    }

    private static void emailReward(Reward reward, BiliWebUtil webUtil) {
        if (!reward.getEmail()) {
            webUtil.log.appendLog("您还未获取邮箱绑定奖励，请登录bilibili官网自行获取");
        }
    }

    private static void telReward(Reward reward, BiliWebUtil webUtil) {
        if (!reward.getTel()) {
            webUtil.log.appendLog("您还未获取手机号绑定奖励，请登录bilibili官网自行获取");
        }
    }

    private static void safeQuestionReward(Reward reward, BiliWebUtil webUtil) {
        if (!reward.getSafeQuestion()) {
            webUtil.log.appendLog("您还未获取安全问题设置奖励，请登录bilibili官网自行获取");
        }
    }

    private static int watchReward(Reward reward, BiliWebUtil webUtil, List<String> bvids) {
        if (reward.getWatch()) {
            webUtil.log.appendLog("今日视频观看任务已完成");
            return 5;
        } else {
            if (!bvids.isEmpty()) {
                try {
                    VideoUtil.watchVideo(bvids.get(0), webUtil);
                    return 5;
                } catch (Exception e) {
                    webUtil.log.appendLog("视频观看出错：" + e.getMessage());
                }
            }
        }
        return 0;
    }

    private static int coinsReward(Reward reward, BiliWebUtil webUtil, List<String> bvids) {
        int coins = 0;
        if ((coins = reward.getCoins()) == 50) {
            webUtil.log.appendLog("今日投币任务已完成");
            return 50;
        } else {
            if (!bvids.isEmpty()) {
                try {
                    int maxAddCoins = 5;
                    int money = BiliHelperUtil.coinBalance(webUtil);
                    int used = BiliHelperUtil.coinsUsedToday(webUtil);
                    if (used == maxAddCoins) {
                        webUtil.log.appendLog("今日投币任务已完成");
                        return 50;
                    }
                    int target = maxAddCoins - used;
                    webUtil.log.appendLog("投币数调整为：%s枚", target);
                    if (target > money) {
                        webUtil.log.appendLog("完成今日设定投币任务还需要投: %s枚硬币，但是余额只有: %s枚", target, money);
                        target = money;
                        webUtil.log.appendLog("投币数调整为：%s枚", target);
                    }
                    // TODO 后期打算加上预留硬币数的功能

                    //
                    int idx = 0, loop = 0, tmp = target;
                    while (target > 0) {
                        loop++;
                        String bvid = bvids.get(idx++);
                        if (BiliHelperUtil.coinAdd(bvid, 1, true, webUtil)) {
                            target--;
                        }
                        if (loop > 15) {
                            webUtil.log.appendLog("尝试投币/投币失败次数太多");
                            break;
                        }
                    }
                    webUtil.log.appendLog("投币任务完成后余额为: %s", BiliHelperUtil.coinBalance(webUtil));
                    return 10 * (tmp - target);
                } catch (Exception e) {
                    webUtil.log.appendLog("投币出错：" + e.getMessage());
                    return coins;
                }
            }
        }
        return coins;
    }

    private static int shareReward(Reward reward, BiliWebUtil webUtil, List<String> bvids) {
        if (reward.getShare()) {
            webUtil.log.appendLog("今日分享任务已完成");
            return 5;
        } else {
            if (!bvids.isEmpty()) {
                try {
                    VideoUtil.shareVideo(bvids.get(0), webUtil);
                    return 5;
                } catch (Exception e) {
                    webUtil.log.appendLog("视频分享出错：" + e.getMessage());
                }
            }
        }
        return 0;
    }

    @Deprecated
    private static void calculateExp(BiliWebUtil webUtil) {
        Reward reward = getReward(webUtil);
        if (null == reward) {
            return;
        }
        int exp = 0;
        exp += reward.getCoins();
        webUtil.log.appendLog("今日投币获取经验值：%s/50", exp);
        if (reward.getLogin()) {
            webUtil.log.appendLog("今日登录获取经验值：5/5");
            exp += 5;
        } else {
            webUtil.log.appendLog("今日登录获取经验值：0/5");
        }
        if (reward.getShare()) {
            webUtil.log.appendLog("今日视频分享获取经验值：5/5");
            exp += 5;
        } else {
            webUtil.log.appendLog("今日视频分享获取经验值：0/5");
        }
        if (reward.getWatch()) {
            webUtil.log.appendLog("今日观看视频获取经验值：5/5");
            exp += 5;
        } else {
            webUtil.log.appendLog("今日观看视频获取经验值：0/5");
        }
        webUtil.log.appendLog("今日总共获取经验值：%s/65", exp);
    }

    public static void run(BiliWebUtil webUtil) {
        BiliTaskUtil.coinLogs(webUtil);
        Reward reward = BiliTaskUtil.getReward(webUtil);
        if (Objects.isNull(reward)) {
            return;
        }
        List<String> bvids = new ArrayList<>();
        try {
            // TODO 关注up主太少可能动态的视频不够，后期需要加上排行榜
            List<Cards> dynamicNew = VideoUtil.getDynamicNew(webUtil);
            for (Cards cards : dynamicNew) {
                bvids.add(cards.getDesc().getBvid());
            }
        } catch (Exception e) {
            webUtil.log.appendLog("获取动态视频出错：" + e.getMessage());
        }
        emailReward(reward, webUtil);
        telReward(reward, webUtil);
        safeQuestionReward(reward, webUtil);
        int exp = reward.getLogin() ? 5 : 0;
        exp += watchReward(reward, webUtil, bvids);
        exp += coinsReward(reward, webUtil, bvids);
        exp += shareReward(reward, webUtil, bvids);
        webUtil.log.appendLog("今日已获取经验值：%s/65", exp);
    }
}
