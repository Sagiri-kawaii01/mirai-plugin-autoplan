package cn.cimoc.mirai.plugin;


import cn.cimoc.mirai.plugin.constant.URLConstant;
import cn.cimoc.mirai.plugin.util.HttpUtil;
import cn.cimoc.mirai.plugin.util.JsonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.console.MiraiConsole;
import net.mamoe.mirai.console.data.PluginData;
import org.junit.Test;

import java.net.URL;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author LGZ
 * <p>
 */
public class AutoPlanTest {

    String dedeuserid = "391252241";

    String bili_jct = "27240410395d1404df08d58f3a9bf6b2";

    String sessdata = "6a72a0ea%2C1680256839%2C9dca6*a1";

    @Test
    public void test() throws Exception {
        HttpUtil http = HttpUtil.builder()
                .setCookie("DedeUserId", dedeuserid)
                .setCookie("bili_jct", bili_jct)
                .setCookie("SESSDATA", sessdata);
        HttpResponse<String> response = http.get(URLConstant.BILI_LOGIN);
        JSONObject json = JSON.parseObject(response.body());
        System.out.println(json.getJSONObject("data").getJSONObject("level_info"));
    }
}
