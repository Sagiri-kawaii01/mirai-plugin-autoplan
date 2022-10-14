package cn.cimoc.mirai.plugin.bili.data;

import cn.cimoc.mirai.plugin.bili.data.dataBean.DynamicCard;
import cn.cimoc.mirai.plugin.bili.data.dataBean.DynamicDesc;
import lombok.Data;

/**
 * @author LGZ
 * <p>
 */
@Data
public class Cards {
    DynamicDesc desc;

    DynamicCard card;
}
