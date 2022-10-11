package cn.cimoc.mirai.plugin.bili.pojo;

import lombok.Data;
import lombok.ToString;

/**
 * @author LGZ
 * <p>
 */
@ToString
@Data
public class QR {
    Integer code;
    Boolean status;
    Long ts;
    QRData data;

    @Data
    public class QRData {
        String url;
        String oauthKey;
    }
}
