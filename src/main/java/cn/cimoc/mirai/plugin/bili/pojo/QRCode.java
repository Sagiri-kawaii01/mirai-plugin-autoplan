package cn.cimoc.mirai.plugin.bili.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.message.data.Image;

/**
 * @author LGZ
 * <p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QRCode {
    String oauthKey;
    Image qrCode;
}
