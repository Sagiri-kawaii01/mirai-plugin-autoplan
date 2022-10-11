package cn.cimoc.mirai.plugin.bili.util;

import cn.cimoc.mirai.plugin.bili.pojo.QR;
import cn.cimoc.mirai.plugin.bili.pojo.QRCode;
import cn.cimoc.mirai.plugin.constant.URLConstant;
import cn.cimoc.mirai.plugin.util.HttpUtil;
import cn.cimoc.mirai.plugin.util.JsonUtil;
import cn.cimoc.mirai.plugin.util.QRCodeUtil;
import net.mamoe.mirai.contact.Contact;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

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
}
