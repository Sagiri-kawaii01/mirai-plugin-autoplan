package cn.cimoc.mirai.plugin.bili.data.dataBean;

import lombok.Data;

/**
 * @author LGZ
 * <p>
 */
@Data
public class DynamicDesc {
    /**
     * up主uid
     */
    private Long uid;

    /**
     * bv号
     */
    private String bvid;

    /**
     * up主信息
     */
    private UserProfile userProfile;
}
